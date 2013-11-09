package vadi.test.sarb.esper.data;

import java.util.HashMap;
import java.util.concurrent.Callable;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class EODMACDCalculator implements Callable<HashMap> {

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	OHLCAverage ohlc ;
	Core core;
	int slow = 30;//26;
	int fast = 14;//12;
	int cd = 5 ; //9
	public OHLCAverage getOhlc() {
		return ohlc;
	}

	public void setOhlc(OHLCAverage ohlc) {
		this.ohlc = ohlc;
		log.info("Setting ohlc in macd"+ohlc.getSymbol());
	}

	public EODMACDCalculator() {
		// TODO Auto-generated constructor stub
		//log.info("Created TA LIBARARY");
		core = new Core();
	}
	
	
	@Override
	public HashMap call() throws Exception {
		MInteger outBegIdx = new MInteger();
		     MInteger outNbElement = new MInteger();
		     RetCode retCode;
		     double []close = ohlc.getPremitive(ohlc.getcList());
		double macd[]   = new double[close.length];
    	double signal[] = new double[close.length];
    	double hist[]   = new double[close.length];
    	retCode = core.macd(0,close.length-1,close,slow,fast,cd,outBegIdx,outNbElement,macd,signal,hist);
         
		log.info("retCode="+retCode);
		log.info("outBegIdx="+outBegIdx.value);
		log.info("outNbElement="+outNbElement.value);
		//log.info("macd="+Arrays.toString(macd));
		//log.info("macd.size="+macd.length);
		//log.info("signal="+Arrays.toString(signal));
		//log.info("hist="+Arrays.toString(hist));
		HashMap map = new HashMap();
		map.put("macd",macd);
		map.put("signal",signal);
		map.put("hist",hist);
		
		return map;
	}
	
	
}
