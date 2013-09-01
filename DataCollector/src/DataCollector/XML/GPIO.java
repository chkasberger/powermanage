package DataCollector.XML;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class GPIO {

	private Pin gpioPin = RaspiPin.GPIO_00;
	private GpioPinDigitalOutput pin;
	private GpioController gpio;
	private boolean pinstate;

	public boolean getPinstate() {
		return pinstate;
	}

	public void setPinstate(boolean pinstate) {
		this.pinstate = pinstate;
	}

	public GPIO(String gpioPin) {
		gpio = GpioFactory.getInstance();
		setGpioPin(gpioPin);
	    pin  = gpio.provisionDigitalOutputPin(this.gpioPin, gpioPin, PinState.LOW);	    
	}

	public synchronized Pin getGpioPin() {
		return gpioPin;
	}

	public synchronized void setGpioPin(String gpioPin) {
		switch (gpioPin) {
		case "GPIO_00":
			this.gpioPin = RaspiPin.GPIO_00;
			break;

		default:
			this.gpioPin = RaspiPin.GPIO_00;
			break;
		}
	}
	
	public void Low(){
		pin.low();
		this.pinstate = false;
	}
	
	public void High(){
		pin.high();
		this.pinstate = true;
	}

	public void Toggle(){
		if(pinstate)
			pinstate = false;
		else
			pinstate = true;

		pin.toggle();
		
	}
}