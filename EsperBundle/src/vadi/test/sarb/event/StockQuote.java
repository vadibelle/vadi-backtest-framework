package vadi.test.sarb.event;

public class StockQuote extends Event {
	
	private String smbl;
	private double price;
	public String getSmbl() {
		return smbl;
	}
	public void setSmbl(String smbl) {
		this.smbl = smbl;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public StockQuote(String smbl, double price) {
		super();
		this.smbl = smbl;
		this.price = price;
	}
	public StockQuote()
	{
		this("",0);
	}
	

}
