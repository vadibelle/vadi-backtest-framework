package vadi.test.sarb.event;

import java.sql.Timestamp;



//@EventType(description = "TradeSignal", name = "vadi.test.event.TradeSignal")
public class TradeSignal extends Event {
 //   private static final long serialVersionUID = -6940265589990886917L;

    /**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    public TradeSignal() {
        this("","0.0","0.0","0.0","0.0","NA","NA","");
    }
    public TradeSignal(String symbol, String open, String high, String low,
			String close, String type, String indicator, String price_timestamp) {
		super();
		this.symbol = symbol;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.type = type;
		this.indicator = indicator;
		this.price_timestamp = price_timestamp;
	}
	public String symbol;
    public String open;
    public String high;
    public String low;
    public String close;
    public String type;
    public String indicator;
    public String price_timestamp;

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

	

	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice_timestamp() {
		return price_timestamp;
	}

	public void setPrice_timestamp(String priceTimestamp) {
		price_timestamp = priceTimestamp;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
	@Override
	public String toString() {
		return "TradeSignal [symbol=" + symbol + ", open=" + open + ", high="
				+ high + ", low=" + low + ", close=" + close + ", type=" + type
				+ ", indicator=" + indicator + ", price_timestamp="
				+ new Timestamp(Long.parseLong(price_timestamp)).toString()+ "]";
	}

	
}
