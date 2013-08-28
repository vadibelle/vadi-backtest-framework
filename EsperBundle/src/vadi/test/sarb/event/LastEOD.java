package vadi.test.sarb.event;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import vadi.test.sarb.esper.data.OHLCAverage;
import vadi.test.sarb.esper.util.LineChart;
import vadi.test.sarb.esper.util.Utility;

//@EventType(description = "", name = "vadi.test.event.LastEOD")
public class LastEOD extends Event implements EventHandler {
    private static final long serialVersionUID = 25321170427911533L;
    LineChart chart ;
    public String symbol;
    
    public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public LastEOD(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	
	/**
     * No argument constructor used by the Apama Java framework on
     * application loading
     */
    public LastEOD() {
        this("");
    }
	
    
    public void handle(ConcurrentHashMap<String,Object> state) {
		// TODO Auto-generated method stubt
    	Utility log = Utility.getInstance();
    	log.info("####LastEOD received ###"+this.getSymbol());
    	log.info("###"+state.toString());
		//log.info( ((LastEOD) evt).getSymbol());
    	chart = (LineChart) state.get("macdChart");
		OHLCAverage avg;
		ConcurrentHashMap<String, OHLCAverage> maArr = (ConcurrentHashMap<String, OHLCAverage>) state.get("maArr");
		if (maArr.containsKey(this.getSymbol())){
			log.info("maArr conatins "+this.getSymbol());
			avg = maArr.get(this.getSymbol());
		
	//	EODMACDCalculator macd = new EODMACDCalculator();
	//	macd.setOhlc(avg);
		//Future<HashMap>ft = log.getExecutor().submit(macd);
		ProcessMACD pmacd = new ProcessMACD();
		/*try {
			pmacd.setHashMap(ft.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		Utility.getInstance().getExecutor().execute(pmacd);
					  
	}
		
	}
    
    class ProcessMACD implements Runnable{

		HashMap result;
		void setHashMap(HashMap hm){
			result = hm;
		}
		
		public void run() {
			// TODO Auto-generated method stub
			//LineChart chart = new  LineChart();
			Utility log = Utility.getInstance();
			log.info("plotting "+symbol+"macd");
			double []macd = (double [])result.get("macd");
			double []hist = (double [])result.get("hist");
			double []signal = (double [])result.get("signal");
			
			chart.addSeries("macd:"+symbol);
			/*chart.addSeries("hist:"+symbol);
			chart.addSeries("signal:"+symbol);
			for(int i=0; i<macd.length;i++)
				chart.addData("macd:"+symbol,(double)i, macd[i]);
			for(int i=0; i<hist.length;i++)
				chart.addData("hist:"+symbol,(double)i, hist[i]);
			for(int i=0; i<signal.length;i++)
				chart.addData("signal:"+symbol,(double)i, signal[i]);*/
			
			for(int i=0; i<macd.length;i++)
			{
				chart.addData("macd:"+symbol,(double)i, (macd[i]-signal[i]));
				
			}
			
			//chart.addSeries("history");
			//for(int i=0; i<hist.length;i++)
				//chart.addData("history",(double)i, hist[i]);
			//chart.addSeries("signal");
			//for(int i=0; i<signal.length;i++)
				//chart.addData("signal",(double)i, signal[i]);
						
		}
		
	}
}
