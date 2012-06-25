import java.io.*;
//import java.lang.reflect.Array;
import java.util.*;

import org.apache.log4j.net.SyslogAppender;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

//import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import gnu.io.*;

public class ComPortSelection extends SelectionAdapter {
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
	int parity = 0;
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
		changeConfig();
	}

	public int getBaudRate() {
		return this.baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
		//serialPort.setBaudBase(this.baudRate);
		changeConfig();
	}

	public int getParity() {
		return this.parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
		changeConfig();
	}

	public int getStopBits() {
		return this.stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
		changeConfig();
	}

	public int getDataBits() {
		return this.dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
		changeConfig();
	}



	public void widgetSelected(SelectionEvent event) {
		MenuItem item = (MenuItem) event.widget;
		if (item.getSelection()) {

			System.out.print(item.getText() + "\n\r");
			System.out.print(item.getAccelerator() + "\n\r");
			System.out.print(item.getMenu() + "\n\r");
			System.out.print(item.getID() + "\n\r");
			System.out.print(item.getParent() + "\n\r");
			//System.out.print(item.setMenu(menu))
			open("");
		}
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
		switch (portType) {
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

	private void changeConfig(){
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
							System.out.println( "Port in use.");
							connectionStatusInfo = "Port in use.";
							continue;
						}

						try {
							outputStream = serialPort.getOutputStream();
						} catch (IOException e) {
						}

						try {
							serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
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
