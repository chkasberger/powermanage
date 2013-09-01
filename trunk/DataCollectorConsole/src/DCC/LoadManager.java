package DCC;

//import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.Timer;

import org.apache.log4j.Logger;

//import DCC.Pin.PinState;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LoadManager {
    static Logger logger = Logger.getRootLogger();

    static Lock lock = new ReentrantLock(true); 

    static GpioPinDigitalOutput load1 = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_03, "load1", PinState.HIGH);
	static GpioPinDigitalOutput load2 = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_04, "load2", PinState.HIGH);
	static GpioPinDigitalOutput load3 = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_05, "load3", PinState.HIGH);
	static GpioPinDigitalOutput load4 = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_06, "load4", PinState.HIGH);
	
	
	static GpioPinDigitalOutput[] loadArray = new GpioPinDigitalOutput[]{load1, load2, load3, load4};
//	static Pin[] loadArray = new Pin[]{new Pin(), new Pin(), new Pin(), new Pin()};
	
	
	private static double meanPower = -1;			

	private static double hysterese = 0.1;
	private static double load1_power = 0.35;
	private static double load2_power = 0.55;
	private static double load3_power = 0.75;
	private static double load4_power = 0.95;
	
	public double getHysterese() {
		return hysterese;
	}
	public static void setHysterese(double hysterese) {
		LoadManager.hysterese = hysterese;
	}
	public double getLoad1_power() {
		return load1_power;
	}
	public static void setLoad1_power(double load1_power) {
		LoadManager.load1_power = load1_power;
	}
	public double getLoad2_power() {
		return load2_power;
	}
	public static void setLoad2_power(double load2_power) {
		LoadManager.load2_power = load2_power;
	}
	public double getLoad3_power() {
		return load3_power;
	}
	public static void setLoad3_power(double load3_power) {
		LoadManager.load3_power = load3_power;
	}
	public double getLoad4_power() {
		return load4_power;
	}
	public static void setLoad4_power(double load4_power) {
		LoadManager.load4_power = load4_power;
	}
	
	static Timer resetTimer = new Timer(60*1000, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			lock.lock();
			{
				logger.debug("Timer resets all Pins!");
				
				for(int i=0; i<loadArray.length; i++){
					loadArray[i].high();
				}
			}
			lock.unlock();
		}
	});
	
	public LoadManager(){
		enable();
	}
	
	public static void enable(){
		resetTimer.setRepeats(false);
		resetTimer.start();
	}
	
	protected static void setLoad(double meanPowerTmp) {
		meanPower = -meanPowerTmp;
		lock.lock();
		{
			resetTimer.restart();
			
			logger.debug("meanPower for load management is: " + meanPower + " kW");
			
			PinState loadState1 = loadArray[0].getState();
			PinState loadState2 = loadArray[1].getState();
			PinState loadState3 = loadArray[2].getState();
			PinState loadState4 = loadArray[3].getState();
			
			logger.debug("GPIO states before routine\r\n\t\t" +
					"GPIO_01: " + loadState1 + " threshold " + load1_power + "\r\n\t\t" +
					"GPIO_02: " + loadState2 + " threshold " + load2_power + "\r\n\t\t" +
					"GPIO_03: " + loadState3 + " threshold " + load3_power + "\r\n\t\t" +
					"GPIO_04: " + loadState4 + " threshold " + load4_power);

			/*
			 * enable relays
			 */
			if(meanPower >= load1_power && 
					loadState1 == PinState.HIGH && 
					loadState2 == PinState.HIGH && 
					loadState3 == PinState.HIGH && 
					loadState4 == PinState.HIGH){
				loadArray[0].low();
				logger.debug("turned on load1");
			}
			else if(meanPower >= load2_power && 
					loadState1 == PinState.LOW && 
					loadState2 == PinState.HIGH && 
					loadState3 == PinState.HIGH && 
					loadState4 == PinState.HIGH){
				loadArray[1].low();
				logger.debug("turned on load2");
			}
			else if(meanPower >= load3_power && 
					loadState1 == PinState.LOW && 
					loadState2 == PinState.LOW && 
					loadState3 == PinState.HIGH && 
					loadState4 == PinState.HIGH){
				loadArray[2].low();
				logger.debug("turned on load3");
			}
			else if(meanPower >= load4_power && 
					loadState1 == PinState.LOW && 
					loadState2 == PinState.LOW && 
					loadState3 == PinState.LOW && 
					loadState4 == PinState.HIGH){
				loadArray[3].low();
				logger.debug("turned on load4");
			}
			
			/*
			 * disable relays
			 */
			else if(meanPower < load4_power-hysterese &&  
					loadState1 == PinState.LOW && 
					loadState2 == PinState.LOW && 
					loadState3 == PinState.LOW && 
					loadState4 == PinState.LOW){
				loadArray[3].high();
				logger.debug("turned off load4");
			}
			else if(meanPower < load3_power-hysterese &&  
					loadState1 == PinState.LOW && 
					loadState2 == PinState.LOW && 
					loadState3 == PinState.LOW && 
					loadState4 == PinState.HIGH){
				loadArray[2].high();
				logger.debug("turned off load3");
			}
			else if(meanPower < load2_power-hysterese && 
					loadState1 == PinState.LOW && 
					loadState2 == PinState.LOW && 
					loadState3 == PinState.HIGH && 
					loadState4 == PinState.HIGH){
				loadArray[1].high();
				logger.debug("turned off load2");
			}
			else if(meanPower < load1_power-hysterese && 
					loadState1 == PinState.LOW && 
					loadState2 == PinState.HIGH && 
					loadState3 == PinState.HIGH && 
					loadState4 == PinState.HIGH){
				loadArray[0].high();
				logger.debug("turned off load1");
			}					
			
			if(
				loadState1 != loadArray[0].getState() ||
				loadState2 != loadArray[1].getState() ||
				loadState3 != loadArray[2].getState() ||
				loadState4 != loadArray[3].getState()){

				logger.debug("GPIO state after routine\r\n\t\t" +
						"GPIO_01: " + loadArray[0].getState() + "\r\n\t\t" +
						"GPIO_02: " + loadArray[1].getState() + "\r\n\t\t" +
						"GPIO_03: " + loadArray[2].getState() + "\r\n\t\t" +
						"GPIO_04: " + loadArray[3].getState());			
			}
		}
		lock.unlock();
		
		meanPower = -1;
		logger.debug("reset meanpower for observation!");
	}


	public double getMeanPower() {
		return meanPower;
	}
	
	public static void testLoad(String substring) {
		setLoad(Double.valueOf(substring));
	}

	public static void testPin(String substring) {
		loadArray[Integer.valueOf(substring)].toggle();
	}
}