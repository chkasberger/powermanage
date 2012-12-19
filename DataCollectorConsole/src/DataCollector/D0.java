package DataCollector;

import gnu.io.SerialPort;

public class D0 {
	private SerialPort serialPort;

	private String portName = "";
	private int baudRate = 300;
	private int parity = SerialPort.PARITY_EVEN;
	private int dataBits = SerialPort.DATABITS_7;
	private int stopBits = SerialPort.STOPBITS_1;
	private int maxBaudRate = 9600;
	private int interval = 60;

	public D0(){

	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public int getMaxBaudRate() {
		return maxBaudRate;
	}

	public void setMaxBaudRate(int maxBaudRate) {
		this.maxBaudRate = maxBaudRate;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
