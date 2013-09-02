package vadi.test.sarb.event;

public class StopLoss extends Event{
	public String symbol;
	public String signal;
	public String close;
	public long timestamp;
	
	public StopLoss(String symbol, String signal, String close, long timestamp) {
		super();
		this.symbol = symbol;
		this.signal = signal;
		this.close = close;
		this.timestamp = timestamp;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public long getTimestamp() {
		return timestamp;
	}

	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "StopLoss [symbol=" + symbol + ", signal=" + signal + ", close="
				+ close + ", timestamp=" + timestamp + "]";
	}

}
