package DataCollector.XML;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class S0 {

	private double ticksPerKwh = 800;
	private double offset = 0;
	private Pin gpioPin = RaspiPin.GPIO_00;
	private PinPullResistance pullResistance = PinPullResistance.PULL_DOWN;
	private int interval = 60;

	public S0(){

	}

	public synchronized double getTicksPerKwh() {
		return ticksPerKwh;
	}

	public synchronized void setTicksPerKwh(double d) {
		ticksPerKwh = d;
	}

	public synchronized double getOffset() {
		return offset;
	}

	public synchronized void setOffset(double d) {
		offset = d;
	}

	public synchronized Pin getGpioPin() {
		return gpioPin;
	}

	public synchronized void setGpioPin(String gpioPin) {
		switch (gpioPin) {
		case "GPIO_00":
			this.gpioPin = RaspiPin.GPIO_00;
			break;
		case "GPIO_01":
			this.gpioPin = RaspiPin.GPIO_01;
			break;
		case "GPIO_02":
			this.gpioPin = RaspiPin.GPIO_02;
			break;
		case "GPIO_03":
			this.gpioPin = RaspiPin.GPIO_03;
			break;
		case "GPIO_04":
			this.gpioPin = RaspiPin.GPIO_04;
			break;

		default:
			this.gpioPin = RaspiPin.GPIO_00;
			break;
		}
	}

	public synchronized PinPullResistance getPullResistance() {
		return pullResistance;
	}

	public synchronized void setPullResistance(String pullResistance) {
		switch (pullResistance) {
		case "PULL_DOWN":
			this.pullResistance = PinPullResistance.PULL_DOWN;
			break;
		case "PULL_UP":
			this.pullResistance = PinPullResistance.PULL_UP;
			break;
		default:
			this.pullResistance = PinPullResistance.PULL_DOWN;
			break;
		}
	}

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}
}
