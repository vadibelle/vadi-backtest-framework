package vadi.test.sarb.listeners;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import vadi.test.sarb.esper.GoogleDownload;
import vadi.test.sarb.esper.data.MACDGenerator;
import vadi.test.sarb.esper.data.OHLCAverage;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.TradeSignal;

@Deprecated
public class MacdListerner implements UpdateListener {

//	LineChart chart ;//= new LineChart();
	GenericChart chart ;
	ConcurrentHashMap<String, Object> state;
	//ConcurrentHashMap<String,HashMap> map;
	//ConcurrentHashMap<String, OHLCAverage> quoteArr;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.listeners");
	//HashMap<String,Integer> macdCount ;
	int n;
	//HashMap<String,Boolean> macdFound;
	boolean found ;
	double cdiff;
	double ldiff;
	
	public MacdListerner() {
		super();
		// TODO Auto-generated constructor stub
		state = new ConcurrentHashMap<String, Object>();
		chart = new GenericChart();
		chart.setTitle("RT MACD");
		//map = new ConcurrentHashMap<String, HashMap>();
	//	quoteArr = new ConcurrentHashMap<String, OHLCAverage>();
		//chart.setChartType("scatter");
		chart.setOrientation("V");
		//macdCount = new HashMap<String, Integer>();
		//n=0;
		//found = false;
		//macdFound = new HashMap<String, Boolean>();
		 cdiff =0;
		 ldiff=0;
		 GoogleDownload.getContainer().getContentPane().add(chart.getChart("MACD"));
		 //GoogleDownload.getContainer().getContentPane().setSize(300, 200);
			GoogleDownload.getContainer().pack();

		//	GoogleDownload.getContainer().setVisible(true);*/
		
	}

	
	@Override
	public void update(EventBean[] arg0,EventBean[] arg1) {
		// TODO Auto-generated method stub
		//log.info("Macd "+arg0.length);
		//log.info(arg0.length);
		EODQuote eq = (EODQuote)arg0[0].getUnderlying();
		HashMap<String,Object> symState;
		int n;
		boolean found;
		 boolean buy=false;
		 boolean sell=false;
		HashMap macdMap = null;
		OHLCAverage avg = null;
		boolean crossover = false;
		//ConcurrentHashMap<String, OHLCAverage> quoteArr;
		//log.info("state "+state.toString());
		if (!state.containsKey(eq.getSymbol())){
			symState = new HashMap<String,Object>();
			macdMap = new HashMap();
			n=0;
			found=false;
		//	avg = new OHLCAverage(0.0, 0.0, 0.0, 0.0, 0.0,eq.getSymbol());
			symState.put("macdFound", found);
			symState.put("macdCount", n);
			symState.put("macdMap",macdMap);
			symState.put("quoteArr",avg);
			state.put(eq.getSymbol(),symState);
			}
		else{
			symState = (HashMap<String, Object>) state.get(eq.getSymbol());
			found = (Boolean) symState.get("macdFound");
			n =  (Integer) symState.get("macdCount");
			macdMap = (HashMap) symState.get("macdMap");
			avg = (OHLCAverage) symState.get("quoteArr");
					
		}
	
		chart.addSeries("macd:"+eq.symbol);
		MACDGenerator macd = new MACDGenerator();
		
		
		/*if ( !macdFound.containsKey(eq.getSymbol()))
				macdFound.put(eq.getSymbol(), false);
		if ( !macdCount.containsKey(eq.getSymbol()))
			macdCount.put(eq.getSymbol(), 0);
		*/
			//if (map.containsKey(eq.getSymbol())){
				//macdMap = map.get(eq.getSymbol());
				if ( avg != null ) {
				//avg = quoteArr.get(eq.getSymbol());
				avg.update(Double.valueOf(eq.open),
						 Double.valueOf(eq.high),
						 Double.valueOf(eq.close),
						 Double.valueOf(eq.close),
						Double.valueOf(eq.volume));
				
			}
			else{
				//macdMap = new HashMap();
				avg  = new OHLCAverage(Double.valueOf(eq.open),
						 Double.valueOf(eq.high),
						 Double.valueOf(eq.close),
						 Double.valueOf(eq.close),
						Double.valueOf(eq.volume), eq.symbol);
							
			}
				symState.put("quoteArr", avg);
			//quoteArr.put(eq.symbol, avg);
			//map.put(eq.symbol,macdMap);
			macd.setTsdata(avg.getPremitive(avg.getcList()));
			int ret = macd.calculate(macdMap);
			if (ret == 0 )
			{
			//	map.put(eq.symbol, macdMap);
				double[] m = (double[]) macdMap.get("macd");
				//macdCount.put(eq.getSymbol(), c.length);
			//	log.info("MACD length"+m.length);
				//log.info(Arrays.toString(m));
				double[]h = (double[]) macdMap.get("histogram");
				//log.info(Arrays.toString(h));
			//	chart.addSeries("hist:"+eq.symbol);			
				double[]s = (double[]) macdMap.get("signal");
			//	chart.addSeries("signal:"+eq.symbol);	
				//log.info(Arrays.toString(s));
			//	int n = macdCount.get(eq.getSymbol());dfaswd
			//	double cdiff =0;
			//	double ldiff=0;
				//boolean found = macdFound.get(eq.getSymbol()); 
				if (!found && m[n] == 0) {
				
					//log.info(Arrays.toString(m));
					//log.info(Arrays.toString(h));
					//log.info(Arrays.toString(s));
					return;
				
				}
					else{
						if (m[n] == 0 )
						//	log.info("MACD events "+m[n]);
						if (m[n] -s[n] == 0 )
							//log.info("MACD -signal="+m[n]);
							
				//	boolean crossover = false;
					//log.info("price="+eq.getClose());
					cdiff = m[n]-s[n];
					if ( n > 0)
						ldiff = m[n-1] - s[n-1];
					if (Math.signum(cdiff) != Math.signum(ldiff)){
						//log.info("Signal CROSSOVER "+cdiff+" "+ldiff+" "+h[n]);
						log.info("Price during crossover "+eq.getClose());
						crossover=true;
						if (Math.signum(cdiff) > Math.signum(ldiff))
							buy=true;
						else
							sell=true;
						
						}
					if (n > 0 && (Math.signum(m[n-1]) != Math.signum(m[n]))){
					//	log.info("MACD CROSSOVER "+m[n-1]+" "+m[n]+" "+h[n]+" "+h[n-1]);
						log.info("Price during crossover "+eq.getClose());
						if ( Math.signum(m[n-1]) > Math.signum(m[n]))
							buy=true;
						else 
							sell = true;
						if (crossover)
							{
							log.info("BOTH CROSSOVERS") ;
							log.info("Price during crossover "+eq.getClose());
							crossover=false;
							}
					}
					chart.addData("macd:"+eq.symbol,(double)n,m[n]);
					chart.addData("macd:"+eq.symbol,m[n]);
				//	chart.addData("hist:"+eq.symbol,(double)n,h[n]);
					//chart.addData("signal:"+eq.symbol,(double)n,s[n]);
					n++;
					found=true;
					symState.put("macdFound", found);
					symState.put("macdCount", n);
					ldiff=cdiff;
				//	macdFound.put(eq.getSymbol(), found);
					//macdCount.put(eq.getSymbol(), n);
					}
						
				/*for(int i=0;i< c.length;i++)
				{
					log.info("macd="+i+"="+c[i]);
					if ( c[i] == 0 && i != 0)
					{
						log.info("plotting macd="+n+"="+c[i-1]);
						//chart.addData("macd:"+eq.symbol,(double)i,c[i-1]);
						chart.addData("macd:"+eq.symbol,(double)n,c[i-1]);
						n++;
					}
				}*/
				if ( buy && crossover ) {
					new TradeSignal(eq.getSymbol(),eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),"Buy","MACD",Long.toString(eq.getTimestamp())).enqueue();
					log.info("###Buy "+eq.toString());
					buy=false;
					crossover=false;
				}
				if (sell && crossover) {
					new TradeSignal(eq.getSymbol(),eq.getOpen(),eq.getHigh(),eq.getLow(),eq.getClose(),"Sell","MACD",Long.toString(eq.getTimestamp())).enqueue();
					log.info("###Sell "+eq.toString());
					sell=false;
					crossover=false;
				}
					
			}
				
	}

}
