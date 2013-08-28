package vadi.test.sarb.event;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;

import vadi.test.sarb.esper.data.OHLCAverage;
import vadi.test.sarb.esper.util.*;

public class EODQuote extends Event implements Serializable {
	
   
     Utility log = Utility.getInstance();

    /**
	 * 
	 */
	private static final long serialVersionUID = 8313772609388595902L;

	/**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    
   public String symbol;
   
 //  @Wildcard
   public String open, high,low,close;
   //@Wildcard
   public long volume,timestamp;
   public EODQuote()
   {
	   this("","","","","",0,0);
   }
public EODQuote(String symbol, String open, String high, String low,
		String close,long volume,long time ) {
	super();
	this.symbol = symbol;
	this.open = open;
	this.high = high;
	this.low = low;
	this.close = close;
	this.volume = volume;
	this.timestamp = time;
	}
public String getSymbol() {
	return symbol;
}
public void setSymbol(String symbol) {
	this.symbol = symbol;
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
public long getTimestamp() {
	return timestamp;
}
public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
}
public long getVolume() {
	return volume;
}
public void setVolume(long volume) {
	this.volume = volume;
}

public void handle(ConcurrentHashMap<String,Object> state) {
	// TODO Auto-generated method stub
	log.info(this.toString());
	log.info("##"+state.toString());
	OHLCAverage avg;
	ConcurrentHashMap<String, OHLCAverage> maArr = (ConcurrentHashMap<String, OHLCAverage>) state.get("maArr");
	if (maArr.containsKey(this.getSymbol())){
	log.info("EODQuote handle "+this.getSymbol());
		avg = maArr.get(this.getSymbol());
		if (Double.valueOf(this.getLow()) > Double.parseDouble(this.getHigh()))
		{
			log.info("low is greater than high, rejecting");
			return;
		}
		avg.update(Double.valueOf(this.getOpen()),
				Double.valueOf(this.getHigh()),
				Double.valueOf(this.getLow()),
				Double.valueOf(this.getClose()),
				(double)this.getVolume());
		//maArr.put(this.getSymbol(), avg);
		//log.info("Averages "+avg); //$NON-NLS-1$
					
	}
	else {
		log.info("Found the Record");
		if (Double.valueOf(this.getLow()) > Double.parseDouble(this.getHigh()))
		{
			log.info("low is greater than high, rejecting");
			return;
		}
		 avg = new OHLCAverage(Double.valueOf(this.getOpen()),
				Double.valueOf(this.getHigh()),
				Double.valueOf(this.getLow()),
				Double.valueOf(this.getClose()),
				(double)this.getVolume(),this.getSymbol());
		//maArr.put(this.getSymbol(), avg);
		//log.info("Averages "+avg);

	}
	maArr.put(this.getSymbol(), avg);
	state.put("maArr", maArr);
	log.info("Averages "+avg); //$NON-NLS-1$
	log.info("Before the chart");
	LineChart chart = (LineChart) state.get("chart");
	chart.addSeries(this.getSymbol());
	chart.addData(this.symbol,Double.valueOf(getClose()),avg.getN());
	String mesg = "n="+avg.getN()+" vol="+getClose();
	
	
	log.info(mesg);
	/*chart.addSeries("sin");
	//double theta=0.0;
	chart.addData("sin",theta,Math.sin(theta));
	String mesg = "theta"+theta+Math.sin(theta);
	log.info(mesg);
	theta ++;*/
	
			
	//chart.addData(avg.h, avg.l);
	
}
@Override
public String toString() {
	return "EODQuote [symbol=" + symbol + ", open=" + open + ", high=" + high
			+ ", low=" + low + ", close=" + close + ", volume=" + volume
			+ ", timestamp=" + new Timestamp(timestamp).toString() + "]";
	//+ ", timestamp=" + timestamp + "]";
}



}
