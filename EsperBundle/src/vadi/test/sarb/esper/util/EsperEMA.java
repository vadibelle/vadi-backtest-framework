package vadi.test.sarb.esper.util;

import java.util.logging.Level;

import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
public class EsperEMA implements AggregationMethod {
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.esper.util");
	
	volatile private int counter = 0;
	private double ema = 0;
	private int max = 0;
	private boolean start = false;
	private  Level level = Level.OFF;
	@Override
	public void clear() {
		log.log(level,"Clear called");
		counter=0;
		ema=0;
		max =0;
	}

	public EsperEMA()
	{
		counter=0;
		ema=0;
		max=0;
		log.setLevel(level);
	}
	@Override
	public  void enter(Object arg0) {
		// TODO Auto-generated method stub
		//log.log(level,"entering "+arg0.toString());
		
		counter ++;
		if ( max < counter )
			max = counter;
		
		log.log(level,"arg0 "+arg0.toString());
		double d = Double.parseDouble(arg0.toString());
		if (Double.isNaN(d))
			return;
		if ( !start ){
			ema = ema*(counter-1);
			ema = ema+d;
			ema = (float)ema/counter;
		
		}
		log.log(level,"entering "+ d +" COUNTER "+counter);
		int sz = max;
		sz++;
		log.log(level,"size is "+sz);
		float ef = (float)2/sz;
		float f = 1 - ef;
		log.log(level,"ema before "+ema+" "+ef*d);
		log.log(level,"ema factor "+f*ema);
		ema = ef*d+f*ema;
		
		log.log(level,"ema after "+ema);
		
	}

	@Override
	public  Object getValue() {
		// TODO Auto-generated method stub
		if ( !start )
			return 0;
		
		log.log(level,"EMA="+ema);
		return ema;
		
	}

	@Override
	public Class getValueType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	@Override
	public synchronized void leave(Object arg0) {
		// TODO Auto-generated method stub
		counter--;
		start = true;
		log.log(level,"leaving "+arg0.toString()+" size "+counter);
		//System.out.println("EMA length "+counter);
		
	}

}
