import java.io.*;
import java.util.*;
import gnu.io.*;
import org.apache.log4j.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.TouchListener;

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
	String portName = "";
	int baudRate = 19200;

	public enum Parity {
		NONE, ODD, EVEN
	};

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

	protected Shell shlPortConfig;
	ArrayList<String> availableComPorts;

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void showConfigWindow() {
		Display display = Display.getDefault();
		createContents();

		shlPortConfig.open();
		shlPortConfig.layout();
		while (!shlPortConfig.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void showConfigWindow(ArrayList<String> availableComPorts) {
		if (availableComPorts != null) {
			this.availableComPorts = availableComPorts;
			showConfigWindow();
			logger.debug(getMethodName(1) + " port list is passed");
		} else {
			logger.debug(getMethodName(1) + " port list is null");
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPortConfig = new Shell();
		shlPortConfig.setMinimumSize(new Point(100, 30));
		shlPortConfig.setSize(378, 186);
		shlPortConfig.setText("Port Config");
		shlPortConfig.setLayout(new FormLayout());

		createRadioButtonGroups(shlPortConfig);
		createComboPortList(shlPortConfig);

	}

	private void createComboPortList(Shell shlPortConfig2) {
		Combo combo = new Combo(shlPortConfig, SWT.NONE);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(0, 10);
		fd_combo.left = new FormAttachment(0, 10);
		combo.setLayoutData(fd_combo);
		for (String s : this.availableComPorts) {
			combo.add(s);
		}
	}

	private void createRadioButtonGroups(Shell shlPortConfig2) {
		Group grpBaudRate = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpBaudRate = new FormData();
		fd_grpBaudRate.bottom = new FormAttachment(0, 119);
		fd_grpBaudRate.right = new FormAttachment(0, 90);
		grpBaudRate.setLayoutData(fd_grpBaudRate);
		grpBaudRate.setText("Baud Rate");

		Button rbBaud_19200 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_19200.addSelectionListener(changeConfigAdapter);
		
		rbBaud_19200.setBounds(10, 20, 60, 16);
		rbBaud_19200.setText("19200");
		rbBaud_19200.setData(19200);
		rbBaud_19200.setSelection(true);

		Button rbBaud_9600 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_9600.setBounds(10, 40, 60, 16);
		rbBaud_9600.setText("9600");
		rbBaud_9600.setData(9600);
		rbBaud_9600.addSelectionListener(changeConfigAdapter);
		
		Group grpParity = new Group(shlPortConfig, SWT.SHADOW_IN);
		FormData fd_grpParity = new FormData();
		fd_grpParity.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpParity.right = new FormAttachment(grpBaudRate, 92, SWT.RIGHT);
		fd_grpParity.left = new FormAttachment(grpBaudRate, 12);
		fd_grpParity.bottom = new FormAttachment(0, 148);
		grpParity.setLayoutData(fd_grpParity);
		grpParity.setText("Parity");

		Button rbParity_NONE = new Button(grpParity, SWT.RADIO);
		rbParity_NONE.setBounds(10, 20, 60, 16);
		rbParity_NONE.setText("NONE");
		rbParity_NONE.setData(0);
		rbParity_NONE.setSelection(true);
		rbParity_NONE.addSelectionListener(changeConfigAdapter);
		
		Button rbParity_ODD = new Button(grpParity, SWT.RADIO);
		rbParity_ODD.setBounds(10, 40, 60, 16);
		rbParity_ODD.setText("ODD");
		rbParity_ODD.setData(1);
		rbParity_ODD.addSelectionListener(changeConfigAdapter);
		
		Button rbParity_EVEN = new Button(grpParity, SWT.RADIO);
		rbParity_EVEN.setBounds(10, 60, 60, 16);
		rbParity_EVEN.setText("EVEN");
		rbParity_EVEN.setData(2);
		rbParity_EVEN.addSelectionListener(changeConfigAdapter);
		
		Group grpStopBits = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpStopBits = new FormData();
		fd_grpStopBits.left = new FormAttachment(grpParity, 6);
		fd_grpStopBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpStopBits.bottom = new FormAttachment(0, 119);
		grpStopBits.setLayoutData(fd_grpStopBits);
		grpStopBits.setText("Stop Bits");

		Button rbStop_ONE = new Button(grpStopBits, SWT.RADIO);
		rbStop_ONE.setBounds(10, 20, 60, 16);
		rbStop_ONE.setText("ONE");
		rbStop_ONE.setData(1);
		rbStop_ONE.setSelection(true);
		rbStop_ONE.addSelectionListener(changeConfigAdapter);
		
		Button rbStop_TWO = new Button(grpStopBits, SWT.RADIO);
		rbStop_TWO.setText("TWO");
		rbStop_TWO.setBounds(10, 40, 60, 16);
		rbStop_TWO.setData(2);
		rbStop_TWO.addSelectionListener(changeConfigAdapter);
		
		Group grpDataBits = new Group(shlPortConfig, SWT.NONE);
		fd_grpStopBits.right = new FormAttachment(grpDataBits, -6);
		FormData fd_grpDataBits = new FormData();
		fd_grpDataBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpDataBits.left = new FormAttachment(0, 274);
		fd_grpDataBits.bottom = new FormAttachment(0, 119);
		fd_grpDataBits.right = new FormAttachment(0, 354);
		grpDataBits.setLayoutData(fd_grpDataBits);
		grpDataBits.setText("Data Bits");

		Button rbDataBits_8 = new Button(grpDataBits, SWT.RADIO);
		rbDataBits_8.setText("8");
		rbDataBits_8.setBounds(10, 22, 60, 16);
		rbDataBits_8.setData(8);
		rbDataBits_8.setSelection(true);
		rbDataBits_8.addSelectionListener(changeConfigAdapter);
		
		Button rbDataBits_7 = new Button(grpDataBits, SWT.RADIO);
		rbDataBits_7.setText("7");
		rbDataBits_7.setBounds(10, 44, 60, 16);
		rbDataBits_7.setData(7);
		rbDataBits_7.addSelectionListener(changeConfigAdapter);
	}
	
	SelectionAdapter changeConfigAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent arg0) {
			//public void itemStateChanged(ItemEvent arg0) {
				Object[] args = new Object[2];
				Button item = (Button) arg0.getSource();
				
				if(item.getSelection()){
					args[0]=item.getText();
					args[1] = item.getData();
					
					logger.debug("Set " + item.getParent() + " to " + item.getData());
				}
			}
	};
	
	public ArrayList<String> listPorts() {
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

	/**
	 * @wbp.parser.entryPoint
	 */
	public void changeConfig() {
		ArrayList<String> portList = new ArrayList<String>();
		portList = listPorts();
		// ComPortShell comPortShell = new ComPortShell();
		// comPortShell.setPortList
		// comPortShell.open(portList);
		showConfigWindow(listPorts());

	}

	public boolean open(String portName) {
		boolean portFound = false;
		// String defaultPort = "/dev/term/a";
		/*
		 * if (((this.portName != null) && (this.portName == portName)) ||
		 * ((this.portName != null) && (this.portName != portName))) {
		 * serialPort.close(); }
		 */
		logger.debug(portName + " is passed to " + getMethodName(1));

		if (isConnected())
			serialPort.close();
		connected = false;

		if (portName.length() > 0) {

			// serialPort.close();

			this.portName = portName;
			portList = CommPortIdentifier.getPortIdentifiers();

			while (portList.hasMoreElements()) {
				portId = (CommPortIdentifier) portList.nextElement();

				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

					if (portId.getName().equals(this.portName)) {
						logger.debug("Found port " + this.portName);

						portFound = true;

						try {
							serialPort = (SerialPort) portId.open(portName,
									baudRate);
							logger.debug("SP isDTR = " + serialPort.isDTR());
							logger.debug("SP isCD = " + serialPort.isCD());
							logger.debug("SP isRTS = " + serialPort.isRTS());

							logger.debug("Port selected.");
							connectionStatusInfo = "Port selected.";
							connected = true;
						} catch (PortInUseException e) {
							logger.debug("Port in use.");
							connectionStatusInfo = "Port in use.";
							continue;
						}

						try {
							outputStream = serialPort.getOutputStream();
						} catch (IOException e) {
						}

						try {
							int localParity = 0;
							switch (parity.toString()) {
							case "NONE":
								localParity = 0;
								break;
							case "ODD":
								localParity = 1;
								break;
							case "EVEN":
								localParity = 2;
							default:
								localParity = 0;
								break;
							}
							serialPort.setSerialPortParams(baudRate, dataBits,
									stopBits, localParity);
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
				logger.debug("port " + this.portName + " not found.");
				connectionStatusInfo = "port " + this.portName + " not found.";
				connected = false;
			}

		} else {
			logger.debug("no Port assigned to function.\n\r");
			connectionStatusInfo = "no Port assigned to function.";
			connected = false;
		}

		return connected;
	}

	public boolean open(Object[] args) {
		switch ((String) args[0]) {
		case "PortName":
			this.portName = args[1].toString();
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		case "BaudRate":
			this.baudRate = Integer.parseInt(args[1].toString());
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		case "Parity":
			this.parity = Parity.valueOf(args[1].toString());
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		case "StopBits":
			this.stopBits = Integer.parseInt(args[1].toString());
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		case "DataBits":
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		default:
			logger.debug(args[0] + " " + args[1] + " passed to "
					+ getMethodName(1));
			break;
		}
		return open(this.portName);
	}

	public void close() {
		try {
			Thread.sleep(2000); // Be sure data is xferred before closing
		} catch (Exception e) {
		}

		serialPort.close();
		logger.debug("Closed Port " + this.portName);
		// System.exit(1);
	}

	public void write(String messageString) {

		logger.debug("Writing \"" + messageString + "\" to "
				+ serialPort.getName());

		try {
			outputStream.write(messageString.getBytes());
		} catch (IOException e) {
		}
	}

	public void writeln(String messageString) {
		write(messageString + "\n\r");
	}

	public void write(List<String> messageList) {
		for (String s : messageList) {
			write(s);
		}
	}

	private String getMethodName(int stack) {
		String functionName;
		functionName = (new Exception().getStackTrace()[stack].getClassName()
				+ "." + new Exception().getStackTrace()[stack].getMethodName());
		return functionName;
	}

	// class ComPortShell {


	// }
}
