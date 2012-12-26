package DataCollector.XML;

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

	public synchronized void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public synchronized String getPortName() {
		return portName;
	}

	public synchronized void setPortName(String portName) {
		this.portName = portName;
	}

	public synchronized int getParity() {
		return parity;
	}

	public synchronized void setParity(int parity) {
		this.parity = parity;
	}

	public synchronized int getDataBits() {
		return dataBits;
	}

	public synchronized void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public synchronized int getStopBits() {
		return stopBits;
	}

	public synchronized void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public synchronized SerialPort getSerialPort() {
		return serialPort;
	}

	public synchronized void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public synchronized int getMaxBaudRate() {
		return maxBaudRate;
	}

	public synchronized void setMaxBaudRate(int maxBaudRate) {
		this.maxBaudRate = maxBaudRate;
	}

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}
}
