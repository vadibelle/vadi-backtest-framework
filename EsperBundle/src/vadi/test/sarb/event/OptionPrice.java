package vadi.test.sarb.event;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import vadi.test.sarb.esper.util.*;


public class OptionPrice extends Event implements Serializable,EventHandler {
    private static final long serialVersionUID = 5537285117925009769L;
    
    private Utility util = Utility.getInstance();
    /**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
   
    public String symbol,call_r_put,expiry ,stock_price, price,bid,ask,volume,strike;
    public long timestamp;

    
    
    public OptionPrice(String symbol, String callRPut, String expiry,
			String stockPrice, String price, String bid, String ask,
			String volume, String strike, long timestamp) {
		super();
		this.symbol = symbol;
		call_r_put = callRPut;
		this.expiry = expiry;
		stock_price = stockPrice;
		this.price = price;
		this.bid = bid;
		this.ask = ask;
		this.volume = volume;
		this.strike = strike;
		this.timestamp = timestamp;
	}
    
	public OptionPrice() {
        this("","","","","","","","","",System.currentTimeMillis());
       
    }
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCall_r_put() {
		return call_r_put;
	}
	public void setCall_r_put(String callRPut) {
		call_r_put = callRPut;
	}
	public String getExpiry() {
		return expiry;
	}
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	public String getStock_price() {
		return stock_price;
	}
	public void setStock_price(String stockPrice) {
		stock_price = stockPrice;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getAsk() {
		return ask;
	}
	public void setAsk(String ask) {
		this.ask = ask;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getStrike() {
		return strike;
	}
	public void setStrike(String strike) {
		this.strike = strike;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	//@Override
	/*public String toString() {
		return "OptionPrice [ask=" + ask + ", bid=" + bid + ", call_r_put="
				+ call_r_put + ", expiry=" + expiry + ", price=" + price
				+ ", stock_price=" + stock_price + ", strike=" + strike
				+ ", symbol=" + symbol + ", timestamp=" + timestamp
				+ ", volume=" + volume + "]";
	}*/


	@Override
	public void handle(ConcurrentHashMap<String,Object> state) {
		// TODO Auto-generated method stub
		util.info(this.toString());
		
	}
    	
}
