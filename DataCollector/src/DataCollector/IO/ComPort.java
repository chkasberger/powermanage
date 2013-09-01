/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.IO;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

public class ComPort {
	private static Logger logger = Logger.getRootLogger();

	private SerialPort serialPort = null;
	public InputStream in;
	public DataInputStream dIn;
	public BufferedInputStream bIn;
	public OutputStream out;
	public PipedInputStream write;

	public SerialReader serialReader;
	public SerialWriter serialWriter;

	private String portName = "";
	private int baudRate = 9600;
	private static int[] baudRates = { 300, 2400, 4800, 9600, 19200, 115200 };

	public static int[] getBaudRates() {
		return baudRates;
	}

	public enum Parity {
		NONE, ODD, EVEN, MARK, SPACE
	}

	public enum DataBits {
		SEVEN, EIGHT
	}

	public enum StopBits {
		ONE, ONEPOINTFIVE, TWO
	}

	private int parity = SerialPort.PARITY_EVEN;
	private int dataBits = SerialPort.DATABITS_7;
	private int stopBits = SerialPort.STOPBITS_1;

	public String getPortName() {
		return portName;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public int getParity() {
		return parity;
	}

	public int getStopBits() {
		return stopBits;
	}

	public int getDataBits() {
		return dataBits;
	}

	public ComPort() {

	}

	public void dispose() {
		// shlPortConfig.close();
		// shlPortConfig.dispose();
	}

	/**
	 * @description create listener components
	 */
	protected static EventListenerList listenerList = new EventListenerList();

	public void addComPortEventListener(ComPortEventListener listener) {
		listenerList.add(ComPortEventListener.class, listener);
	}

	public void removeComPortEventListener(ComPortEventListener listener) {
		listenerList.remove(ComPortEventListener.class, listener);
	}

	static void fireComPortEvent(ComPortEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == ComPortEventListener.class) {
				((ComPortEventListener) listeners[i + 1])
						.comPortEventFired(evt);
			}
		}
	}

	/**
	 * @return Returns all available serial ports of the local machine
	 */
	public static ArrayList<String> portList() {

		ArrayList<String> availableComPorts = new ArrayList<>();
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();

			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				availableComPorts.add(portIdentifier.getName());
			}
		}
		return availableComPorts;
	}

	/**
	 * @description manages the serial port connectivity
	 */
	public void connect(Object portName, Object baudRate, Object parity, Object dataBits, Object stopBits) {

		portSettingsIntern(portName, baudRate, parity, dataBits, stopBits);

		//Thread thisThread = Thread.currentThread();
		//thisThread.setPriority(2);

		try {
			if (serialPort != null) {
				serialReader.interrupt();
				//serialWriter.interrupt();
				serialPort.close();
				logger.debug(serialPort.getName() + " is closed!");
			}

			// String SerialPortID = this.portName;
			// System.setProperty("gnu.io.rxtx.SerialPorts", SerialPortID);

			/** Identifies and opens the desired port */
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(this.portName);
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					1000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				logger.debug(serialPort.getName() + " is open");

				try {
					/** Configure the opened serial port */

					// portSettings(portName, baudRate, parity, dataBits,
					// stopBits);

					// serialPort.setRTS(true);
					// serialPort.setDTR(true);

					// serialPort.addEventListener(this);
					// serialPort.notifyOnDataAvailable(true);

					/** configure accessibility to serial port in/out stream */
					in = serialPort.getInputStream();
					
					bIn = new BufferedInputStream(in);
					out = serialPort.getOutputStream();
					serialReader = new SerialReader(in, "SerialReader");
					//serialWriter = new SerialWriter(out, "SerialWriter");

					/** starts threads for read and write process */
					serialReader.start();
					//serialWriter.start();

				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			} else {
				System.out
						.println("Error: Only serial ports are handled by this example.");
			}

		} catch (NoSuchPortException | PortInUseException e) {
			logger.error(e.getStackTrace());
		}
	}

	public void close(Object portName) {

		if (serialPort != null) {
			serialReader.interrupt();
			//serialWriter.interrupt();
			serialPort.close();
			logger.debug(serialPort.getName() + " is closed!");
		}
	}

	public void portSettings(Object portName, Object baudRate, Object parity,Object dataBits, Object stopBits) {
		if(serialPort == null){
			logger.debug("Port " + portName + " was null!");
			connect(portName, baudRate, parity, dataBits, stopBits);
		}
		else{
			//logger.debug("Port " + portName + "was not null----------------------->");
			portSettingsIntern(portName, baudRate, parity, dataBits, stopBits);
		}
	}
	/** Parses the port settings */
	private void portSettingsIntern(Object portName, Object baudRate, Object parity,Object dataBits, Object stopBits) {
		if (portName != null) {
			this.portName = portName.toString();
		} else {
			this.portName = portList().get(0);
		}
		if ((int) baudRate > 0)
			this.baudRate = (int) baudRate;
		else
			this.baudRate = 9600;

		switch ((Parity) parity) {
		case NONE:
			this.parity = SerialPort.PARITY_NONE;
			break;
		case EVEN:
			this.parity = SerialPort.PARITY_EVEN;
			break;
		case ODD:
			this.parity = SerialPort.PARITY_ODD;
			break;
		default:
			this.parity = SerialPort.PARITY_NONE;
		}
		switch ((DataBits) dataBits) {
		case SEVEN:
			this.dataBits = SerialPort.DATABITS_7;
			break;
		case EIGHT:
			this.dataBits = SerialPort.DATABITS_8;
			break;
		default:
			this.dataBits = SerialPort.DATABITS_7;
		}
		switch ((StopBits) stopBits) {
		case ONE:
			this.stopBits = SerialPort.STOPBITS_1;
			break;
		case ONEPOINTFIVE:
			this.stopBits = SerialPort.STOPBITS_1_5;
			break;
		case TWO:
			this.stopBits = SerialPort.STOPBITS_2;
			break;
		default:
			this.stopBits = SerialPort.STOPBITS_1;
		}

		try {
			if (serialPort != null) {
				//serialPort.setRTS(false);
				//serialPort.setDTR(false);
				serialPort.setSerialPortParams(this.baudRate, this.dataBits,this.stopBits, this.parity);
				//if(!serialPort.isRTS())
					//serialPort.setRTS(true);
				if(!serialPort.isDTR())
						serialPort.setDTR(true);
				logger.debug(serialPort.getName() + " is configured with: "
						+ serialPort.getBaudRate() + " "
						+ serialPort.getParity() + " "
						+ serialPort.getDataBits() + " "
						+ serialPort.getStopBits());
			}
			else{
				logger.debug("serialPort is null and can not be reconfigured");
			}
		} catch (UnsupportedCommOperationException e) {
			logger.error(e.getMessage());
		}
	}

	public static int flag = -1;
	
	
	/** Class for serial read process */
	public class SerialReader extends Thread // implements Runnable
	{

		InputStream in;
		int hardwareType = 0;
		
		public SerialReader(InputStream in, String threadName) {
			super(threadName);
			this.in = in;
		}

		public SerialReader(InputStream in, String threadName, int hardwareType) {
			super(threadName);
			this.in = in;
			this.hardwareType = hardwareType;
		}

		@Override
		public void run() {
			int bufferSize = 9600;
			byte[] buffer = new byte[bufferSize];
			int len = -1;
			try {

				String frame = "";

				while (!this.isInterrupted()) {

					Pattern identificationMessagePattern = Pattern.compile("/[\\w]{3}[0-9a-iA-I].{16}\r\n");
					Pattern blockEndMessagePattern = Pattern.compile("!\r\n");
					double approxFrameLength = 0;
					
					while ((len = bIn.read(buffer, 0, bufferSize)) > 0) {
						try{
							frame += new String(buffer, 0, len);
							//logger.debug(new String(buffer, 0, len));
						}
						catch(Exception e){
							logger.error(e.getMessage());
						}

						if(flag == 1){
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								logger.error(e.getStackTrace());
							}
							if (frame.length() > approxFrameLength && 
									blockEndMessagePattern.matcher(frame).find()
									){
								ComPortEvent eventFrame = new ComPortEvent(frame);
								fireComPortEvent(eventFrame);
								
								double frameLength = frame.length();
								approxFrameLength = frameLength * 0.9;
								logger.debug("Got EOF frame! Size was " + frame.length() + " bytes! " + "ApproxFrameLength " + approxFrameLength);

								frame = "";								
								flag = -1;
							}
						}
						else if(flag == -1){
							if (identificationMessagePattern.matcher(frame).find()) {
								logger.debug("identification frame is:\r\n\t\t" + frame +
										"\t\tsend: " + new String(new byte[] { 0x06, 0x30, 0x35, 0x30, 0x0d, 0x0a }));

								frame = "";
								
								serialPort.getOutputStream().write(new byte[] {0x06, 0x30, 0x35, 0x30, 0x0d, 0x0a});
								serialPort.getOutputStream().flush();
								
								flag = 0;
								logger.debug("Flag is now " + flag);								
							}
						}
						
						else if(flag == 0){
								try {
									sleep(1000);
								} catch (InterruptedException e) {
									logger.error(e.getStackTrace());
								}
								int baud = 9600;
								logger.debug("set port to : "+ baud + " baud");
								
								portSettings(portName, baud, Parity.EVEN,
										DataBits.SEVEN, StopBits.ONE);
								
								frame = "";
								flag = 1;
						}
					}
					logger.debug("falled out of port while loop!");
					
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public class SerialWriter extends Thread // implements Runnable
	{
		Thread thisThread;
		public OutputStream out;
		
		boolean suspendFlag;
		
		public SerialWriter(OutputStream out, String threadName) {
			super(threadName);
			thisThread = SerialReader.currentThread();
			this.out = out;
			suspendFlag = false;
		}

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				try {
					while ((in.read()) > -1) {
				         synchronized(this) {
				             while(suspendFlag) {
				                wait();
				             }
				         }
						System.out.println(out);
						logger.debug("foo Ya Writer Man!");
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			//thisThread = null;
		}
		
	   public void Suspend() {
		      suspendFlag = true;
	   }
	   public synchronized void Resume() {
	      suspendFlag = false;
	       notify();
	   }
	}
}