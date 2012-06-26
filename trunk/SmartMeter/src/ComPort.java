import java.io.*;
import java.util.*;
import gnu.io.*;
import org.apache.log4j.*;

public class ComPort {
	private static Logger logger = Logger.getRootLogger();
		
	Enumeration<?> portList;
	CommPortIdentifier portId;
	String messageString = "Hello, world!";
	SerialPort serialPort;
	OutputStream outputStream;
	boolean outputBufferEmptyFlag = false;
	boolean connected = false;
	String connectionStatusInfo = null;
	String portName;
	int baudRate = 19200;
	enum Parity{NONE,ODD,EVEN};
		
	Parity parity = Parity.NONE;
	int stopBits = 1;
	int dataBits = 8;

	public boolean isConnected() {
		
		return this.connected;
	}

	public String getConnectionStatusInfo() {
		return this.connectionStatusInfo;
	}

	public String getPortName() {
		return this.portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getBaudRate() {
		return this.baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public Parity getParity() {
		return this.parity;
	}

	public void setParity(Parity parity) {
		this.parity = parity;
	}

	public int getStopBits() {
		return this.stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getDataBits() {
		return this.dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public ArrayList<String> listPorts() {
		ArrayList<String> portList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			portList.add(portIdentifier.getName());
		}

		for (String s : portList) {
			System.out.println(s);
		}

		return portList;
	}

	private String getPortTypeName(int portType) {
		switch (portType)
			{
			case CommPortIdentifier.PORT_I2C:
				return "I2C";
			case CommPortIdentifier.PORT_PARALLEL:
				return "Parallel";
			case CommPortIdentifier.PORT_RAW:
				return "Raw";
			case CommPortIdentifier.PORT_RS485:
				return "RS485";
			case CommPortIdentifier.PORT_SERIAL:
				return "Serial";
			default:
				return "unknown type";
			}
	}

	private void changeConfig(String name, float value) {
		open(this.portName);
	}

	public boolean open(String portName) {
		boolean portFound = false;
		// String defaultPort = "/dev/term/a";
		if (((this.portName != null) && (this.portName == portName))
				|| ((this.portName != null) && (this.portName != portName))) {
			serialPort.close();
		}

		if (portName.length() > 0) {
			this.portName = portName;
			portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

					if (portId.getName().equals(this.portName)) {
						System.out.println("Found port " + this.portName);

						portFound = true;

						try {
							serialPort = (SerialPort) portId.open("SimpleWrite", 2000);
							System.out.println("Port selected.");
							connectionStatusInfo = "Port selected.";
							connected = true;
						} catch (PortInUseException e) {
							System.out.println("Port in use.");
							connectionStatusInfo = "Port in use.";
							continue;
						}

						try {
							outputStream = serialPort.getOutputStream();
						} catch (IOException e) {
						}

						try {
							serialPort.setSerialPortParams(baudRate, dataBits, stopBits, 1);							
						} catch (UnsupportedCommOperationException e) {
						}

						try {
							serialPort.notifyOnOutputEmpty(true);
						} catch (Exception e) {
							System.out.println("Error setting event notification");
							System.out.println(e.toString());
						}
					}
				}
			}

			if (!portFound) {
				System.out.println("port " + this.portName + " not found.");
				connectionStatusInfo = "port " + this.portName + " not found.";
				connected = false;
			}

		} else {
			System.out.println("no Port assigned to function.\n\r");
			connectionStatusInfo = "no Port assigned to function.";
			connected = false;
		}

		return connected;
	}

	public boolean open(Object[] args) {
		switch ((String) args[0])
			{
			case "PortName":
				this.portName = (String) args[1];
				logger.debug("New " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			case "BaudRate":
				this.baudRate = Integer.parseInt(args[1].toString());
				logger.debug("New " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			case "Parity":
				this.baudRate = Integer.parseInt(args[1].toString());
				logger.debug("New " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			case "StopBits":
				this.stopBits = Integer.parseInt(args[1].toString());
				logger.debug("New " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			case "DataBits":
				this.dataBits = Integer.parseInt(args[1].toString());
				logger.debug("New " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			default:
				logger.debug("Unknown name " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				
				System.out.println("Unknown name " + args[0] + " with value " + args[1] + " passed to method "
						+ new Exception().getStackTrace()[0].getMethodName());
				break;
			}
		return true;
	}

	public void close() {
		try {
			Thread.sleep(2000); // Be sure data is xferred before closing
		} catch (Exception e) {
		}

		serialPort.close();
		System.out.println("Closed Port " + this.portName);
		// System.exit(1);
	}

	public void write(String messageString) {

		System.out.println("Writing \"" + messageString + "\" to " + serialPort.getName());

		try {
			outputStream.write(messageString.getBytes());
		} catch (IOException e) {
		}
	}

	public void write(List<String> messageList) {
		for (String s : messageList) {
			write(s);
		}
	}
}
