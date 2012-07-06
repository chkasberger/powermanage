import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

//import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Label;


public class ComPort {
	private static Logger logger = Logger.getRootLogger();

	private Enumeration<?> portList;
	private CommPortIdentifier portId;
	private SerialPort serialPort;
	private OutputStream outputStream;
	private boolean outputBufferEmptyFlag = false;
	private boolean connected = false;
	private String connectionStatusInfo = null;
	private String portName = "";
	private int baudRate = 19200;
	private int parity = 0;
	private int stopBits = 1;
	private int dataBits = 8;
	protected Shell shlPortConfig;
	ArrayList<String> availableComPorts;
	Combo combo;
	boolean shellExists = false;
	static Label lblConnectionStatus;
	
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

	public void showConfigWindow(ArrayList<String> availableComPorts) {
		if (availableComPorts != null) {
			this.availableComPorts = availableComPorts;
			Display display = Display.getDefault();
			createContents();

			shlPortConfig.open();
			shlPortConfig.layout();
			shellExists = true;
			
			while (!shlPortConfig.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			logger.debug(JUtil.getMethodName(1) + " port list is passed");
		} else {
			logger.debug(JUtil.getMethodName(1) + " port list is null");
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createContents() {
		shlPortConfig = new Shell(SWT.BORDER);
		shlPortConfig.setTouchEnabled(true);
		shlPortConfig.setMinimumSize(new Point(100, 30));
		shlPortConfig.setSize(364, 133);
		shlPortConfig.setText("Port Config");
		shlPortConfig.setLayout(new FormLayout());

		createRadioButtonGroups(shlPortConfig);
		createComboPortList(shlPortConfig);

		Button btnTest = new Button(shlPortConfig, SWT.NONE);
		btnTest.setText("TEST");
		FormData fd_btnTest = new FormData();
		fd_btnTest.top = new FormAttachment(combo, -2, SWT.TOP);
		btnTest.setLayoutData(fd_btnTest);
		
		Button btnHide = new Button(shlPortConfig, SWT.NONE);
		fd_btnTest.right = new FormAttachment(btnHide, -6);
		btnHide.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlPortConfig.setVisible(false);
			}
		});
		FormData fd_btnHide = new FormData();
		fd_btnHide.left = new FormAttachment(100, -39);
		fd_btnHide.bottom = new FormAttachment(grpDataBits);
		fd_btnHide.right = new FormAttachment(100, -14);
		btnHide.setLayoutData(fd_btnHide);
		btnHide.setText("X");
		
		lblConnectionStatus = new Label(shlPortConfig, SWT.NONE);
		FormData fd_lblConnectionStatus = new FormData();
		fd_lblConnectionStatus.bottom = new FormAttachment(btnTest, 20);
		fd_lblConnectionStatus.left = new FormAttachment(combo, 6);
		lblConnectionStatus.setLayoutData(fd_lblConnectionStatus);
		lblConnectionStatus.setText("not connected");
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				write("TEST MESSAGE\n\r");
			}
		});
	}

	private void createComboPortList(Shell shlPortConfig2) {
		combo = new Combo(shlPortConfig, SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(grpBaudRate, 90);
		fd_combo.left = new FormAttachment(grpBaudRate, 0, SWT.LEFT);
		//fd_combo.bottom = new FormAttachment(grpDataBits);
		fd_combo.top = new FormAttachment(0, 10);
		combo.setLayoutData(fd_combo);
		combo.add("");

		for (String s : this.availableComPorts) {
			combo.add(s);
			logger.debug("Combo Background is " + combo.getForeground());
		}
		
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Combo item = (Combo) arg0.getSource();
				logger.info("Set " + item.getText());
				open(item.getText());

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
	}

	private FormData fd_grpParity;
	private Group grpBaudRate;
	private Group grpDataBits;

	private void createRadioButtonGroups(Shell shlPortConfig2) {
		grpBaudRate = new Group(shlPortConfig, SWT.NONE);
		FormData fd_grpBaudRate = new FormData();
		fd_grpBaudRate.bottom = new FormAttachment(combo, 120);
		fd_grpBaudRate.top = new FormAttachment(combo, 35);
		fd_grpBaudRate.left = new FormAttachment(0, 17);
		fd_grpBaudRate.right = new FormAttachment(0, 90);
		grpBaudRate.setLayoutData(fd_grpBaudRate);
		grpBaudRate.setText("Baud Rate");

		Button rbBaud_57600 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_57600.addSelectionListener(changeConfigAdapter);
		rbBaud_57600.setBounds(10, 20, 60, 16);
		rbBaud_57600.setText("57600");
		rbBaud_57600.setData(57600);

		Button rbBaud_19200 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_19200.addSelectionListener(changeConfigAdapter);
		rbBaud_19200.setBounds(10, 40, 60, 16);
		rbBaud_19200.setText("19200");
		rbBaud_19200.setData(19200);
		rbBaud_19200.setSelection(true);

		Button rbBaud_9600 = new Button(grpBaudRate, SWT.RADIO);
		rbBaud_9600.setBounds(10, 60, 60, 16);
		rbBaud_9600.setText("9600");
		rbBaud_9600.setData(9600);
		rbBaud_9600.addSelectionListener(changeConfigAdapter);

		grpParity = new Group(shlPortConfig, SWT.SHADOW_IN);
		fd_grpParity = new FormData();
		fd_grpParity.left = new FormAttachment(grpBaudRate, 6);
		fd_grpParity.bottom = new FormAttachment(grpBaudRate, 0, SWT.BOTTOM);
		fd_grpParity.top = new FormAttachment(0, 35);
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
		fd_grpParity.right = new FormAttachment(100, -192);
		FormData fd_grpStopBits = new FormData();
		fd_grpStopBits.left = new FormAttachment(grpParity, 6);
		fd_grpStopBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpStopBits.bottom = new FormAttachment(0, 120);
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

		grpDataBits = new Group(shlPortConfig, SWT.NONE);
		fd_grpStopBits.right = new FormAttachment(100, -106);
		FormData fd_grpDataBits = new FormData();
		fd_grpDataBits.bottom = new FormAttachment(grpBaudRate, 0, SWT.BOTTOM);
		fd_grpDataBits.top = new FormAttachment(grpBaudRate, 0, SWT.TOP);
		fd_grpDataBits.left = new FormAttachment(grpStopBits, 6);
		fd_grpDataBits.right = new FormAttachment(0, 348);
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
	private Group grpParity;

	/*
	 * private String getPortTypeName(int portType) { switch (portType) { case
	 * CommPortIdentifier.PORT_I2C: return "I2C"; case
	 * CommPortIdentifier.PORT_PARALLEL: return "Parallel"; case
	 * CommPortIdentifier.PORT_RAW: return "Raw"; case
	 * CommPortIdentifier.PORT_RS485: return "RS485"; case
	 * CommPortIdentifier.PORT_SERIAL: return "Serial"; default: return
	 * "unknown type"; } }
	 */

	public void configure() {
		if (!shellExists)
			showConfigWindow(listPorts());
		else
			shlPortConfig.setVisible(true);
	}

	protected void reConfigurePortSettings() {
		if(open(this.portName)){
			lblConnectionStatus.setText("Port connected");
		}else{
			lblConnectionStatus.setText("Port not connected");
		}
		
	}
	
	public void dispose(){
		shlPortConfig.close();
		shlPortConfig.dispose();
	}
	
	private ArrayList<String> listPorts() {
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
	
	public boolean open(String portName) {
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
	}

	public boolean open(Object[] args) {
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
		return open(this.portName);
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
			outputStream.write(messageString.getBytes());
			logger.debug(messageString);
		} catch (IOException e) {
			e.printStackTrace();
			logger.fatal("write not possible");
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.fatal("no port assigned");
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
}
