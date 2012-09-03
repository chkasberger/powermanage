package DatacCllector;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

//import java.util.logging.Logger;
import org.apache.log4j.Logger;
import java.awt.Label;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.JButton;


public class ComPort {
	private static Logger logger = Logger.getRootLogger();
	private Thread reader;
	private boolean end = false;
	private int divider;
	private int id;
	private int[] tempBytes;
	int numTempBytes = 0, numTotBytes = 0;
	
	private Enumeration<?> portList;
	private CommPortIdentifier portId;
	//private SerialPort serialPort;
	private RXTXPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean outputBufferEmptyFlag = false;
	private boolean connected = false;
	private String connectionStatusInfo = null;
	private String portName = "";
	private int baudRate = 19200;
	private static int[] baudRateArray = {300,2400,9600,19200,115200};
	public static int[] getBaudRateArray() {
		return baudRateArray;
	}
	public enum Parity{NONE, ODD, EVEN, MARK, SPACE};
	public enum DataBits {
		FIVE(5), SIX(6), SEVEN(7), EIGHT(8);
	
		DataBits(int x){}
	}
	public enum StopBits {
		ONE(1), ONEPOINTFIVE(3), TWO(2);
	
		StopBits(int x){}
	}
	
	private int parity = 0;
	private int stopBits = 1;
	private int dataBits = 8;
	//protected Shell shlPortConfig;
	ArrayList<String> availableComPorts;
	//JCombo combo;
	boolean shellExists = false;
	static Label lblConnectionStatus;
	private Point location;
	
	
	public boolean isConnected() {
		return this.connected;
	}

	public String getConnectionStatusInfo() {
		return this.connectionStatusInfo;
	}

	public String getPortName() {
		return this.portName;
	}

	public int getBaudRate() {
		return this.baudRate;
	}

	public int getParity() {
		return this.parity;
	}

	public int getStopBits() {
		return this.stopBits;
	}

	public int getDataBits() {
		return this.dataBits;
	}

	
/*	
	private SelectionAdapter changeConfigAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent arg0) {
			Object[] args = new Object[2];
			Button item = (Button) arg0.getSource();

			if (item.getSelection()) {
				args[0] = item.getParent();
				args[1] = item.getData();

				logger.debug("Set " + args[0].toString() + " to "
						+ item.getData());

				switch (args[0].toString()) {
				case "Group {Baud Rate}":
					logger.debug("found Baud Rate");
					baudRate = (int) args[1];
					break;
				case "Group {Parity}":
					logger.debug("found Parity");
					parity = (int) args[1];
					break;
				case "Group {Stop Bits}":
					logger.debug("found Stop Bits");
					stopBits = (int) args[1];
					break;
				case "Group {Data Bits}":
					logger.debug("found Data Bits");
					dataBits = (int) args[1];
					break;
				default:
					logger.debug("foo bar");
					break;
				}
				reConfigurePortSettings();
			}
		}
	};

	/*
	 * private String getPortTypeName(int portType) { switch (portType) { case
	 * CommPortIdentifier.PORT_I2C: return "I2C"; case
	 * CommPortIdentifier.PORT_PARALLEL: return "Parallel"; case
	 * CommPortIdentifier.PORT_RAW: return "Raw"; case
	 * CommPortIdentifier.PORT_RS485: return "RS485"; case
	 * CommPortIdentifier.PORT_SERIAL: return "Serial"; default: return
	 * "unknown type"; } }
	 */

	/*
	public void configure() {
		if (!shellExists)
			showConfigWindow();
		else {			
			
		}
	}
	
	public void configure(Point location) {
		//this.location.x = location.x;
		//this.location.y = location.y;
		this.location = location;
		showConfigWindow();
		/*
		if (!shellExists){
			showConfigWindow();
		}
		else {			
			shlPortConfig.setVisible(true);
		}
		*/
//		shlPortConfig.setLocation(location);
//		shlPortConfig.redraw();

	/*S}
	*/
	public void configure(String string, int i) {
		// TODO Auto-generated method stub
		this.baudRate = i;
		try {
			open(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void reConfigurePortSettings() {
		try {
			if (open(this.portName)) {
				//lblConnectionStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
				lblConnectionStatus.setText("Port connected");
				// lblConnectionStatus.set
			} else {
				lblConnectionStatus.setText("Port not connected");
				//lblConnectionStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//shlPortConfig.redraw();
	}

	public void dispose() {
		//shlPortConfig.close();
		//shlPortConfig.dispose();
	}

	public static ArrayList<String> portList() {
		ArrayList<String> portList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			portList.add(portIdentifier.getName());
		}

		for (String s : portList) {
			logger.debug(s);
		}

		return portList;
	}

	public static ArrayList<Integer> baudList() {
		ArrayList<Integer> baudList = new ArrayList<Integer>();
		for (Integer i : baudRateArray) {
			baudList.add(i);
		}
		return baudList;
	}
	int x = 0;
	public boolean open(String portName) throws IOException {
		logger.debug("function open() called from " + JUtil.getMethodName(2));
		if (isConnected()) {
			serialPort.close();
			connected = false;
		}

		if (portName.length() > 0) {
			this.portName = portName;

			CommPortIdentifier portIdentifier;
			//boolean conn = false;
			try {
				portIdentifier = CommPortIdentifier
						.getPortIdentifier(this.portName);
				if (portIdentifier.isCurrentlyOwned()) {
					logger.debug(id + "Error: Port is currently in use");
				} else {
					serialPort = (RXTXPort) portIdentifier.open(
							"RTBug_network", 2000);
					serialPort. setSerialPortParams(baudRate, dataBits,
							stopBits, parity);

					inputStream = serialPort.getInputStream();
					outputStream = serialPort.getOutputStream();

					x++;
					logger.debug("this is the " + x + ". time of passing by");
					
					serialPort.addEventListener(new SerialPortEventListener() {
						
						@Override
						public void serialEvent(SerialPortEvent arg0) {
							// TODO Auto-generated method stub
							logger.debug("got event from com port!");
						}
					});
					
					
					//reader = (new Thread(new SerialReader(inputStream)));
					end = false;
					//reader.start();

					connected = true;
				}
			} catch (NoSuchPortException e) {
				logger.debug(id + "the connection could not be made");
				e.printStackTrace();
			} catch (PortInUseException e) {
				logger.debug(id + "the connection could not be made");
				e.printStackTrace();
			} catch (UnsupportedCommOperationException e) {
				logger.debug(id + "the connection could not be made");
				e.printStackTrace();
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return connected;
		
		/*
		boolean portFound = false;
		logger.debug(portName + " is passed to " + JUtil.getMethodName(1));

		if (isConnected()) {
			serialPort.close();
			connected = false;
		}

		if (portName.length() > 0) {
			this.portName = portName;
			portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

					if (portId.getName().equals(this.portName)) {
						logger.debug("Found port " + this.portName);

						portFound = true;

						try {
							logger.debug("try to connect to port "
									+ this.portName + " with baud rate "
									+ this.baudRate);
							serialPort = (SerialPort) portId.open(
									this.portName, 19200);
							connectionStatusInfo = "Port selected.";
							connected = true;
						} catch (PortInUseException e) {
							connectionStatusInfo = "Port in use.";
							connected = false;
							continue;
						}

						try {
							outputStream = serialPort.getOutputStream();
						} catch (IOException e) {
						}

						try {
							serialPort.setSerialPortParams(baudRate, dataBits,
									stopBits, parity);
							logger.debug(baudRate + "\n" + dataBits + "\n"
									+ stopBits + "\n" + parity);
						} catch (UnsupportedCommOperationException e) {
						}

						try {
							serialPort.notifyOnOutputEmpty(true);
						} catch (Exception e) {
							logger.debug("Error setting event notification");
							logger.debug(e.toString());
						}
					}
				}
			}

			if (!portFound) {
				connectionStatusInfo = "port " + this.portName + " not found.";
				connected = false;
			}

		} else {
			connectionStatusInfo = "no Port assigned to function.";
			connected = false;
		}

		logger.debug(connectionStatusInfo + "\n\r");

		return connected;
		*/
	}

	public boolean open(Object[] args) {
		boolean ack = false;
		switch ((String) args[0]) {
		case "PortName":
			this.portName = args[1].toString();
			break;
		case "BaudRate":
			this.baudRate = Integer.parseInt(args[1].toString());
			break;
		case "Parity":
			this.parity = Integer.parseInt(args[1].toString());
			break;
		case "StopBits":
			this.stopBits = Integer.parseInt(args[1].toString());
			break;
		case "DataBits":
			this.dataBits = Integer.parseInt(args[1].toString());
			break;
		default:
			break;
		}
		logger.debug(args[0] + " " + args[1] + " passed to "
				+ JUtil.getMethodName(1));
		try {
			ack = open(this.portName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ack;
	}

	private class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1, i, temp;
			try {
				logger.debug("foo-Thread-Bar");
				while (!end) {
					if ((in.available()) > 0) {
						if ((len = this.in.read(buffer)) > -1) {
							for (i = 0; i < len; i++) {
								temp = buffer[i];
								// adjust from C-Byte to Java-Byte
								if (temp < 0)
									temp += 256;
								if (temp == divider) {
									if (numTempBytes > 0) {
										// contact.parseInput(id, numTempBytes,
										// tempBytes);
									}
									numTempBytes = 0;
								} else {
									tempBytes[numTempBytes] = temp;
									++numTempBytes;
								}
							}
						}
					}
				}
			} catch (IOException e) {
				end = true;
				try {
					outputStream.close();
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				serialPort.close();
				connected = false;
				// contact.networkDisconnected(id);
				// contact.writeLog(id, "connection has been interrupted");
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
		logger.debug("Closed Port " + this.portName);
	}

	public void write(String messageString) {
		try {
			
			int[] intArray = new int[messageString.length()/2];
			String[] stringArray = new String[messageString.length()/2];
			byte[] byteArray = new byte[messageString.length()/2];
			String x = null;
			
			//String[] subString = null;
			for(int i = 0;  i < stringArray.length; i++){
				stringArray[i] = "0x" + messageString.substring(i*2,i*2+2);
				x = messageString.substring(i*2,i*2+1);
				intArray[i] = Integer.decode(stringArray[i]);
				byteArray[i] = (byte)intArray[i];
			}
			
			try {
				outputStream.write(byteArray);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logger.debug(messageString);
			logger.debug("hashCode of outputstream is: " + outputStream.hashCode());
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.fatal("no port assigned");
		}
	}
    public static byte[] convertHexToBytes(String hex) {
        byte[] result = null;
        if (hex != null) {
            // remove all non alphanumeric chars like colons, whitespace, slashes
            hex = hex.replaceAll("[^a-zA-Z0-9]", "");
            // from http://forums.sun.com/thread.jspa?threadID=546486
            // (using BigInteger to convert to byte array messes up by adding extra 0 if first byte > 7F and this method
            //  will not rid of leading zeroes like the flawed method byte[] bts = new BigInteger(hex, 16).toByteArray();)
            result = new byte[hex.length() / 2];
            for (int i = 0; i < result.length; i++) {
                result[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
            }
        }
		System.out.println("lenientHexToBytes(" + hex + ") returned '"
				+ new String(result) + "'");
		for (byte b : result) {
			 int i = b & 0xFF;	
			 
			logger.debug(String.format("fooBarResultOfConversion\t" + b + "\t" + Integer.toHexString(i)));
		}
		logger.debug(String.format("fooBarResultOfConversion" + result.toString(),16));
        return result;
    }
	public void write(byte message) {
		String hexStr = "02303033434333d3037313131303131323639393130333131303139033f";
		byte[] unsignedByte = convertHexToBytes(hexStr);
		try {
			serialPort.getOutputStream().write(unsignedByte);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serialPort.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}	
	
	public void write(byte[] message) {
		try {
			//serialPort.getOutputStream().write(unsignedByte);
			serialPort.getOutputStream().write(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serialPort.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	public enum ReturnType {
	    BYTE, STRING, ASCII 
	}
	
	public Object read(ReturnType returnType) {
		Object returnValue = "";
		//byte[] buffer = new byte[0xff];
		
		try {
			Thread.sleep(1000);
			while(inputStream.available() > 0){
				int buffer = inputStream.read();
				logger.debug(buffer);
				returnValue = returnValue + " " + String.format("%h", buffer); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;	
	}
}
