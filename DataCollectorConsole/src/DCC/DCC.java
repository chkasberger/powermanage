/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DCC;

//import java.io.BufferedReader;
//import gnu.io.PortInUseException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import DataCollector.DB.*;
import DataCollector.IO.*;
import DataCollector.PV.*;
import DataCollector.XML.*;

public class DCC {
	final static Logger logger = Logger.getRootLogger();
	static Lock lockDbValues = new ReentrantLock(true);
	static Lock mysqlLock = new ReentrantLock(true);
	//static MySQLAccess DB;
	static double[] DBData = new double[] { -1.0, -1.0, -1.0, -1.0 };
	//final static double[] DBData3p = new double[] { -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0 };

	static Object portName = 0;
	static Object baudRate = 300;
	static Object parityBit = ComPort.Parity.EVEN;
	static Object dataBits = ComPort.DataBits.SEVEN;
	static Object stopBits = ComPort.StopBits.ONE;
	static Object interval = 60;

	static int flagS0 = 0;
	static double meanPowerS0 = 0;
	//private static double meanEnergy = 0;
	//static double totalEnergy = 0;// = offset;
	static double powerFactorS0;// = (1000 / ticksPerKWH) * 3600; // Wh between two D0 events
	static GpioController gpioS0 = GpioFactory.getInstance();
	
	static Pin pinS0 = RaspiPin.GPIO_00;
	static GpioPinDigitalInput CounterS0; 
	static long startTimeS0 = System.currentTimeMillis();// nanoTime();
	static long stopTimeS0 = System.currentTimeMillis();// System.nanoTime();
	static double deltaTimeS0 = -1;
	static double kwhPerTickS0 = -1;
	static double currentPowerS0 = -1;
	static Lock lockS0 = new ReentrantLock();

	static ComPort port = new ComPort();
	static double energyConsumeD0 = -1;
	static double energyFeedInD0 = -1;
	
	static XmlConfigParser config = new XmlConfigParser();
	
    static JSONObject jObjectLastRow = null;
    static MySQLAccess mySQLAccess;
    
    static javax.swing.Timer DBTimer;
    static javax.swing.Timer D0Timer;
    static javax.swing.Timer S0Timer;
    static javax.swing.Timer PVTimer;
    
	static boolean loadManagement = false;
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length > 0) {
			// read "config.xml"
			if (config.getConfig(args[0])) {
				logger.debug("found valid config file"); //$NON-NLS-1$
				createTimerTasks();
				fcgiTaskSetup();
			} else {
				logger.error("No valid input found!"); //$NON-NLS-1$
				// parseArgs(new String[]{"./jar/config.xml"});
			}
		}
        logger.debug("exit programm!");
	}
	
	private static void fcgiTaskSetup(){
	      
		String inputString;
        JSONObject jObject = null;
        com.fastcgi.FCGIInterface fcgiInterface = new com.fastcgi.FCGIInterface();  
		
        while (fcgiInterface.FCGIaccept() >= 0) {

            System.out.println("Content-type: application/json\r\n");
            
            try {
                    inputString = System.getProperty("QUERY_STRING");
                    inputString = inputString.replace("%22", "\"");
                    inputString = inputString.replace("&", "");
    				
                    logger.debug("Input = " + inputString);
                    
                    if(inputString.startsWith("testLoad")){
                    	LoadManager.testLoad(inputString.substring(8));
                    }
                    else if(inputString.startsWith("testPin")){
                    	LoadManager.testPin(inputString.substring(7));
                    }
                    else if(inputString.startsWith("logger")){
                    	switch (inputString.substring(6,7)){
                		case "D":
                        	logger.setLevel(Level.DEBUG);
                			break;
                		case "E":
                        	logger.setLevel(Level.ERROR);
                			break;
                		case "O":
                        	logger.setLevel(Level.OFF);
                			break;
                		case "A":
                        	logger.setLevel(Level.ALL);
                			break;
                    	}                    	                    
                    }
                    else if(inputString.contains("\"last\":false") || inputString.contains("\"last\":\"false\"")){        
                         	logger.debug("Input contains an interval");
                        
                    	JSONObject jObj = new JSONObject(inputString);
                    	
                    	//lockDbValues.lock();
                    	mysqlLock.lock();
                    	{
                        	jObject = mySQLAccess.Get(jObj,true);                    		
                    	}
                    	mysqlLock.unlock();
                    	jObj = null;
                    	//lockDbValues.unlock();                                	                    				
                    }
                    else{        
                    	logger.debug("Input contains last"); 
                    	
                    	//lockDbValues.lock();
                    	
                    	jObject = jObjectLastRow;
                    	
                    	//lockDbValues.unlock();
                    }
                    System.out.println(jObject);
    				System.out.flush();


            } catch (Exception e) {
                    logger.error(e.getMessage());
            }
            //inputString = null;
            //jObject = null;
		}    
        logger.debug("foo we went into troubles!");
	}
	
	private static void createTimerTasks(){
		
		if(config.getMysql() != null){
			mySQLAccess = new MySQLAccess(config.getMysql());

			DbTaskSetup();
			DBTimer.setRepeats(true);
			DBTimer.setInitialDelay(40000);
			DBTimer.start();
		}else{
			logger.error("No config for DB available!");
		}
		
		if(config.getD0() != null){
			D0TaskSetup();
			D0Timer.setRepeats(true);
			D0Timer.setInitialDelay(10000);
			D0Timer.start();
		}else{
			logger.error("No config for D0 available!");
		}
		
		if(config.getS0() != null){
			S0TaskSetup();		
			S0Timer.setRepeats(true);
			S0Timer.setInitialDelay(20000);
			S0Timer.start();
		}else{
			logger.error("No config for S0 available!");
		}
		
		if(config.getJson() != null){
			PvTaskSetup();
			PVTimer.setRepeats(true);
			PVTimer.setInitialDelay(10000);
			PVTimer.start();
		}else{
			logger.error("No config for PV available!");
		}
	}	
	
	private static void DbTaskSetup(){
		/*
		 * DB task setup
		 */
		DBTimer = new javax.swing.Timer(config.getMysql().getInterval() * 1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lockDbValues.lock();
				{
					mysqlLock.lock();
					{
						mySQLAccess.Set(DBData);
					}
					mysqlLock.unlock();
					
					//DB.Set(DBData3p);
					
					for (int i = 0; i < DBData.length; i++)
						DBData[i] = -1.0;												
					//for (int i = 0; i < DBData3p.length; i++)
						//DBData3p[i] = -1.0;	
				}
				lockDbValues.unlock();					
			}			
		});
	}
	
	private static void D0TaskSetup(){
		/*
		 * D0 task setup
		 */
		
		port.portSettings(config.getD0().getPortName(), config.getD0().getBaudRate(),
				config.getD0().getParity(), config.getD0().getDataBits(), config.getD0().getStopBits());
		
		
		if(config.getLoad() != null){
			LoadManager.setLoad1_power(config.getLoad().getLoad1());
			LoadManager.setLoad2_power(config.getLoad().getLoad2());
			LoadManager.setLoad3_power(config.getLoad().getLoad3());
			LoadManager.setLoad4_power(config.getLoad().getLoad4());
			LoadManager.setHysterese(config.getLoad().getHysterese());
			loadManagement = true;
		}
		
		D0Timer = new javax.swing.Timer(120000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					port.portSettings(config.getD0().getPortName(), config.getD0().getBaudRate(),
							config.getD0().getParity(), config.getD0().getDataBits(), config.getD0().getStopBits());

					ComPort.flag = -1;
					port.out.write(new byte[] { 0x2f, 0x3f, 0x21, 0x0d, 0x0a});
					logger.debug("send: " + new String(new byte[] { 0x2f, 0x3f, 0x21, 0x0d, 0x0a}) + "; flag is: " + ComPort.flag);
					port.out.flush();
				} catch (Exception e) {
					logger.error(e.getStackTrace());
				}
			}
		});
		
		port.addComPortEventListener(new ComPortEventListener() {

			@Override
			public void comPortEventFired(ComPortEvent evt) {

				String str = evt.getSource().toString();
				//logger.debug("received comPortEvent!" +	str);
				try{
					if(str.length()>20){
						energyConsumeD0 = Double.parseDouble(str.substring(str.indexOf("1.7.0(") + 6 ,str.indexOf("1.7.0(") + 11));
						energyFeedInD0 = Double.parseDouble(str.substring(str.indexOf("2.7.0(") + 6 ,str.indexOf("2.7.0(") + 11));
						
						logger.info("finished cycle\r\n"
								+"\t\tconsume: " + energyConsumeD0 + " kW\r\n"
								+"\t\tfeed in: " + energyFeedInD0 + " kW");

						if(loadManagement)
							LoadManager.setLoad(energyConsumeD0 - energyFeedInD0);
						
						lockDbValues.lock();
						{
							DBData[0] = energyConsumeD0;
							DBData[1] = energyFeedInD0;
						}
						lockDbValues.unlock();
						
						D0Timer.restart();
					}
				}
				catch(Throwable e){
					logger.error(e.getMessage());
				}
			}
		});
	}
	
	private static void S0TaskSetup(){
		/*
		 * S0 task setup
		 */		
		
		S0Timer = new javax.swing.Timer(60*1000, new ActionListener() {			
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double mp;				

				lockS0.lock();
				{
					lockDbValues.lock();
					{
						mp = meanPowerS0 / flagS0;
						mp = Math.round(mp*1000);
						DBData[3] = mp / 1000;
					}
					lockDbValues.unlock();
					meanPowerS0 = 0;
					flagS0 = 0;				
				}
				lockS0.unlock();
				
			}
		});
		
		kwhPerTickS0 = (1 / config.getS0().getTicksPerKwh());
		powerFactorS0 = (1000 / kwhPerTickS0) * 3600;
		logger.debug("S0 interface has kwhPerTick: " + kwhPerTickS0);
		CounterS0 = gpioS0.provisionDigitalInputPin(pinS0, "S0", config.getS0().getPullResistance());
		
		CounterS0.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {

				if (event.getState().toString()
						.equals("HIGH"))
				{
					deltaTimeS0 = System.currentTimeMillis() - startTimeS0;
					startTimeS0 = System.currentTimeMillis();

					currentPowerS0 = powerFactorS0 / deltaTimeS0;

					double tmpPower = currentPowerS0 * 1000;
					tmpPower = Math.round(tmpPower);
					tmpPower = tmpPower / 1000;
					String log = "";
					
					lockS0.lock();
					{
						flagS0++;
						meanPowerS0 = meanPowerS0 + tmpPower;
						log = (" --> " + event.getPin() + " = " + event.getState() + "\t" +
								"time since last event: " + deltaTimeS0 + "ms\t" +
								"current power: " + tmpPower + " kW\t" +
								"mp=" + meanPowerS0 + " kW");
					}
					lockS0.unlock();					
					logger.debug(log);		
					log = null;
				}
			}
		});
	}
	private static void PvTaskSetup(){
		/*
		 * PV task setup
		 */

		final PV pvSystem = new PV(config.getJson().getUrl(), PV.SCOPE.SYSTEM);

		PVTimer = new javax.swing.Timer(60000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pvSystem.readValues();
				logger.debug("read PV values!");				
			}
		});
		
		pvSystem.addPVEventListener(new PVEventListener() {

			@Override
			public void PVEventFired(PVEvent evt) {
				logger.debug("got PV event!");
				double[] tmpIntArray = (double[]) evt.getSource();

				if(tmpIntArray[0] == -1)
				{
					lockDbValues.lock();
					{
						DBData[2] = tmpIntArray[1];	
					}
					lockDbValues.unlock();
				}
			}
		});
	}
}
	
	
	
	
	
	///////////////////////////////////// old ////////////////////////////////////////////
	

	/*
	 * private static void setup() { Level logLevel; //logLevel = Level.INFO;
	 * logLevel = Level.DEBUG; // ALL | DEBUG | INFO | WARN | ERROR | FATAL |
	 * OFF: try { SimpleLayout layout = new SimpleLayout(); ConsoleAppender
	 * consoleAppender = new ConsoleAppender(layout); logger.setLevel(logLevel);
	 * logger.addAppender(consoleAppender); } catch (Exception e) {
	 * System.out.println(e.getMessage()); } }
	 */
	/*
	private static void config(String[] args) {
		if (args.length > 0) {
			// read "config.xml"
			if (config.getConfig(args[0])) {
				logger.debug("found valid config file"); //$NON-NLS-1$
			} else {
				logger.error("No valid input found!"); //$NON-NLS-1$
				// parseArgs(new String[]{"./jar/config.xml"});
			}
		}
	}
	*/ 
	/*
	 * Creating S0 task
	 */
	/*
	private static int flag = 0;
	private static double meanPower = 0;
	private static double meanEnergy = 0;
	private static double totalEnergy = 0;// = offset;
	private static double powerFactor;// = (1000 / ticksPerKWH) * 3600; // Wh between two D0 events
	
	private static void createTaskS0(final S0 s0) {
		final double[] S0DbData = new double[] { -1.0, -1.0 };
		final Lock localLock = new ReentrantLock();
		final double kwhPerTick = (1 / s0.getTicksPerKwh()); // see user manual
																// of energy
																// meter

		totalEnergy = s0.getOffset();
		powerFactor = (1000 / s0.getTicksPerKwh()) * 3600; // Wh between two D0
															// events
		logger.debug("S0 interface has kwhPerTick: " + kwhPerTick); //$NON-NLS-1$

		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(
				RaspiPin.GPIO_00, "S0", s0.getPullResistance()); //$NON-NLS-1$
		Counter.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {

				if (event.getState().toString()
						.equals("HIGH"))
				{
					double deltaTime = System.currentTimeMillis() - startTime;
					startTime = System.currentTimeMillis();

					double currentPower = powerFactor / deltaTime;

					double tmpPower = currentPower * 1000;
					tmpPower = Math.round(tmpPower);
					tmpPower = tmpPower / 1000;

					double tmpEnergy; // = Math.round(totalEnergy * 1000);

					logger.debug("-----> S0 Energy: " + totalEnergy); //$NON-NLS-1$
					localLock.lock();
					{
						flag++;
						totalEnergy = totalEnergy + kwhPerTick; // kWh between
																// two ticks
						logger.debug("calculation: " + 1 / s0.getTicksPerKwh()); //$NON-NLS-1$
						// tmpEnergy = Math.round(totalEnergy * 1000);
						tmpEnergy = totalEnergy;
						logger.debug("-----> S0 Energy: " + totalEnergy); //$NON-NLS-1$
						meanPower = meanPower + tmpPower;
					}
					localLock.unlock();

					String log = (" --> " + event.getPin() + " = " + event.getState() + Messages.getString("DCC.21") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							Messages.getString("DCC.22") + deltaTime + Messages.getString("DCC.23") + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("DCC.24") + tmpPower + Messages.getString("DCC.25") + //$NON-NLS-1$ //$NON-NLS-2$
							Messages.getString("DCC.26") + tmpEnergy + Messages.getString("DCC.27")); //$NON-NLS-1$ //$NON-NLS-2$
					logger.debug(log);
					logger.debug(Messages.getString("DCC.28") + meanPower + Messages.getString("DCC.29") + meanEnergy); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});

		// create timer schedule for S0 interface
		Timer timer_S0 = new Timer();
		long delay_S0 = 0;
		long interval_S0 = s0.getInterval() * 1000;
		timer_S0.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.debug(Messages.getString("DCC.30")); //$NON-NLS-1$

				double mp;
				double te;
				localLock.lock();
				{
					mp = (Math.round((meanPower / flag) * 1000));
					te = totalEnergy;
					meanPower = 0;
					flag = 0;
					config.setConfig(
							Messages.getString("DCC.31"), Messages.getString("DCC.32"), String.valueOf(te)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				localLock.unlock();

				lock.lock();
				{
					S0DbData[0] = mp / 1000;
					;
					S0DbData[1] = te;
					;
					DBData[3] = S0DbData[0];
					
					for (int i = 0; i < S0DbData.length; i++) {
						DBData[i + 14] = S0DbData[i];
						logger.debug(Messages.getString("DCC.33") + DBData[i + 14].toString()); //$NON-NLS-1$
					}
					
				}
				lock.unlock();
			}
		}, delay_S0, interval_S0);
	}
	
	private static void createTaskD0(final D0 d0) {
		final ComPort port = new ComPort();
		try {
			port.connect(d0.getPortName(), d0.getBaudRate(), d0.getMaxBaudRate(), d0.getParity(),
					d0.getDataBits(), d0.getStopBits());
		} catch (PortInUseException e) {
			logger.error(e.getMessage());
		}

		// create timer schedule for D0 interface

		
		Timer timer_D0 = new Timer();
		long delay_D0 = 0;
		long interval_D0 = d0.getInterval() * 1000;
		// timer_AMIS.schedule(new ComPortTask(port, portName, baudRate,
		// parityBit, dataBits, stopBits), delay_AMIS, interval_AMIS);
		timer_D0.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					port.portSettings(d0.getPortName(), d0.getBaudRate(),
							d0.getParity(), d0.getDataBits(), d0.getStopBits());
					// port.portSettings(d0.getPortName(), d0.getBaudRate(),
					// parityBit, dataBits, stopBits);
					port.out.write(new byte[] { 0x2f, 0x3f, 0x21, 0x0d, 0x0a });
					port.out.flush();
					
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}, delay_D0, interval_D0);

		port.addComPortEventListener(new ComPortEventListener() {
			//Object[] AmisDbData = new Object[10];
			double[] AmisDbData = new double[2];
			double[] AmisDbData3p = new double[10];
			@Override
			public void comPortEventFired(ComPortEvent evt) {

				String str = evt.getSource().toString();
				String rawData = Messages.getString("DCC.34"); //$NON-NLS-1$

				if (str.contains(Messages.getString("DCC.35")) | //$NON-NLS-1$
						str.contains(Messages.getString("DCC.36")) | //$NON-NLS-1$
						str.contains(Messages.getString("DCC.37"))) //$NON-NLS-1$
				{
					String data;
					if (!str.contains(Messages.getString("DCC.38"))) //$NON-NLS-1$
						rawData = str.substring(
								str.indexOf(Messages.getString("DCC.39")) + 1, str.indexOf(Messages.getString("DCC.40"))); //$NON-NLS-1$ //$NON-NLS-2$
					else
						rawData = str;

					if (rawData.contains(Messages.getString("DCC.41"))) //$NON-NLS-1$
						data = rawData.substring(0,
								rawData.indexOf(Messages.getString("DCC.42"))); //$NON-NLS-1$
					else
						data = rawData.trim();

					if (str.contains("1.7.0("))
						AmisDbData[0] = Double.parseDouble(data);
					else if (str.contains("2.7.0("))
						AmisDbData[1] = Double.parseDouble(data);
					
					else if (str.contains("32.7("))
						AmisDbData3p[0] = Double.parseDouble(data);
					else if (str.contains("52.7("))
						AmisDbData3p[1] = Double.parseDouble(data);
					else if (str.contains("72.7("))
						AmisDbData3p[2] = Double.parseDouble(data);
					else if (str.contains("31.7("))
						AmisDbData3p[3] = Double.parseDouble(data);
					else if (str.contains("51.7("))
						AmisDbData3p[4] = Double.parseDouble(data);
					else if (str.contains("71.7("))
						AmisDbData3p[5] = Double.parseDouble(data);
					else if (str.contains("91.7("))
						AmisDbData3p[6] = Double.parseDouble(data);
					else if (str.contains("81.7.4("))
						AmisDbData3p[7] = Double.parseDouble(data);
					else if (str.contains("81.7.15("))
						AmisDbData3p[8] = Double.parseDouble(data);
					else if (str.contains("81.7.26("))
						AmisDbData3p[9] = Double.parseDouble(data);
						
					else if (str.startsWith(Messages.getString("DCC.53"))) //$NON-NLS-1$
					{
						data = Messages.getString("DCC.54"); //$NON-NLS-1$
						logger.debug(Messages.getString("DCC.55")); //$NON-NLS-1$

						lock.lock();
						DBData[0] = -1;
						DBData[1] = -1;
						DBData[0] = AmisDbData[0];
						DBData[1] = AmisDbData[1];
						
						for(int i = 0; i < AmisDbData3p.length; i++){
							DBData3p[i] = -1;
							DBData3p[i] = AmisDbData3p[i];
							//logger.debug(i + ": " + DBData3p[i]);
						}

						lock.unlock();
					} else {
						// logger.debug("D0 event data received: " + data);
					}
				}
			}
		});
	}

	private static void createTaskPV(final JSON json) {		
		final PV pvSystem = new PV(json.getUrl(), PV.SCOPE.SYSTEM);
		//String urlString3P = json.getHost() + json.getDeviceId() + json.getCollection();
		String url3PString = "http://solar/solar_api/v1/GetInverterRealtimeData.cgi?Scope=Device&DeviceId=1&DataCollection=3PInverterData";
		URL url3P = null;
		try {
			url3P = new URL(url3PString);			
		} catch (MalformedURLException e) {
			logger.error("createTaskPV: " + e.getMessage());
		}
		final PV pv3p = new PV(url3P, PV.SCOPE.THREEPHASE);
		
		// create timer schedule for JSON interface
		Timer timer_PV = new Timer();
		long delay_PV = 0;
		long interval_PV = json.getInterval() * 1000;
		timer_PV.schedule(new TimerTask() {

			@Override
			public void run() {
				pvSystem.readValues();
				pv3p.readValues();
			}
		}, delay_PV, interval_PV);

		pvSystem.addPVEventListener(new PVEventListener() {

			@Override
			public void PVEventFired(PVEvent evt) {
				logger.debug(Messages.getString("DCC.58")); //$NON-NLS-1$
				double[] tmpIntArray = (double[]) evt.getSource();
				lock.lock();

				if(tmpIntArray[0] == -1)
				DBData[2] = tmpIntArray[1];

				lock.unlock();
			}
		});

		pv3p.addPVEventListener(new PVEventListener() {
			@Override
			public void PVEventFired(PVEvent evt) {
				logger.debug(Messages.getString("DCC.58")); //$NON-NLS-1$
				double[] tmpIntArray = (double[]) evt.getSource();
				lock.lock();
				
				if(tmpIntArray[0] == 1)
				for (int i = 0; i < tmpIntArray.length - 1; i++) {
					DBData3p[i + 10] = tmpIntArray[i + 1];
					//logger.info(Messages.getString("DCC.59") + DBData3p[i + 10].toString()); //$NON-NLS-1$
				}
				lock.unlock();
			}
		});
	}
}
	*/
