package DCC;

public class Pin {
	
	private int state = -1;
	
	enum PinState{
		HIGH, LOW
	}
		
	public void low() {
		// TODO Auto-generated method stub
		state = 1;
	}
	
	public void high() {
		// TODO Auto-generated method stub
		state = 0;
	}

	public PinState getState() {
		// TODO Auto-generated method stub
		
		if(state == 0)
			return PinState.HIGH;
		else
			return PinState.LOW;
	}
}
