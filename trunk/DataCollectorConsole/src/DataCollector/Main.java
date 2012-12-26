package DataCollector;

import gnu.io.PortInUseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

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

public class Main {
	final static Logger logger = Logger.getRootLogger();
	static Lock lock = new ReentrantLock();
	static MySQLAccess DB;
	final static Object[] DBData = new Object[]{"00:00:00","00-01-01",0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
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

		setup();
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
					//createTaskD0(portName, baudRate, parityBit, dataBits, stopBits, interval);
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
					//createTaskPV("http://10.0.0.31/solar_api/GetInverterRealtimeData.cgi?Scope=System", interval);
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
				@Override
				public void run(){
					MYSQL mysql = new MYSQL();
					mysql = config.getMysql();
					if(mysql != null)
						createTaskDB(mysql);
					else
						logger.error("no valid config for MYSQL interface found!");
				}
			};
		}

		S0Thread.start();
		D0Thread.start();
		PVThread.start();
		DBThread.start();
	}

	private static void config(String[] args) {
		if(config.getConfig(args[0] + "config.xml")){
			logger.debug("found valid config file");
		} else if (parseArgs(args)){
			logger.debug("no valid config file available; try to use startup arguments");
		} else{
			logger.error("No valid input found!");
		}
	}

	private static void setup() {
		Level logLevel = Level.DEBUG;
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

	private static boolean parseArgs(String[] args) {
		boolean valid = true;
		String[] singleOption;
		ArrayList<String> portList = ComPort.portList();
		String[][] portListArray = new String[portList.size()][2];

		System.out.print("available serial ports:\r\n");
		for (int i = 0; i < portList.size(); i++) {
			portListArray[i][0] = String.valueOf(i);
			portListArray[i][1] = portList.get(i);

			System.out.println("\t" + portListArray[i][0] + "=" + portListArray[i][1]);
		}

		if(portList.size() > 0)
		{
			if(args.length > 0)
			{
				singleOption = args;
				logger.debug("command line arguments found");
			}
			else
			{
				System.out.print("usage:\t[port] + [options]\r\n" +
						"options:\r\n" +
						"\t-b [baud{300(default)|600|1200|2400|4800|9600|19200|...}]\r\n" +
						"\t-p [parity{0=none(default),1=odd,2=even}]\r\n" +
						"\t-d [data{7(default)|8}]\r\n" +
						"\t-s [stop{1(default)}|2|3(=1.5)]\r\n" +
						"\t-t [type{0=AMIS(default)|1=Froeling}]\r\n" +
						"\t-c [cycle{time in seconds}]\r\n" +
						"example:\r\t0 -b 19200 -p 0 -s 2 -t 0 -c 60");

				Scanner in = new Scanner(System.in);
				String options = in.nextLine();
				singleOption = options.split(" -");
			}

			//String[] singleOption = new String[]{"0"};
			System.out.print("enter port + options:\r\n\t");


			for (String string : singleOption) {
				logger.debug(string);

				if (string.matches("[0-9]{1,3}")) // port index
				{
					portName = portListArray[Integer.parseInt(string)][1];
					logger.debug("selected port: " + portName);
				}
				else if(string.matches("[0-9]?[a-zA-Z]{1}[ ]{1}[0-9]+"))
				{
					if(string.startsWith("b")) // port index
					{
						int baud = Integer.parseInt(string.substring(2));
						if(baud % 300 == 0)
						{
							baudRate = baud;
							logger.debug("baudrate is set to " + baudRate.toString());
						}
						else
							logger.debug("baudrate is not multiple of 300!");
					}

					if(string.startsWith("p")) //parity
					{
						switch(Integer.parseInt(string.substring(2))){
						case 0: parityBit = ComPort.Parity.NONE; break;
						case 1: parityBit = ComPort.Parity.ODD; break;
						case 2: parityBit = ComPort.Parity.EVEN; break;
						}

						logger.debug("parity is set to " + parityBit.toString());
						logger.debug("parity is set to " + parityBit.toString());
					}
					if(string.startsWith("d")) // data bits
					{
						switch(Integer.parseInt(string.substring(2))){
						case 7: dataBits = ComPort.DataBits.SEVEN; break;
						case 8: dataBits = ComPort.DataBits.EIGHT; break;
						}
						logger.debug("databit is set to " + dataBits.toString());
					}
					if(string.startsWith("s")) // stop bits
					{
						switch(Integer.parseInt(string.substring(2))){
						case 1: stopBits = ComPort.StopBits.ONE; break;
						case 3: stopBits = ComPort.StopBits.ONEPOINTFIVE; break;
						case 2: stopBits = ComPort.StopBits.TWO; break;
						}
						logger.debug("stopbit is set to " + stopBits.toString());
					}
					if(string.startsWith("t")) // hardware type
					{
						switch(Integer.parseInt(string.substring(2))){
						case 0: hardwareType = Hardware.AMIS; break;
						case 1: hardwareType = Hardware.FROELING; break;
						}
						logger.debug("hardware is set to " + hardwareType.toString());
					}
					if(string.startsWith("c")) // interval
					{
						try{
							interval = Integer.parseInt(string.substring(2));
							logger.debug("interval is set to " + interval.toString());
						}
						catch(NumberFormatException nfe){
							logger.info("option must be an integer value; used default value: " + interval.toString());
						}
					}
				}
				else
				{
					logger.info("Wrong or rather unknown option: " + string);
					valid = false;
				}
			}
		}
		else
		{
			System.out.println("No valid serial port found!");
		}

		return valid;
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
		//GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "D0", PinPullResistance.PULL_DOWN);
		GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "S0", s0.getPullResistance());
		//logger.debug("PIN" + RaspiPin.GPIO_00.toString() + "RESISTANCE" + s0.getPullResistance().toString());
		Counter.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {
				//logger.debug("...............> got GPIO event!");
				// display pin state on console
				if(event.getState().toString().equals("HIGH"))
				{
					//logger.debug("type of S0 event: " + event.getState().toString());
					//double deltaTime;
					//stopTime = System.currentTimeMillis();
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
		//long delay_PV = (Integer)interval*1000;
		long delay_S0 = 0;
		long interval_S0 = s0.getInterval()*1000;
		//timer_PV.schedule(new PVTask(pv), delay_PV, interval_PV);
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
			port.connect(d0.getPortName(), d0.getBaudRate(), parityBit, dataBits, stopBits);
			//port.connect(d0.getPortName(), d0.getBaudRate(), d0.getParity(), d0.getDataBits(), d0.getStopBits());
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
					port.portSettings(d0.getPortName(), d0.getBaudRate(), parityBit, dataBits, stopBits);
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
		//final PV pv = new PV("http://wilma-pt2-24.fronius.com/solar_api/v1/GetInverterRealtimeData.cgi?Scope=System");
		//pv.start();

		//create timer schedule for JSON interface
		Timer timer_PV = new Timer();
		//long delay_PV = (Integer)interval*1000;
		long delay_PV = 0;
		long interval_PV = json.getInterval()*1000;
		//timer_PV.schedule(new PVTask(pv), delay_PV, interval_PV);
		timer_PV.schedule(new TimerTask() {

			@Override
			public void run() {
				pv.readValues();

			}
		}, delay_PV, interval_PV);

		pv.addPVEventListener(new PVEventListener() {

			@Override
			public void PVEventFired(PVEvent evt) {
				// TODO Auto-generated method stub
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
		//timer_DB.schedule(new Task(DBData), 10000, delay_DB);
		timer_DB.schedule(new TimerTask() {
			@Override
			public void run() {
				lock.lock();
				DB.Set(DBData);
				/*for(int i = 0; i < DBData.length; i++){
					DBData[i] = 0;
				}
				 */
				lock.unlock();
			}
		}, delay_DB, interval_DB);
	}
}
