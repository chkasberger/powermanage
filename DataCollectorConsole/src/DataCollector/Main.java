package DataCollector;

import gnu.io.PortInUseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import DataCollector.IO.ComPortTask;
import DataCollector.JSON.PV;
import DataCollector.JSON.PVEvent;
import DataCollector.JSON.PVEventListener;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Main {
	final static Logger logger = Logger.getRootLogger();
	static Lock lock = new ReentrantLock();
	static MySQLAccess DB;
	final static Object[] DBData = new Object[]{"0","0","0","0","00:00:00","0000-00-00","0","0","0","0",0,0,0,0};
	enum Hardware {AMIS, FROELING}
	//static MySQLAccess DB;

	static double ticksPerKWH = 800;	// see user manual of energy meter
	static double offset = 1598.300;	// already consumed energy

	static double totalEnergy = 0;
	static final double powerFactor = (1000000 / ticksPerKWH) * 3600; // Wh between two D0 events
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		setup();

		if(parseArgs(args))
		{
			createTaskS0();
			createTaskD0(portName, baudRate, parityBit, dataBits, stopBits, interval);
			createTaskPV("http://10.0.0.3/solar_api/GetInverterRealtimeData.cgi?Scope=System");
			createTaskDB();
		}
		else{
			System.out.println("No valid input found!");
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

	private static void createTaskS0() {
		// create gpio controller instance
		GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin #00 as an input pin with its internal pull down resistor enabled
		// (configure pin edge to both rising and falling to get notified for HIGH and LOW state
		// changes)
		GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00,             // PIN NUMBER
				"D0",                   // PIN FRIENDLY NAME
				PinPullResistance.PULL_DOWN); // PIN RESISTANCE
		//Counter.addListener(new GpioEventListener());
		Counter.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {


				// display pin state on console
				if(event.getState().toString().equals("HIGH"))
				{
					double deltaTime;
					stopTime = System.currentTimeMillis();
					deltaTime = stopTime - startTime;
					double currentPower = powerFactor / deltaTime;

					totalEnergy = totalEnergy + 1.250;

					String log = (" --> "+ event.getPin() + " = " + event.getState() + ";\t" +
							"time since last event: " + deltaTime + "ms;\t" +
							"current power: " + currentPower + " W\t" +
							"total energy: " + totalEnergy + "Wh");
					System.out.println(log);

					startTime = System.currentTimeMillis();
				}
			}
		});
	}

	private static void createTaskD0(
			Object portName,
			Object baudRate,
			Object parityBit,
			Object dataBits,
			Object stopBits,
			Object interval) {
		ComPort port = new ComPort();
		try {
			port.connect(portName, baudRate, parityBit, dataBits, stopBits);
		} catch (PortInUseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		//create timer schedule for D0 interface
		Timer timer_AMIS = new Timer();
		long delay_AMIS = 0;
		long interval_AMIS = (Integer)interval*1000;
		timer_AMIS.schedule(new ComPortTask(port, portName, baudRate, parityBit, dataBits, stopBits), delay_AMIS, interval_AMIS);

		port.addComPortEventListener(new ComPortEventListener() {
			Object[] AmisDbData = new Object[10];

			@Override
			public void comPortEventFired(ComPortEvent evt) {
				// TODO Auto-generated method stub
				File file =new File("AMISLOG.csv");

				//if file doesnt exists, then create it
				if(!file.exists()){
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//true = append file
				FileWriter fileWritter = null;
				try {
					fileWritter = new FileWriter(file.getName(),true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

				String str = evt.getSource().toString();
				String data = "";
				String rawData = "";
				String sepperator = ";";
				boolean writeToFile = true;		//log data into file
				if(
						str.contains("(")|
						str.contains(")")|
						str.contains("!\r\n"))
				{
					if(!str.contains("!\r\n"))
						rawData = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
					else
						rawData = str;

					if(rawData.contains("*"))
						data = rawData.substring(0, rawData.indexOf("*"));
					else
						data = rawData.trim();

					if(str.contains("F.F("))
					{
						AmisDbData[0] = data;
					}
					else if(str.contains("0.0.0("))
					{
						AmisDbData[1] = data;
					}
					else if(str.contains("1.8.0("))
					{
						AmisDbData[2] = data;
					}
					else if(str.contains("2.8.0("))
					{
						AmisDbData[3] = data;
					}
					else if(str.contains("0.9.1("))
					{
						AmisDbData[4] = data;
					}
					else if(str.contains("0.9.2("))
					{
						AmisDbData[5] = data;
					}
					else if(str.contains("1.7.0("))
					{
						AmisDbData[6] = data;
					}
					else if(str.contains("2.7.0("))
					{
						AmisDbData[7] = data;
					}
					else if(str.contains("3.7.0("))
					{
						AmisDbData[8] = data;
					}
					else if(str.contains("4.7.0("))
					{
						AmisDbData[9] = data;
					}
					else if(str.startsWith("!\r\n"))
					{
						data = "";
						sepperator = "\r\n";
						System.out.print("\r\nfinished cycle\r\n");
						lock.lock();
						for (int i = 0; i < AmisDbData.length; i++) {
							DBData[i] = AmisDbData[i];
						}
						lock.unlock();
					}
					else
					{
						writeToFile = false;
					}

					if(writeToFile)
					{
						logger.debug(data);
						try {
							bufferWritter.write(data + sepperator);
							bufferWritter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

	}

	private static void createTaskPV(String host) {
		final PV pv = new PV(host);
		//final PV pv = new PV("http://wilma-pt2-24.fronius.com/solar_api/v1/GetInverterRealtimeData.cgi?Scope=System");
		//pv.start();

		//create timer schedule for JSON interface
		Timer timer_PV = new Timer();
		//long delay_PV = (Integer)interval*1000;
		long delay_PV = 0;
		long interval_PV = 60*1000;
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
				logger.debug("got PV Event!");
				int[] tmpIntArray = (int[])evt.getSource();
				lock.lock();
				for (int i = 0; i < tmpIntArray.length; i++) {
					DBData[i+10] = tmpIntArray[i];
					System.out.print(DBData[i + 10].toString() + "\r\n");
				}
				lock.unlock();
			}
		});
	}

	private static void createTaskDB() {
		//create timer schedule for DB access
		Timer timer_DB = new Timer();
		//long delay_PV = (Integer)interval*1000;
		long delay_DB = 60*1000;
		long interval_DB = 60*1000;
		//timer_DB.schedule(new Task(DBData), 10000, delay_DB);
		timer_DB.schedule(new TimerTask() {

			@Override
			public void run() {
				lock.lock();
				DB.Set(DBData);
				lock.unlock();
			}
		}, delay_DB, interval_DB);
	}

	private static void setup() {
		Level logLevel = Level.DEBUG;
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		try {
			SimpleLayout layout = new SimpleLayout();
			ConsoleAppender consoleAppender = new ConsoleAppender(layout);
			logger.setLevel(logLevel);
			logger.addAppender(consoleAppender);
		} catch (Exception ex) {
			logger.debug(ex + "@" + JUtil.getMethodName(1));
		}
		DB = new MySQLAccess();
	}
}
