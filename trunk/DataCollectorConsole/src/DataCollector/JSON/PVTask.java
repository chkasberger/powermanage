package DataCollector.JSON;

import java.util.TimerTask;

public class PVTask extends TimerTask {

	//private final ComPort Port;
	PV _pv;
	/**
	 * Constructs the object, sets the string to be output in function run()
	 * @param str
	 */
	public PVTask(PV pv) {
		_pv = pv;
	}

	/**
	 * When the timer executes, this code is run.
	 */
	@Override
	public void run() {
		_pv.readValues();
	}
}