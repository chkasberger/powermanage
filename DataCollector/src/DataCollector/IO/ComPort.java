package DataCollector.IO;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;


public class ComPort{
	private static Logger logger = Logger.getRootLogger();

	private SerialPort serialPort;
	public InputStream in;
	public DataInputStream dIn;
	public BufferedInputStream bIn;
	public OutputStream out;
	public PipedInputStream write;

	public SerialReader serialReader;
	public SerialWriter serialWriter;

	private String portName = "";
	private int baudRate = 9600;
	private static int[] baudRates = {300,2400,4800,9600,19200,115200};
	public static int[] getBaudRates() {
		return baudRates;
	}
	public enum Parity {NONE, ODD, EVEN, MARK, SPACE}
	public enum DataBits {SEVEN, EIGHT}
	public enum StopBits {ONE, ONEPOINTFIVE, TWO}

	private int parity = SerialPort.PARITY_EVEN;
	private int dataBits = SerialPort.DATABITS_7;
	private int stopBits = SerialPort.STOPBITS_1;

	public String getPortName() {return portName;}
	public int getBaudRate() {return baudRate;}
	public int getParity() {return parity;}
	public int getStopBits() {return stopBits;}
	public int getDataBits() {return dataBits;}
	public ComPort() {

	}

	public void dispose() {
		//shlPortConfig.close();
		//shlPortConfig.dispose();
	}
	/**
	 * @description create listener components
	 */
	protected static EventListenerList listenerList = new EventListenerList();
	public void addComPortEventListener(ComPortEventListener listener){
		listenerList.add(ComPortEventListener.class, listener);
	}
	public void removeComPortEventListener(ComPortEventListener listener){
		listenerList.remove(ComPortEventListener.class, listener);
	}
	static void fireComPortEvent(ComPortEvent evt){
		Object[] listeners = listenerList.getListenerList();
		for(int i=0; i<listeners.length; i+=2){
			if(listeners[i]==ComPortEventListener.class){
				((ComPortEventListener)listeners[i+1]).comPortEventFired(evt);
			}
		}
	}


	/**
	 * @return Returns all available serial ports of the local machine
	 */
	public static ArrayList<String> portList() {

		ArrayList<String> availableComPorts = new ArrayList<>();
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();


			if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				availableComPorts.add(portIdentifier.getName());
			}
		}
		return availableComPorts;
	}

	/**
	 * @description manages the serial port connectivity
	 */
	public void connect(Object portName, Object baudRate, Object parity,
			Object dataBits, Object stopBits) throws PortInUseException {

		portSettings(portName, baudRate, parity, dataBits, stopBits);

		Thread thisThread = Thread.currentThread();
		thisThread.setPriority(2);

		try {
			if(serialPort!=null){
				serialReader.interrupt();
				serialWriter.interrupt();
				serialPort.close();
				logger.debug(serialPort.getName() + " is closed!");
			}

			//String SerialPortID = this.portName;
			//System.setProperty("gnu.io.rxtx.SerialPorts", SerialPortID);

			/** Identifies and opens the desired port */
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 1000);

			if (commPort instanceof SerialPort )
			{
				serialPort = (SerialPort) commPort;
				logger.debug(serialPort.getName() + " is open");

				try {
					/** Configure the opened serial port */

					portSettings(portName, baudRate, parity, dataBits, stopBits);

					//serialPort.addEventListener(this);
					//serialPort.notifyOnDataAvailable(true);


					/** configure accessibility to serial port in/out stream */
					in = serialPort. getInputStream();
					out = serialPort.getOutputStream();
					serialReader = new SerialReader(in, "SerialReader");
					serialWriter = new SerialWriter(out, "SerialWriter");

					/** starts threads for read and write process */
					serialReader.start();
					//serialWriter.start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}

		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}
	}



	/** Parses the port settings */
	public void portSettings(Object portName, Object baudRate, Object parity, Object dataBits, Object stopBits) {
		if(portName != null)
		{
			this.portName = portName.toString();
		}else{
			this.portName = portList().get(0);
		}
		if ((int)baudRate > 0)
			this.baudRate = (int)baudRate;
		else
			this.baudRate = 9600;

		switch((Parity)parity){
		case NONE: this.parity = SerialPort.PARITY_NONE; break;
		case EVEN: this.parity = SerialPort.PARITY_EVEN; break;
		case ODD: this.parity = SerialPort.PARITY_ODD; break;
		default: this.parity = SerialPort.PARITY_NONE;
		}
		switch((DataBits)dataBits){
		case SEVEN: this.dataBits = SerialPort.DATABITS_7; break;
		case EIGHT: this.dataBits = SerialPort.DATABITS_8; break;
		default: this.dataBits = SerialPort.DATABITS_8;
		}
		switch((StopBits)stopBits){
		case ONE: this.stopBits = SerialPort.STOPBITS_1; break;
		case ONEPOINTFIVE: this.stopBits = SerialPort.STOPBITS_1_5; break;
		case TWO: this.stopBits = SerialPort.STOPBITS_2; break;
		default: this.stopBits = SerialPort.STOPBITS_1;
		}


		try {
			if(serialPort != null){
				serialPort.setRTS(false);
				serialPort.setDTR(false);
				serialPort.setSerialPortParams(this.baudRate,this.dataBits,this.stopBits,this.parity);
				serialPort.setRTS(true);
				serialPort.setDTR(true);				logger.debug(serialPort.getName() + " is configured with: " + serialPort.getBaudRate() + " " + serialPort.getDataBits() + " " + serialPort.getStopBits() + " " + serialPort.getParity());
			}
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** Class for serial read process */
	public class SerialReader extends Thread //implements Runnable
	{
		Thread thisThread;

		InputStream in;

		public SerialReader ( InputStream in, String threadName )
		{
			super(threadName);
			thisThread = SerialReader.currentThread();
			thisThread.setPriority(3);
			this.in = in;
		}

		@Override
		public void run ()
		{
			byte[] buffer = new byte[9600];
			int len = -1;
			try
			{
				File file = new File("./java.log");
				String frame = new String();
				while (!this.isInterrupted())
				{
					while((len = in.read(buffer,0,1)) > 0)
					{
						//logger.debug("foo Ya Reader Man!");
						String stringBuffer = new String(buffer, 0, len);

						if(stringBuffer.length() > 0)
						{
							frame += stringBuffer;
						}

						Pattern identificationMessagePattern = Pattern.compile("/[\\w]{3}[0-9a-iA-I].{16}\r\n");
						Matcher identificationMessageMatcher = identificationMessagePattern.matcher(frame);

						Pattern acknowledgePattern = Pattern.compile("ACK^");

						/** Filter identification message and fire ComPortEvent */
						//if(frame.matches(identificationMessage))
						if(identificationMessageMatcher.find())
						{
							System.out.println("--| " + frame + ": strings are equal");
							ComPortEvent foo = new ComPortEvent(frame);
							fireComPortEvent(foo);
							frame = new String();
							//out.write(new byte[] {0x06, 0x30, 0x33, 0x30, 0x0d, 0x0a});
							serialPort.getOutputStream().write(new byte[] {0x06, 0x30, 0x35, 0x30, 0x0d, 0x0a});
							serialPort.getOutputStream().flush();

						}

						if(frame.contains("050\r\n")){
							System.out.print("--> " + frame);
							ComPortEvent foo = new ComPortEvent(frame);
							fireComPortEvent(foo);
							frame = new String();
							portSettings(portName, 9600, Parity.EVEN, DataBits.SEVEN, StopBits.ONE);
						}

						if(frame.endsWith("!\r\n")){
							System.out.println("--> " + frame);
							ComPortEvent foo = new ComPortEvent(frame);
							fireComPortEvent(foo);
							frame = new String();
						}

						if(frame.endsWith(")\r\n")){
							System.out.print("--> " + frame);
							ComPortEvent foo = new ComPortEvent(frame);
							fireComPortEvent(foo);
							frame = new String();
						}

						FileOutputStream fileOutputStream = new FileOutputStream(file,true);
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

						for (int i=0; i < stringBuffer.length(); i++){
							bufferedOutputStream.write((byte)stringBuffer.charAt(i));
						}
						bufferedOutputStream.close();
					}
				}
			}
			catch ( IOException e )
			{
				//System.out.println("foo you reader");
				e.printStackTrace();
			}
			thisThread = null;
		}
	}

	/** Class for serial write process */
	public class SerialWriter extends Thread //implements Runnable
	{
		Thread thisThread;
		public OutputStream out;

		public SerialWriter ( OutputStream out, String threadName )
		{
			super(threadName);
			thisThread = SerialReader.currentThread();
			this.out = out;
		}
		@Override
		public void run ()
		{
			while (!this.isInterrupted()){
				try
				{
					int c = 0;
					while (( c = in.read()) > -1)
					{
						System.out.println(out);
						logger.debug("foo Ya Writer Man!");
					}
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}
			thisThread = null;
		}
	}



}




