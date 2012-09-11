package DatacCollector;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.awt.Label;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;


public class ComPort implements ActionListener{
	private static Logger logger = Logger.getRootLogger();
	private Thread reader;
	private final boolean end = false;
	private int divider;
	private int id;
	private int[] tempBytes;
	int numTempBytes = 0, numTotBytes = 0;
	CommPortIdentifier portIdentifier = null;
	CommPort commPort = null;
	//private SerialPort serialPort;
	private static SerialPort serialPort;
	public InputStream in;
	public DataInputStream dIn;
	public BufferedInputStream bIn;
	public OutputStream out;


	private final boolean outputBufferEmptyFlag = false;
	private final boolean connected = false;
	private final String connectionStatusInfo = null;
	private String portName = "";
	private int baudRate = 9600;
	private static int[] baudRateArray = {300,2400,9600,19200,115200};
	public static int[] getBaudRateArray() {
		return baudRateArray;
	}
	public static enum Parity {NONE, ODD, EVEN, MARK, SPACE}
	public static enum DataBits {SEVEN, EIGHT}
	public enum StopBits {ONE, ONEPOINTFIVE, TWO}

	private int parity = SerialPort.PARITY_EVEN;
	private int stopBits = SerialPort.STOPBITS_1;
	private int dataBits = SerialPort.DATABITS_7;

	boolean shellExists = false;
	static Label lblConnectionStatus;
	private Point location;


	public boolean isConnected() {
		return connected;
	}

	public String getConnectionStatusInfo() {
		return connectionStatusInfo;
	}

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

	public ComPort()
	{
		//portList();
	}
	public void dispose() {
		//shlPortConfig.close();
		//shlPortConfig.dispose();
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

	public void connect(Object portName, Object baudRate, Parity parity,
			DataBits dataBits, StopBits stopBits) {

		if(portName != null)
			this.portName = portName.toString();
		else
			this.portName = portList().get(0);

		if ((int)baudRate > 0)
			this.baudRate = (int)baudRate;
		else
			this.baudRate = 9600;

		switch(parity){
		case NONE: this.parity = SerialPort.PARITY_NONE; break;
		case EVEN: this.parity = SerialPort.PARITY_EVEN; break;
		case ODD: this.parity = SerialPort.PARITY_ODD; break;
		default: this.parity = SerialPort.PARITY_NONE;
		}
		switch(dataBits){
		case SEVEN: this.dataBits = SerialPort.DATABITS_7; break;
		case EIGHT: this.dataBits = SerialPort.DATABITS_8; break;
		default: this.dataBits = SerialPort.DATABITS_8;
		}
		switch(stopBits){
		case ONE: this.stopBits = SerialPort.STOPBITS_1; break;
		case ONEPOINTFIVE: this.stopBits = SerialPort.STOPBITS_1_5; break;
		case TWO: this.stopBits = SerialPort.STOPBITS_2; break;
		default: this.stopBits = SerialPort.STOPBITS_1;
		}

		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(this.portName);
		} catch (NoSuchPortException e) {
			e.printStackTrace();
		}

		if ( portIdentifier.isCurrentlyOwned() )
		{
			//System.out.println("Error: Port is currently in use");
			commPort.close();
		}
		else
		{
			try {
				commPort = portIdentifier.open(this.getClass().getName(),2000);
			} catch (PortInUseException e) {
				e.printStackTrace();
			}

			if ( commPort instanceof SerialPort )
			{
				serialPort = (SerialPort) commPort;

				try {
					serialPort.setSerialPortParams(this.baudRate,this.dataBits,this.stopBits,this.parity);
					logger.debug(serialPort.getName() + " " + serialPort.getBaudRate() + " " + serialPort.getDataBits() + " " + serialPort.getStopBits() + " " + serialPort.getParity());

					in = serialPort.getInputStream();
					out = serialPort.getOutputStream();
					(new Thread(new SerialReader(in))).start();
					(new Thread(new SerialWriter(out))).start();
				} catch (IOException | UnsupportedCommOperationException e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/** */
	public static class SerialReader implements Runnable
	{
		InputStream in;
		String stringBuffer;

		public synchronized String getStringBuffer() {
			return stringBuffer;
		}

		public SerialReader ( InputStream in )
		{
			this.in = in;
		}

		@Override
		public void run ()
		{
			byte[] buffer = new byte[1024];
			int len = -1;
			try
			{
				File file = new File("./java.log");
				/*try {
					System.setOut(new PrintStream(new FileOutputStream(file,true)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				 */

				while ( ( len = in.read(buffer)) > -1 )
				{
					serialPort.setRTS(false);
					serialPort.setDTR(true);

					stringBuffer = new String(buffer, 0, len);

					System.out.print(stringBuffer);
					FileOutputStream fileOutputStream = new FileOutputStream(file,true);
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					for (int i=0; i < stringBuffer.length(); i++){
						bufferedOutputStream.write((byte)stringBuffer.charAt(i));
					}
					bufferedOutputStream.close();

					ActionEvent ae = new ActionEvent(this, 0, "Bytes received");

					//OutputStream bout;
					//DataOutputStream dOut = new DataOutputStream(new BufferedOutputStream((bout.write(buffer, 0, len))));
					//BufferedOutputStream bOut;
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}


	}

	/** */
	public static class SerialWriter implements Runnable
	{
		OutputStream out;

		public SerialWriter ( OutputStream out )
		{
			this.out = out;
		}

		@Override
		public void run ()
		{
			try
			{
				int c = 0;
				while ( ( c = System.in.read()) > -1 )
				{
					serialPort.setRTS(true);
					serialPort.setDTR(false);
					out.write(c);


				}
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		serialPort.close();
		logger.debug("Closed Port " + portName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


}

