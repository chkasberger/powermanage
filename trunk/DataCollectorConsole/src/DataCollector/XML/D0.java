/*******************************************************************************
 * Copyright (c) 2013 Christian Kasberger.
 * All rights reserved. This program and the accompanying materials are made available for non comercial use!
 *******************************************************************************/
package DataCollector.XML;

import gnu.io.SerialPort;
import DataCollector.IO.ComPort.DataBits;
import DataCollector.IO.ComPort.Parity;
import DataCollector.IO.ComPort.StopBits;

public class D0 {
	private SerialPort serialPort;

	private String portName = "";
	private int baudRate = 300;
	private Parity parity = Parity.EVEN;
	private DataBits dataBits = DataBits.SEVEN;
	private StopBits stopBits = StopBits.ONE;
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

	public synchronized Parity getParity() {
		return parity;
	}

	public synchronized void setParity(Parity parity) {
		this.parity = parity;
	}

	public synchronized DataBits getDataBits() {
		return dataBits;
	}

	public synchronized void setDataBits(DataBits dataBits) {
		this.dataBits = dataBits;
	}

	public synchronized StopBits getStopBits() {
		return stopBits;
	}

	public synchronized void setStopBits(StopBits stopBits) {
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
