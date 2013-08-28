package vadi.test.sarb.event;

import java.io.Serializable;

public class LoadPortfolio extends Event {
	public double cash = 0;

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}
	
}
