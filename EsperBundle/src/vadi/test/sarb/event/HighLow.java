package vadi.test.sarb.event;

public class HighLow extends Event {
	private static final long serialVersionUID = 25381170427911533L;
	public String indicator;
	public String symbol;

	public HighLow() {
		super();
		indicator="";
		symbol="";
	}

	public HighLow(String indicator,String symbol) {
		super();
		this.indicator = indicator;
		this.symbol = symbol;
	}

	public String getIndicator() {
		return indicator;
	}

	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	

}
