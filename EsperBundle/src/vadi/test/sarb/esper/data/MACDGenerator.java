package vadi.test.sarb.esper.data;

import java.util.HashMap;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

public class MACDGenerator  {

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.data");
	int slow = 26;//26;
	int fast = 12;//12;
	int crossover = 9 ; //9

	Core core;
	double []tsdata;
	
	public MACDGenerator() {
		super();
		// TODO Auto-generated constructor stub
		core = new Core();
		
	}

	public int getSlow() {
		return slow;
	}

	public void setSlow(int slow) {
		this.slow = slow;
	}

	public int getFast() {
		return fast;
	}

	public void setFast(int fast) {
		this.fast = fast;
	}

	public int getCrossover() {
		return crossover;
	}

	public void setCrossover(int crossover) {
		this.crossover = crossover;
	}

	public double[] getTsdata() {
		return tsdata;
	}

	public void setTsdata(double[] tsdata) {
		this.tsdata = tsdata;
	}

	
	public int  calculate(HashMap map)  {
		// TODO Auto-generated method stub
		//HashMap map = new HashMap();
		 MInteger outBegIdx = new MInteger();
	     MInteger outNbElement = new MInteger();
	     RetCode retCode;
	     int lookback;

	     if (tsdata.length < slow)
	     {
	    	 map.put("macd",new double[0]);
	    	 map.put("signal",new double[0]);
	    	 map.put("histogram",new double[0]);
	    	 return 1;
	     }
		lookback =	core.macdLookback(slow,fast,crossover);
		double macd[]   = new double[tsdata.length];
    	double signal[] = new double[tsdata.length];
    	double hist[]   = new double[tsdata.length];
		
    	retCode = core.macd(0,tsdata.length-1,tsdata,slow,fast,crossover,outBegIdx,outNbElement,macd,signal,hist);  	
    	if (retCode == RetCode.Success )
    	{
    		map.put("macd", macd);
    		map.put("signal", signal);
    		map.put("histogram",hist);
    		return 0;
    	}
    	else
    		return 1;
	}
	
	
	}
	


