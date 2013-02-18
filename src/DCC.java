import gnu.io.PortInUseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import DataCollector.JUtil;
import DataCollector.DB.MySQLAccess;
import DataCollector.IO.ComPort;
import DataCollector.IO.ComPortEvent;
import DataCollector.IO.ComPortEventListener;
import DataCollector.PV.PV;
import DataCollector.PV.PVEvent;
import DataCollector.PV.PVEventListener;
import DataCollector.XML.D0;
import DataCollector.XML.JSON;
import DataCollector.XML.MYSQL;
import DataCollector.XML.S0;
import DataCollector.XML.XmlConfigParser;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class DCC {
	final static Logger logger = Logger.getRootLogger();
	static Lock lock = new ReentrantLock();
	static MySQLAccess DB;
	final static Object[] DBData = new Object[]{"00:00:00","00-01-01",0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	enum Hardware {AMIS, FROELING}
	//static MySQLAccess DB;

	String logFile = "d0.log";
	static long startTime = System.currentTimeMillis();// nanoTime();
	static long stopTime = System.currentTimeMillis();// System.nanoTime();

	static Object portName = 0;
	static Object baudRate = 300;
	static Object parityBit = ComPort.Parity.EVEN;
	static Object dataBits = ComPort.DataBits.SEVEN;
	static Object stopBits = ComPort.StopBits.ONE;
	static Object interval = 60;
	static Object hardwareType = Hardware.AMIS;

	static XmlConfigParser config = new XmlConfigParser();
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JUtil.setupLogger(args[1]);
		
		config(args);
		//config(new String[]{"./jar/"});
		createThreads();
	}

	private static void createThreads() {
		Thread S0Thread = null;
		Thread D0Thread = null;
		Thread PVThread = null;
		Thread DBThread = null;

		if(S0Thread == null){
			S0Thread = new Thread(){
				@Override
				public void run(){
					Thread thisThread = Thread.currentThread();
					thisThread.setName("S0Thread");
					
					S0 s0 = new S0();
					s0 = config.getS0();
					if(s0 != null)
						createTaskS0(s0);
					else
						logger.error("no valid config for S0 interface found!");
				}
			};
		}

		if(D0Thread == null){
			D0Thread = new Thread(){
				@Override
				public void run(){
					Thread thisThread = Thread.currentThread();
					thisThread.setName("D0Thread");
					
					D0 d0 = new D0();
					d0 = config.getD0();
					if(d0 != null)
						createTaskD0(d0);
					else
						logger.error("no valid config for D0 interface found!");
				}
			};
		}

		if(PVThread == null){
			PVThread = new Thread(){
				@Override
				public void run(){
					Thread thisThread = Thread.currentThread();
					thisThread.setName("PVThread");
					
					JSON json = new JSON();
					json = config.getJson();
					if(json != null)
						createTaskPV(json);
					else
						logger.error("no valid config for JSON interface found!");
				}
			};
		}

		if(DBThread == null){
			DBThread = new Thread(){
				public void run(){
					Thread thisThread = Thread.currentThread();
					thisThread.setName("DBThread");
					
					MYSQL mysql = new MYSQL();
					mysql = config.getMysql();

					createTaskDB(mysql);
					while(true){
						createSocketCommunication(mysql);
						logger.debug("Socket Thread crashed,... try again!");
					}
				}
			};
		}

		S0Thread.start();
		D0Thread.start();
		PVThread.start();
		DBThread.start();
	}

/*	private static void setup() {
		Level logLevel;
		//logLevel = Level.INFO;
		logLevel = Level.DEBUG;
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.setLevel(logLevel);
			logger.addAppender(consoleAppender);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
*/
	private static void config(String[] args) {
		if(args.length > 0){
			//read "config.xml"
			if(config.getConfig(args[0])){
				logger.debug("found valid config file");
			} else{
				logger.error("No valid input found! Try dev environment location");
				//parseArgs(new String[]{"./jar/config.xml"});					
			}		
		}
	}

	private static int flag = 0;
	private static double meanPower = 0;
	private static double meanEnergy = 0;
	private static double totalEnergy = 0;// = offset;
	private static double powerFactor;// = (1000 / ticksPerKWH) * 3600; // Wh between two D0 events

	private static void createTaskS0(final S0 s0) {
		final Object[] S0DbData = new Object[]{0.0,0.0};
		final Lock localLock = new ReentrantLock();
		final double kwhPerTick = (1 / s0.getTicksPerKwh());	// see user manual of energy meter

		totalEnergy = s0.getOffset();
		powerFactor = (1000 / s0.getTicksPerKwh()) * 3600; // Wh between two D0 events
		logger.debug("S0 interface has kwhPerTick: " + kwhPerTick);

		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "S0", s0.getPullResistance());
		Counter.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {

				if(event.getState().toString().equals("HIGH"))
				{
					double deltaTime = System.currentTimeMillis() - startTime;
					startTime = System.currentTimeMillis();

					double currentPower = powerFactor / deltaTime;

					double tmpPower = currentPower * 1000;
					tmpPower = Math.round(tmpPower);
					tmpPower = tmpPower / 1000;

					double tmpEnergy; //= Math.round(totalEnergy * 1000);

					logger.debug("-----> S0 Energy: " + totalEnergy);
					localLock.lock();
					{
						flag++;
						totalEnergy = totalEnergy + kwhPerTick; //kWh between two ticks
						logger.debug("calculation: " + 1 / s0.getTicksPerKwh());
						//						tmpEnergy = Math.round(totalEnergy * 1000);
						tmpEnergy = totalEnergy;
						logger.debug("-----> S0 Energy: " + totalEnergy);
						meanPower = meanPower + tmpPower;
					}
					localLock.unlock();

					String log = (" --> "+ event.getPin() + " = " + event.getState() + ";\t" +
							"time since last event: " + deltaTime + "ms;\t" +
							"current power: " + tmpPower + " kW\t" +
							"total energy: " + tmpEnergy + "kWh");
					logger.debug(log);
					logger.debug("mp=" + meanPower + " me=" + meanEnergy);
				}
			}
		});

		//create timer schedule for S0 interface
		Timer timer_S0 = new Timer();
		long delay_S0 = 0;
		long interval_S0 = s0.getInterval()*1000;
		timer_S0.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.debug("write S0 values to variables!");

				double mp;
				double te;
				localLock.lock();
				{
					mp = (Math.round((meanPower / flag) * 1000));
					te = totalEnergy;
					meanPower = 0;
					flag = 0;
					config.setConfig("s0", "offset", String.valueOf(te));
				}
				localLock.unlock();

				lock.lock();
				{
					S0DbData[0] = mp / 1000;;
					S0DbData[1] = te;;
					for (int i = 0; i < S0DbData.length; i++) {
						DBData[i+14] = S0DbData[i];
						logger.debug("mean values of S0 interface: " + DBData[i + 14].toString());
					}
				}
				lock.unlock();
			}
		}, delay_S0, interval_S0);
	}

	private static void createTaskD0(final D0 d0) {
		final ComPort port = new ComPort();
		try {
			port.connect(d0.getPortName(), d0.getBaudRate(), d0.getParity(), d0.getDataBits(), d0.getStopBits());
		} catch (PortInUseException e) {
			logger.error(e.getMessage());
		}

		//create timer schedule for D0 interface
		Timer timer_D0 = new Timer();
		long delay_D0 = 0;
		long interval_D0 = d0.getInterval()*1000;
		//timer_AMIS.schedule(new ComPortTask(port, portName, baudRate, parityBit, dataBits, stopBits), delay_AMIS, interval_AMIS);
		timer_D0.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					port.portSettings(d0.getPortName(), d0.getBaudRate(), d0.getParity(), d0.getDataBits(), d0.getStopBits());
					//port.portSettings(d0.getPortName(), d0.getBaudRate(), parityBit, dataBits, stopBits);
					port.out.write(new byte[] {0x2f, 0x3f, 0x21, 0x0d, 0x0a});
					port.out.flush();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}, delay_D0, interval_D0);

		port.addComPortEventListener(new ComPortEventListener() {
			Object[] AmisDbData = new Object[10];

			@Override
			public void comPortEventFired(ComPortEvent evt) {

				String str = evt.getSource().toString();
				String rawData = "";

				if(	str.contains("(")|
						str.contains(")")|
						str.contains("!\r\n"))
				{
					String data;
					if(!str.contains("!\r\n"))
						rawData = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
					else
						rawData = str;

					if(rawData.contains("*"))
						data = rawData.substring(0, rawData.indexOf("*"));
					else
						data = rawData.trim();

					if(str.contains("0.9.1("))
						AmisDbData[0] = data;
					else if(str.contains("0.9.2("))
						AmisDbData[1] = data;
					else if(str.contains("F.F("))
						AmisDbData[2] = Double.parseDouble(data);
					else if(str.contains("0.0.0("))
						AmisDbData[3] = Double.parseDouble(data);
					else if(str.contains("1.8.0("))
						AmisDbData[4] = Double.parseDouble(data);
					else if(str.contains("2.8.0("))
						AmisDbData[5] = Double.parseDouble(data);
					else if(str.contains("1.7.0("))
						AmisDbData[6] = Double.parseDouble(data);
					else if(str.contains("2.7.0("))
						AmisDbData[7] = Double.parseDouble(data);
					else if(str.contains("3.7.0("))
						AmisDbData[8] = Double.parseDouble(data);
					else if(str.contains("4.7.0("))
						AmisDbData[9] = Double.parseDouble(data);
					else if(str.startsWith("!\r\n"))
					{
						data = "";
						logger.debug("\r\nfinished cycle\r\n");

						lock.lock();
						logger.info("\tD0 values:");
						for (int i = 0; i < AmisDbData.length; i++) {
							if(AmisDbData[i] != null)
							{
								DBData[i] = AmisDbData[i];
								logger.info("\t\t" + AmisDbData[i].toString());
								AmisDbData[i] = null;
							}
						}
						lock.unlock();
					}
					else
					{
						//logger.debug("D0 event data received: " + data);
					}
				}
			}
		});
	}

	private static void createTaskPV(final JSON json) {
		final PV pv = new PV(json.getUrl());

		//create timer schedule for JSON interface
		Timer timer_PV = new Timer();
		long delay_PV = 0;
		long interval_PV = json.getInterval()*1000;
		timer_PV.schedule(new TimerTask() {

			@Override
			public void run() {
				pv.readValues();

			}
		}, delay_PV, interval_PV);

		pv.addPVEventListener(new PVEventListener() {

			@Override
			public void PVEventFired(PVEvent evt) {
				logger.debug("\tPV values:");
				double[] tmpIntArray = (double[])evt.getSource();
				lock.lock();
				for (int i = 0; i < tmpIntArray.length; i++) {
					DBData[i+10] = tmpIntArray[i];
					logger.info("\t\t" + DBData[i + 10].toString());
				}
				lock.unlock();
			}
		});
	}

	private static void createTaskDB(final MYSQL mysql) {
		DB = new MySQLAccess(mysql);
		Timer timer_DB = new Timer();
		long delay_DB = 60*1000;
		long interval_DB = mysql.getInterval()*1000;
		timer_DB.schedule(new TimerTask() {
			@Override
			public void run() {
				lock.lock();
				DB.Set(DBData);
				lock.unlock();
			}
		}, delay_DB, interval_DB);
	}
	
	private static boolean createSocketCommunication(MYSQL mysql){
		MySQLAccess mysqlAccess = null;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		String inputLine;
		
		if(mysql != null){
			while(!Thread.currentThread().isInterrupted()){
				mysqlAccess = new MySQLAccess(mysql);
				
				try {
					serverSocket = new ServerSocket(mysql.getPort());
					clientSocket = serverSocket.accept();
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream(), true);
				} catch (IOException e) {
					logger.error("Could not listen on port: " + mysql.getPort() + ".");							
				}

				try {
					while ((inputLine = in.readLine()) != null) {
						logger.debug("request for data between: " + inputLine);
						Object[] input = inputLine.split("[ T]");
						JSONObject jObject = null;
						try {
							jObject = mysqlAccess.Get(input);
							out.println(jObject.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}							
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					out.close();
					in.close();
					clientSocket.close();
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
		else{
			logger.error("no valid config for MYSQL interface found!");
		}		
		return false;
	}
	
}