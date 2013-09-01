package DataCollector.XML;

public class LOAD {

	private double load1;
	private double load2;
	private double load3;
	private double load4;
	private double hysterese;
	
	public synchronized double getLoad1() {
		return load1;
	}
	public synchronized void setLoad1(double load1) {
		this.load1 = load1;
	}
	public synchronized double getLoad2() {
		return load2;
	}
	public synchronized void setLoad2(double load2) {
		this.load2 = load2;
	}
	public synchronized double getLoad3() {
		return load3;
	}
	public synchronized void setLoad3(double load3) {
		this.load3 = load3;
	}
	public synchronized double getLoad4() {
		return load4;
	}
	public synchronized void setLoad4(double load4) {
		this.load4 = load4;
	}
	public synchronized double getHysterese() {
		return hysterese;
	}
	public synchronized void setHysterese(double hysterese) {
		this.hysterese = hysterese;
	}

}
