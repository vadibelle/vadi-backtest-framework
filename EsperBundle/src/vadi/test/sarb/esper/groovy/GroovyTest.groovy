package vadi.test.sarb.esper.groovy;
import java.awt.geom.Arc2D.Double;
import java.util.concurrent.ConcurrentSkipListMap.Iter;

import groovy.transform.EqualsAndHashCode;

import com.espertech.esper.client.EPServiceProvider
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener
import com.espertech.esper.client.deploy.EPDeploymentAdmin
import com.espertech.esper.client.deploy.Module
import com.espertech.esper.client.hook.AggregationFunctionFactory
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationSupport
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import vadi.test.sarb.esper.util.GenericChart
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote
import vadi.test.sarb.event.LoadPortfolio
import vadi.test.sarb.event.StartEODQuote
import vadi.test.sarb.event.StatArb;
import vadi.test.sarb.event.TradeSignal
import vadi.test.sarb.listeners.DummyListener
import vadi.test.sarb.listeners.EODHandler
import vadi.test.sarb.listeners.ExitGenerator
import vadi.test.sarb.listeners.LongPosition
import vadi.test.sarb.listeners.MAIndicator
import vadi.test.sarb.listeners.MacdListerner
import vadi.test.sarb.listeners.MyListener
import vadi.test.sarb.listeners.PositionLoader
import vadi.test.sarb.listeners.ShortPosition
import vadi.test.sarb.listeners.StartEOD
import vadi.test.sarb.listeners.StatArbHandler
import vadi.test.sarb.listeners.OldSimulator;

import vadi.test.sarb.event.HighLow;

//evaluate(new File((vadi.test.sarb.esper.Messages.getString("StatnUtil")))

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

	def GenericChart close= Utility.addChart("slope");
	def GenericChart cp = Utility.addChart("ema")
	
	public CpListener() {
		//chart.setOrientation("H");
		println "initialized CPlisterner";
		close.addSeries("close");
		cp.addSeries("slope")
		//cp.addSeries("");
	
	}
	
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try{
		//println "Recevied ema quote"
	close.addData("close", java.lang.Double.parseDouble(arg0[0].get("cl")));
		cp.addData("slope", arg0[0].get("slope"));
		}
		catch(e) {
			e.printStackTrace();
	}
 }
	
}

class TradeListener implements UpdateListener {
	
		public void update(EventBean[] arg0, EventBean[] arg1) {
			try{
			// TODO Auto-generated method stub
			//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
			println "TradeReceived "+arg0[0].getUnderlying();
			//TradeSignal sig = arg0[0].getUnderlying();
			Utility.getInstance().dbUtil.execute("insert into signals ("+
				" signal) values ('"+arg0[0].getUnderlying().toString()+"')");
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
	
		public void update(EventBean[] arg0, EventBean[] arg1) {
			try{
			// TODO Auto-generated method stub
			//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
			println "Signal "+arg0[0].getUnderlying();
			println "Shutting down"
			System.exit(0);
			
			}
		catch(e){
				e.printStackTrace();
			}
				
		}
	}



def loadModules() {
	try {
	Utility u = Utility.getInstance();
	u.addEPLFactory("EMA", "vadi.test.sarb.esper.util.EMAFactory")
	u.addEPLFactory("SLOPE", "vadi.test.sarb.esper.util.Regression")
	u.addEPLFactory("BUP", "vadi.test.sarb.esper.data.UpIndicator")
	
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("toDouble",
		"vadi.test.sarb.esper.util.SingleRowFunction", "toDouble");
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("diff",
		"vadi.test.sarb.esper.util.SingleRowFunction", "diff");
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("pnl",
		"vadi.test.sarb.esper.util.SingleRowFunction", "pnl");
	sb = "select * from StartEODQuote";
	
	
	u.deployModule(vadi.test.sarb.esper.Messages.getString("init.epl"))

//	u.deployModule(vadi.test.sarb.esper.Messages.getString("qstick.epl"))
	u.deployModule(vadi.test.sarb.esper.Messages.getString("highlow.epl"))
	
	u.deployModule(vadi.test.sarb.esper.Messages.getString("volatility.epl"))
	u.deployModule(vadi.test.sarb.esper.Messages.getString("ma.epl"))
	
	u.registerEventListener(sb, new StartEOD());
	}
	catch(Throwable e){
		e.printStackTrace();
	}
	
	
}

def TradeHandler() {
	println "Registering handlers"
	Utility u = Utility.getInstance();
	
	//u.registerEventListener("select * from TradeSignal.win:length(10)", new LongPosition());
	//u.registerEventListener("select * from TradeSignal", new ShortPosition());
	
	//u.registerEventListener('select * from emalong',new GenericListener())
	//su.registerEventListener('select * from emashort',new GenericListener())
	
//	u.registerEventListener('select * from LoadPortfolio', new PositionLoader());
	
	trdExp = 'select * from TradeSignal'
	//'.std:unique(price_timestamp) group by symbol'
	u.registerEventListener(trdExp, new LongPosition())
	//u.registerEventListener(trdExp, new ShortPosition())
	trdExp='select * from TradeSignal'
	u.registerEventListener(trdExp, new TradeListener())
	//trdExp='select * from StopLoss'
	u.registerEventListener(trdExp, new TradeListener());
	
	def lastSig = 'select * from LastEOD'
	u.registerEventListener(lastSig, new StopSystem());
		
	//u.addModuleListener("crossover_b", new GenericListener())
	//u.addModuleListener("crossover_s", new GenericListener())
	
	
	u.registerEventListener("select * from EODQuote",new EODHandler());
	//u.registerEventListener("select * from EODQuote",new ExitGenerator());
 
//Utility.addEPLFunction("EMA","vadi.test.sarb.esper.util.EsperEMA")
//u.addEPLFactory("EMA", "vadi.test.sarb.esper.util.EMAFactory")
//u.addEPLFactory("SLOPE", "vadi.test.sarb.esper.util.Regression")
///Utility.addEPLFactory("EMAFact","vadi.test.sarb.esper.groovy.EMAFactory")
//Utility.deployModule(vadi.test.sarb.esper.Messages.getString("ema.epl"))
//u.deployModule(vadi.test.sarb.esper.Messages.getString("ma.epl"))
//u.deployModule(vadi.test.sarb.esper.Messages.getString("sarb.epl"))
//Utility.addModuleListener('EMAAbove',new TradeListener())
//Utility.addModuleListener('EMABelow',new TradeListener())
//Utility.addModuleListener("EMA10", new GenericListener())
//Utility.addModuleListenert("EMA20", new GenericListener())
//Utility.addModuleListener("CP", new CpListener())
//Utility.addModuleListener("Slope", new CpListener())
//Utility.addModuleListener("Slope", new GenericListener())
//Utility.addModuleListener("emalongcross", new GenericListener())
//Utility.addModuleListener("Ematesting", new CpListener())
//Utility.addModuleListener("longsignal", new GenericListener())
//u.registerEventListener("select * from TradeSignal", new GenericListener())
//Utility.addModuleListener("cprice", new GenericListener())
//Utility.addModuleListener("slope", new GenericListener())

//Utility.addModuleListener("macd_b", new GenericListener())
//Utility.addModuleListener("macd_s", new GenericListener())
//Utility.addModuleListener("SMA20", new GenericListener())
//u.registerEventListener("select * from StatArb", new StatArbHandler())
//def sbstr="select * from EODQuote.win:length(100)  t1 , EODQuote.win:length(100)   t2  where t1.symbol='gld' and t2.symbol='xle' and t1.timestamp=t2.timestamp"
//u.registerEventListener(sbstr, new StatArbHandler().getCalc());
	

//u.registerEventListener("select * from EODQuote", new DummyListener());
//u.registerEventListener("select * from OrderStockQuotes order by timestamp", new DummyListener());

	
}

//String ema = new File(vadi.test.sarb.esper.Messages.getString("ema.epl")).text
//print "Contents of ema "+ema

def debug() {
	try {
	Utility u = Utility.getInstance();
	
	
	//u.registerEventListener("select * from OrderStockQuotes order by timestamp", new DummyListener());
	//str='select * from volatility'
	//str='select symbol,close, ((cast(close,float)-cast(prev(1,close),double))) as std '+
	//'from EODQuote.win:length(20)'+
	//' group by symbol '
	//str="select stddev(cast(close,double)) as sd ,symbol,timestamp from "+
	//'EODQuote.win:length(390) group by symbol'
	str='select * from symgrp'
	
	u.registerEventListener(str,new GenericListener());
	}
	catch(Throwable e){
		e.printStackTrace();
	}
	
}


//main lo
 def main()  {

print "Loading all the Quotes"
lp = new LoadPortfolio();
lp.setCash(10000);
lp.enqueue()


sb = new StatArb('SSO','QQQ')
//sb.enqueue();
for( st in vadi.test.sarb.esper.Messages.getString("EOD.quote.list").split(",")){
	print st+"\n"
	evt = new StartEODQuote(st);
	evt.enqueue();
}


//st = new StatArb('GLD','XLE')
//st.enqueue();
new File("C:\\temp\\test.csv").delete();
}

 

loadModules()
//TradeHandler()
debug()
main()

