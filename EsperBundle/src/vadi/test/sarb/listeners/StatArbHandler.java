package vadi.test.sarb.listeners;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import vadi.test.sarb.esper.GoogleDownload;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.StartEODQuote;
import vadi.test.sarb.event.StatArb;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class StatArbHandler implements UpdateListener {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");
	 CalculateRatio calc;
	// CalculateT1Ratio calc1;
	 //CalculateT2Ratio calc2;
		GenericChart chart ;
		//to save the series data for the charts
		//Each pair would have a series
		 ConcurrentHashMap map = new ConcurrentHashMap();
	//	volatile int count;
	//	 EODQuote t1,t2;
	//	Vector stockList ;
		 // TO save last quote from each symbol
		//ConcurrentHashMap<String,EODQuote> quotes ;
		Vector<String> quotes;
		
		ConcurrentHashMap<HashMap<String,String>,EODQuote> arbQuote ;
		ConcurrentHashMap<String,Double> ratioDiff;
		
	public StatArbHandler() {
			super();
			// TODO Auto-generated constructor stub
			chart = new GenericChart();
			chart.setTitle("StatArb");
	    	chart.setOrientation("V");
	  // chart.setChartType("scatter");
	  //  	count = 0;
	    	//quotes = new ConcurrentHashMap<String,EODQuote>();
	    	quotes = new Vector<String>();
	    	arbQuote = new ConcurrentHashMap<HashMap<String,String>,EODQuote>();
	    	calc = new CalculateRatio();
	    	//calc1 = new CalculateT1Ratio();
	    	//calc2 = new CalculateT2Ratio();
	    	
	    	  	//t1 = new EODQuote();
	    	//t2= new EODQuote();
	    	//stockList = new Vector();
	    	ratioDiff = new ConcurrentHashMap<String, Double>();
	    	
	    	GoogleDownload.getContainer().getContentPane().add(chart.getChart("StatArb"));
	       	//GoogleDownload.getContainer().getContentPane().setSize(300 ,200);
	    	GoogleDownload.getContainer().pack();
		}
	
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		// TODO Auto-generated method stub
		if ( newEvents == null || newEvents.length == 0)
		{
			log.info("StatArb match Found with 0 lengh");
		}
		log.info("Start Arb match found "+newEvents.toString());
		StatArb evt = (StatArb)newEvents[0].getUnderlying();
		String tick1 = evt.tick1;
		String tick2 = evt.tick2;
		String series = tick1+"vs"+tick2;
		chart.addSeries(series);
		chart.addSeries(series+":ratio diff");
		//number of items in a series
		int count =0;
		map.put(series, count);
		//chart.addSeries("volume");
		StartEODQuote t1 = new StartEODQuote();
		t1.symbol = tick1;
		StartEODQuote t2 = new StartEODQuote();
		t2.symbol = tick2;
		//t1.enqueue();
		//t2.enqueue();
		/*StringBuffer sb = new StringBuffer(" all vadi.test.event.EODQuote(symbol=\""+tick1+"\"):t1");
		sb.append(" and "+" all vadi.test.event.EODQuote(symbol=\""+tick2+"\"):t2");*/
		StringBuffer sb = new StringBuffer("select * from myfirst.win:length(10) as t1 , mysecond.win:length(10) as  t2 ");
	//	sb.append(" where t1.symbol=\'"+tick1+"\' and t2.symbol=\'"+tick2+"\'");
		sb.append(" where t1.timestamp=t2.timestamp");
		log.info("new match string "+sb.toString());
		
		Utility.getInstance().registerEventListener(sb.toString(), calc);
		/*sb = new StringBuffer(" all( vadi.test.event.EODQuote(symbol=\""+tick2+"\"):t2");
		sb.append(" -> "+"vadi.test.event.EODQuote(symbol=\""+tick1+"\"):t1)");
		log.info("new match string "+sb.toString());
		statArb.addMatchListener(calc);*/
		//statArb.addMatchListener(new CalculateRatio());
	
			HashMap<String,String> tmp1 = new HashMap<String,String>();
			tmp1.put(series,tick1);
			arbQuote.put(tmp1,new EODQuote());
			
			HashMap<String,String> tmp2 = new HashMap<String,String>();
			tmp2.put(series,tick2);
			arbQuote.put(tmp2,new EODQuote());
			
			if ( !quotes.contains(tick1)) {
			//log.info("quotes doesnt contain "+tick1);
			quotes.add(tick1);
			t1.enqueue();
		}
		if ( !quotes.contains(tick2)){
			log.info("quotes doesnt contain "+tick2);
			quotes.add(tick2);
			t2.enqueue();
		}
		
		
		/*if ( !stockList.contains(tick1)){
				stockList.add(tick1);
				t1.enqueue();
		}
		if ( !stockList.contains(tick2)){
			stockList.add(tick2);
			t2.enqueue();
		}*/
			
    }
	

    class CalculateRatio implements UpdateListener {
    	SimpleRegression reg = new SimpleRegression();
    	SummaryStatistics stats = new SummaryStatistics();

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void update(EventBean[] newEvents,EventBean[] oldEvents) {
			
			try {
				log.info("Calaculate ratio");
			if ( newEvents == null || newEvents.length == 0)
			{
				log.info("StatArb match Found with 0 lengh");
				return;
			}
			
			log.info(newEvents.toString());
			EventBean event = newEvents[0];
			EODQuote t1 = (EODQuote)event.get("t1");
			EODQuote t2 = (EODQuote)event.get("t2");
			
			log.info("Start arb :Calc");
	//		log.info("StatArb "+arg0.toString());
			//log.info("Stat Arb found "+arg0.getMatchingEvents().toString() );
		
			//Need to change handle parallel procesing of same 
			//symbol wiht different time period
			
		//	EODQuote t1 = (EODQuote)arg0.getMatchingEvent("t1");
			//EODQuote t2 = (EODQuote)arg0.getMatchingEvent("t2");
		//	log.info("STAT ARB "+t1.toString()+t2.toString());
			if (( t1.timestamp-t2.timestamp) != 0 )
				return;
			
			//log.info("STAT ARB "+t1.toString()+t2.toString());
			String series = t1.symbol+"vs"+t2.symbol;
			//log.info("pair="+t1.symbol+t2.symbol);
		//	chart.addSeries("diff1");
			//chart.addSeries("diff2");
			long diff1,diff2 = 0;
			HashMap<String,String> tmp1 = new HashMap<String,String>();
			tmp1.put(series,t1.symbol);
			diff1 = arbQuote.get(tmp1).timestamp-t1.timestamp;
			if ( arbQuote.containsKey(tmp1))
			{
				if ( arbQuote.get(tmp1).timestamp  > t1.timestamp )
				{
					//log.info("Series "+series+arbQuote.get(tmp1).toString()+t1.toString());
				//	arbQuote.put(tmp1,t1);
				//	log.info("Returning becasue t1.old > t1.new");
					return;
				}
			}
			arbQuote.put(tmp1,t1);
					
			
			HashMap<String,String> tmp2 = new HashMap<String,String>();
			tmp2.put(series,t2.symbol);
			diff2 = arbQuote.get(tmp2).timestamp-t2.timestamp;
			if ( arbQuote.containsKey(tmp2))
			{
				if ( arbQuote.get(tmp2).timestamp > t2.timestamp ){
				//	log.info("Series "+series+arbQuote.get(tmp2).toString()+t2.toString());
					//arbQuote.put(tmp2,t2);
					//log.info("Returning becasue t2.old > t2.new");
					return;
				}
			}
			arbQuote.put(tmp2,t2);
					
			/*if ( quotes.containsKey(t1.symbol)) {
				//log.info("Found "+t1.toString());
				if (quotes.get(t1.symbol).timestamp > t1.timestamp)
				{
					log.info("Found"+t1.toString()+quotes.get(t1.symbol).toString());
					//return;
				}
			}*/
			
			//quotes.put(t1.symbol, t1);
		//	arbQuote.put
			
			/*if ( quotes.containsKey(t2.symbol)) {
			//	log.info("Found "+t2.toString());
				if (quotes.get(t2.symbol).timestamp > t2.timestamp)
				{
					//log.info("Found "+t2+"return");
					log.info("Found"+t2.toString()+quotes.get(t2.symbol).toString());
					//return;
				}
			}*/
			//quotes.put(t2.symbol, t2);
			
		/*			
			if ( t1.timestamp < tt1.timestamp) {
				log.info("t1 time stamp > tt1 timestamp");
				log.info(t1.toString());
				log.info(tt1.toString());
							return;
			
			}
			t1 = tt1;
			if (t2.timestamp < tt2.timestamp )
			{
				log.info("t2 time stamp > tt2 timestamp");
				return;
			}
			t2=tt2;*/
			//String tm = "timestamp="
			//StringBuffer sb = new StringBuffer("all vadi.test.event.EODQuote(symbol=\""+t1.symbol+"\"):t1");
			
			//EventExpression st1 = new EventExpression(sb.toString());
			//st1.addMatchListener(calc1);
			//log.info(sb.toString());
			//sb = new StringBuffer("all vadi.test.event.EODQuote(symbol=\""+t2.symbol+"\"):t2");
		
			//EventExpression st2 = new EventExpression(sb.toString());
			//st2.addMatchListener(calc2);
		//	log.info(sb.toString());
			
			if (( t1.timestamp-t2.timestamp) != 0 )
			return;
		//	log.info("Stat Arb found "+arg0.getMatchingEvents().toString() );
			double ratio =  Double.valueOf(t1.getClose())/Double.valueOf(t2.getClose());
			//double ratio =  Double.valueOf(t1.getHigh())/Double.valueOf(t2.getHigh());
			//double ratio =  Double.valueOf(t1.getVolume())/Double.valueOf(t2.getVolume());
			//String series = t1.symbol+"vs"+t2.symbol;
			String mesg = "count="+map.get(series)+"Ratio="+ratio;
			//log.info(mesg);
			double rd = 0;
			chart.addSeries("correlation"+series);
			if (ratioDiff.containsKey(series))
			{	
				//rd = (ratio - ratioDiff.get(series))/ratioDiff.get(series);
				rd = (ratio - ratioDiff.get(series));
				
			}
			ratioDiff.put(series,ratio);
			reg.addData(rd,ratio);
			int count = (Integer)map.get(series);
			Double d = new Double(count);
		//	chart.addData(series, d,ratio);
			chart.addData(series,ratio);
			chart.addData(series+":ratio diff",d, rd);
			stats.addValue(rd);
			double sdRatio = rd/stats.getStandardDeviation();
			chart.addData("correlation"+series,d,reg.predict(rd) );
			chart.addData("correlation"+series,reg.predict(rd) );
			//stats.addValue(rd);
			chart.addSeries("Stddev");
			
			
			chart.addData("Stddev",d,sdRatio);
			double prevSd = 0;
			if ( ratioDiff.containsKey("prevSd"))
				prevSd = ratioDiff.get("prevSd");
			
		//	if ( (sdRatio- prevSd > 2)
			if ( sdRatio  > 2)
				new TradeSignal(t1.getSymbol(),t1.getOpen(),t1.getHigh(),t1.getLow(),t1.getClose(),"BUY","Stat",Long.toString(t1.getTimestamp())).enqueue();
			if ( rd == stats.getMean() ) {
				if ( prevSd > sdRatio )
					new TradeSignal(t1.getSymbol(),t1.getOpen(),t1.getHigh(),t1.getLow(),t1.getClose(),"SELL","Stat",Long.toString(t1.getTimestamp())).enqueue();
				else 
					new TradeSignal(t2.getSymbol(),t2.getOpen(),t2.getHigh(),t2.getLow(),t2.getClose(),"SELL","Stat",Long.toString(t2.getTimestamp())).enqueue();
			}
			
			//if ((sdRatio - prevSd) < -2)
			if (sdRatio  < -2)
			
				new TradeSignal(t2.getSymbol(),t2.getOpen(),t2.getHigh(),t2.getLow(),t2.getClose(),"BUY","Stat",Long.toString(t2.getTimestamp())).enqueue();
			
			ratioDiff.put("prevSd",sdRatio);
			long t = 1000*60*60*24;
			diff1 = diff1/t;
			diff2 = diff2/t;
			//chart.addData("diff1", d,(double)diff1); 
			//chart.addData("diff2", d,(double)diff2); 
			//chart.addData("volume", (double)count,ratio2);
			//int count = (Integer) map.get(series);
			count++;
			map.put(series,count);
			 mesg = "Diff "+diff1+" "+diff2;
			// log.info(mesg);
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		}
	}


	  
   /* class CalculateT1Ratio implements MatchListener {

		@Override
		public void match(MatchEvent arg0) {
			// TODO Auto-generated method stub
			t1 = (EODQuote)arg0.getMatchingEvent("t1");
			if (( t1.timestamp-t2.timestamp) != 0 )
				return;
			double ratio =  Double.valueOf(t1.getHigh())/Double.valueOf(t2.getHigh());
		//	String mesg = "count="+count+"Ratio="+ratio+" "+(t1.timestamp-t2.timestamp);
			String series = t1.symbol+"vs"+t2.symbol;
		//	log.info(mesg);
		//	chart.addData(series, (double)count,ratio);
			//chart.addData("timediff", (double)count,(double)(t1.timestamp-t2.timestamp));
			//count++;
			
		}

    }
    class CalculateT2Ratio implements MatchListener {

		@Override
		public void match(MatchEvent arg0) {
			// TODO Auto-generated method stub
			t2 = (EODQuote)arg0.getMatchingEvent("t2");
			if ( (t1.timestamp-t2.timestamp) != 0 )
				return;
			double ratio =  Double.valueOf(t1.getHigh())/Double.valueOf(t2.getHigh());
			String mesg = "count="+count+"Ratio="+ratio+" "+(t1.timestamp-t2.timestamp);
			String series = t1.symbol+"vs"+t2.symbol;
			log.info(mesg);
			chart.addData(series, (double)count,ratio);
			//chart.addData("timediff", (double)count,(double)(t1.timestamp-t2.timestamp));
			count++;
			
		}
    }*/
}
