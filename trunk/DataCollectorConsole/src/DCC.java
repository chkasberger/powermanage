/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
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
	final static Object[] DBData = new Object[] {
			Messages.getString("DCC.0"), Messages.getString("DCC.1"), -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0 }; //$NON-NLS-1$ //$NON-NLS-2$
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;

	enum Hardware {
		AMIS, FROELING
	}

	// static MySQLAccess DB;

	String logFile = Messages.getString("DCC.2"); //$NON-NLS-1$
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
		// config(new String[]{"./jar/"});
		createThreads();
	}

	private static void createThreads() {
		Thread S0Thread = null;
		Thread D0Thread = null;
		Thread PVThread = null;
		Thread DBThread = null;

		if (S0Thread == null) {
			S0Thread = new Thread() {
				@Override
				public void run() {
					Thread thisThread = Thread.currentThread();
					thisThread.setName(Messages.getString("DCC.3")); //$NON-NLS-1$

					S0 s0 = new S0();
					s0 = config.getS0();
					if (s0 != null)
						createTaskS0(s0);
					else
						logger.error(Messages.getString("DCC.4")); //$NON-NLS-1$
				}
			};
		}

		if (D0Thread == null) {
			D0Thread = new Thread() {
				@Override
				public void run() {
					Thread thisThread = Thread.currentThread();
					thisThread.setName(Messages.getString("DCC.5")); //$NON-NLS-1$

					D0 d0 = new D0();
					d0 = config.getD0();
					if (d0 != null)
						createTaskD0(d0);
					else
						logger.error(Messages.getString("DCC.6")); //$NON-NLS-1$
				}
			};
		}

		if (PVThread == null) {
			PVThread = new Thread() {
				@Override
				public void run() {
					Thread thisThread = Thread.currentThread();
					thisThread.setName(Messages.getString("DCC.7")); //$NON-NLS-1$

					JSON json = new JSON();
					json = config.getJson();
					if (json != null)
						createTaskPV(json);
					else
						logger.error(Messages.getString("DCC.8")); //$NON-NLS-1$
				}
			};
		}

		if (DBThread == null) {
			DBThread = new Thread() {
				public void run() {
					Thread thisThread = Thread.currentThread();
					thisThread.setName(Messages.getString("DCC.9")); //$NON-NLS-1$

					MYSQL mysql = new MYSQL();
					mysql = config.getMysql();

					createTaskDB(mysql);
					while (true) {
						createSocketCommunication(mysql);
						logger.debug(Messages.getString("DCC.10")); //$NON-NLS-1$
					}
				}
			};
		}

		S0Thread.start();
		D0Thread.start();
		PVThread.start();
		DBThread.start();
	}

	/*
	 * private static void setup() { Level logLevel; //logLevel = Level.INFO;
	 * logLevel = Level.DEBUG; // ALL | DEBUG | INFO | WARN | ERROR | FATAL |
	 * OFF: try { SimpleLayout layout = new SimpleLayout(); ConsoleAppender
	 * consoleAppender = new ConsoleAppender(layout); logger.setLevel(logLevel);
	 * logger.addAppender(consoleAppender); } catch (Exception e) {
	 * System.out.println(e.getMessage()); } }
	 */
	private static void config(String[] args) {
		if (args.length > 0) {
			// read "config.xml"
			if (config.getConfig(args[0])) {
				logger.debug(Messages.getString("DCC.11")); //$NON-NLS-1$
			} else {
				logger.error(Messages.getString("DCC.12")); //$NON-NLS-1$
				// parseArgs(new String[]{"./jar/config.xml"});
			}
		}
	}

	private static int flag = 0;
	private static double meanPower = 0;
	private static double meanEnergy = 0;
	private static double totalEnergy = 0;// = offset;
	private static double powerFactor;// = (1000 / ticksPerKWH) * 3600; // Wh
										// between two D0 events

	private static void createTaskS0(final S0 s0) {
		final Object[] S0DbData = new Object[] { -1.0, -1.0 };
		final Lock localLock = new ReentrantLock();
		final double kwhPerTick = (1 / s0.getTicksPerKwh()); // see user manual
																// of energy
																// meter

		totalEnergy = s0.getOffset();
		powerFactor = (1000 / s0.getTicksPerKwh()) * 3600; // Wh between two D0
															// events
		logger.debug(Messages.getString("DCC.13") + kwhPerTick); //$NON-NLS-1$

		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput Counter = gpio.provisionDigitalInputPin(
				RaspiPin.GPIO_00,
				Messages.getString("DCC.14"), s0.getPullResistance()); //$NON-NLS-1$
		Counter.addListener(new GpioPinListenerDigital() {

			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {

				if (event.getState().toString()
						.equals(Messages.getString("DCC.15"))) //$NON-NLS-1$
				{
					double deltaTime = System.currentTimeMillis() - startTime;
					startTime = System.currentTimeMillis();

					double currentPower = powerFactor / deltaTime;

					double tmpPower = currentPower * 1000;
					tmpPower = Math.round(tmpPower);
					tmpPower = tmpPower / 1000;

					double tmpEnergy; // = Math.round(totalEnergy * 1000);

					logger.debug(Messages.getString("DCC.16") + totalEnergy); //$NON-NLS-1$
					localLock.lock();
					{
						flag++;
						totalEnergy = totalEnergy + kwhPerTick; // kWh between
																// two ticks
						logger.debug(Messages.getString("DCC.17") + 1 / s0.getTicksPerKwh()); //$NON-NLS-1$
						// tmpEnergy = Math.round(totalEnergy * 1000);
						tmpEnergy = totalEnergy;
						logger.debug(Messages.getString("DCC.18") + totalEnergy); //$NON-NLS-1$
						meanPower = meanPower + tmpPower;
					}
					localLock.unlock();

					String log = (Messages.getString("DCC.19") + event.getPin() + Messages.getString("DCC.20") + event.getState() + Messages.getString("DCC.21") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			port.connect(d0.getPortName(), d0.getBaudRate(), d0.getParity(),
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
			Object[] AmisDbData = new Object[10];

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

					if (str.contains(Messages.getString("DCC.43"))) //$NON-NLS-1$
						AmisDbData[0] = data;
					else if (str.contains(Messages.getString("DCC.44"))) //$NON-NLS-1$
						AmisDbData[1] = data;
					else if (str.contains(Messages.getString("DCC.45"))) //$NON-NLS-1$
						AmisDbData[2] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.46"))) //$NON-NLS-1$
						AmisDbData[3] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.47"))) //$NON-NLS-1$
						AmisDbData[4] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.48"))) //$NON-NLS-1$
						AmisDbData[5] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.49"))) //$NON-NLS-1$
						AmisDbData[6] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.50"))) //$NON-NLS-1$
						AmisDbData[7] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.51"))) //$NON-NLS-1$
						AmisDbData[8] = Double.parseDouble(data);
					else if (str.contains(Messages.getString("DCC.52"))) //$NON-NLS-1$
						AmisDbData[9] = Double.parseDouble(data);
					else if (str.startsWith(Messages.getString("DCC.53"))) //$NON-NLS-1$
					{
						data = Messages.getString("DCC.54"); //$NON-NLS-1$
						logger.debug(Messages.getString("DCC.55")); //$NON-NLS-1$

						lock.lock();
						logger.info(Messages.getString("DCC.56")); //$NON-NLS-1$
						for (int i = 0; i < AmisDbData.length; i++) {
							if (AmisDbData[i] != null) {
								DBData[i] = AmisDbData[i];
								logger.info(Messages.getString("DCC.57") + AmisDbData[i].toString()); //$NON-NLS-1$
								AmisDbData[i] = null;
							}
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
		final PV pv = new PV(json.getUrl());

		// create timer schedule for JSON interface
		Timer timer_PV = new Timer();
		long delay_PV = 0;
		long interval_PV = json.getInterval() * 1000;
		timer_PV.schedule(new TimerTask() {

			@Override
			public void run() {
				pv.readValues();

			}
		}, delay_PV, interval_PV);

		pv.addPVEventListener(new PVEventListener() {

			@Override
			public void PVEventFired(PVEvent evt) {
				logger.debug(Messages.getString("DCC.58")); //$NON-NLS-1$
				double[] tmpIntArray = (double[]) evt.getSource();
				lock.lock();
				for (int i = 0; i < tmpIntArray.length; i++) {
					DBData[i + 10] = tmpIntArray[i];
					logger.info(Messages.getString("DCC.59") + DBData[i + 10].toString()); //$NON-NLS-1$
				}
				lock.unlock();
			}
		});
	}

	private static void createTaskDB(final MYSQL mysql) {
		DB = new MySQLAccess(mysql);
		Timer timer_DB = new Timer();
		long delay_DB = 60 * 1000;
		long interval_DB = mysql.getInterval() * 1000;
		timer_DB.schedule(new TimerTask() {
			@Override
			public void run() {
				lock.lock();
				DB.Set(DBData);
				DBData[0] = Messages.getString("DCC.60"); //$NON-NLS-1$
				DBData[1] = Messages.getString("DCC.61"); //$NON-NLS-1$

				for (int i = 2; i < DBData.length; i++) {
					DBData[i] = -1.0;
				}
				lock.unlock();
			}
		}, delay_DB, interval_DB);
	}

	private static boolean createSocketCommunication(MYSQL mysql) {
		MySQLAccess mysqlAccess = null;
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		String inputLine;

		if (mysql != null) {
			while (!Thread.currentThread().isInterrupted()) {
				mysqlAccess = new MySQLAccess(mysql);

				try {
					serverSocket = new ServerSocket(mysql.getPort());
					clientSocket = serverSocket.accept();
					in = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream(), true);
				} catch (IOException e) {
					logger.error(Messages.getString("DCC.62") + mysql.getPort() + Messages.getString("DCC.63")); //$NON-NLS-1$ //$NON-NLS-2$
				}

				try {
					while ((inputLine = in.readLine()) != null) {
						logger.debug(Messages.getString("DCC.64") + inputLine); //$NON-NLS-1$
						Object[] input = inputLine.split(Messages
								.getString("DCC.65")); //$NON-NLS-1$
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
		} else {
			logger.error(Messages.getString("DCC.66")); //$NON-NLS-1$
		}
		return false;
	}
}