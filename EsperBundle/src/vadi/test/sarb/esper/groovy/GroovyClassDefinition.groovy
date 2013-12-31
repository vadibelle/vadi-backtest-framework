package vadi.test.sarb.esper.groovy

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.event.LastEOD
import vadi.test.sarb.event.LoadPortfolio
import vadi.test.sarb.event.StartEODQuote
import vadi.test.sarb.event.StockSignal

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import vadi.test.sarb.esper.util.*

//utility to convert long to date string
class Datetime
{
	public static String str(long str)
	{
		return new Date(str).toString();
	}
}

class GenericListener implements UpdateListener {
	
		//GenericChart c = Utility.addChart("CP");
		public void update(EventBean[] arg0, EventBean[] arg1) {
			try{
			// TODO Auto-generated method stub
				def out = new File('c:\\temp\\test.csv')
			//	println "arg0 length "+arg0.length
				for ( e in arg0 ){
				//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
				println "event "+e.getUnderlying();
				def p = e.getProperties();
				for ( i in p.values())
				{
				out.append(i);
				out.append(",");
				out.append("\n");
				}
							
				}
			}
			catch(e){
				e.printStackTrace();
			}
				
		}
	}
	
	class CpListener  implements UpdateListener {
	
		def GenericChart close= Utility.addChart("close");
		//def GenericChart cp = Utility.addChart("correl")
		
		public CpListener() {
			//chart.setOrientation("H");
			println "initialized CPlisterner";
			close.addSeries("close");
			close.addSeries("correl");
			close.addSeries('vol')
			//cp.addSeries("correl")
			//cp.addSeries("");
		
		}
		
		public void update(EventBean[] arg0, EventBean[] arg1) {
			// TODO Auto-generated method stub
			try{
			//println "Recevied ema quote"
		close.addData("close", java.lang.Double.parseDouble(arg0[0].get("close")));
			close.addData("correl", arg0[0].get("cor")*100);
			close.addData("vol", arg0[0].get("vol"));
			}
			catch(e) {
				e.printStackTrace();
		}
	 }
		
	}
	
	class TradeListener implements UpdateListener {
		
			public void update(EventBean[] arg0, EventBean[] arg1) {
				try{
				Object obj = arg0[0].getUnderlying();
				if ( obj instanceof StockSignal)
				{
					StockSignal sig = (StockSignal)obj;
					PFManager.getInstance().addLastTrade(sig.getSymbol(), sig.toString())
				}
				//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
				println "TradeReceived "+obj;
				//TradeSignal sig = arg0[0].getUnderlying();
				Utility.getInstance().dbUtil.execute("insert into signals ("+
					" signal) values ('"+obj.toString()+"')");
				//sig.enqueue();
				
				}
			catch(e){
					e.printStackTrace();
				}
					
			}
		}
	
	class StopSignal implements UpdateListener {
		
			public void update(EventBean[] arg0, EventBean[] arg1) {
				try{
				// TODO Auto-generated method stub
				//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
				println "TradeReceived "+arg0[0].getUnderlying();
				StopSignal sig = arg0[0].getUnderlying();
				Utility.getInstance().dbUtil.execute("insert into signals ("+
					" signal) values ('"+sig.toString()+"')");
				//sig.enqueue();
				}
			catch(e){
					e.printStackTrace();
				}
					
			}
		}
	
	
	class StopSystem implements UpdateListener {
			def output = "";
				public void update(EventBean[] arg0, EventBean[] arg1) {
				try{
				// TODO Auto-generated method stub
				//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
				//println "Shutting down"
				LastEOD evt = arg0[0].getUnderlying();
				def u = Utility.getInstance();
				u.removeFromSymbolList(evt.getSymbol())
				PFManager pfm = PFManager.getInstance();
				println "Last event received "+evt.getSymbol();
				//println " Details for "+evt.getSymbol()
				output +=  pfm.getDetails(evt.getSymbol())
				output += "\n"
				if( u.isSymbolListEmpty()){
					println "Shutting down"
					print output;
					output.split('\n').each { 
						println it.split(",").each {k ->
							if ( k.contains("last Trade"))
							println k
					}
						
						
					}
					System.exit(0);
				}
				else {
					PFManager.getInstance().setCash(10000)
					def s = u.getSymbolList().get(0)
					def sq = new StartEODQuote(s)
					sq.enqueue()
					
				}
					
				}
			catch(e){
					e.printStackTrace();
				}
					
			}
		}
	