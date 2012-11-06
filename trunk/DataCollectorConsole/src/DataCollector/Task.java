package DataCollector;

import java.io.IOException;
import java.util.TimerTask;

import DataCollector.IO.ComPort;

public class Task extends TimerTask {

	private final ComPort Port;
	Object portName;
	Object baudRate;
	Object parity;
	Object dataBits;
	Object stopBits;

	/**
	 * Constructs the object, sets the string to be output in function run()
	 * @param str
	 */
	Task(ComPort _Port, Object portName, Object baudRate, Object parity, Object dataBits, Object stopBits) {
		this.portName = portName;
		this.baudRate = baudRate;
		this.parity = parity;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		Port = _Port;
	}

	/**
	 * When the timer executes, this code is run.
	 */
	@Override
	public void run() {
		try {
			Port.portSettings(portName, baudRate, parity, dataBits, stopBits);

			Port.out.write(new byte[] {0x2f, 0x3f, 0x21, 0x0d, 0x0a});
			Port.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println(_objectName + " - Current time: " + current_time);
	}
}