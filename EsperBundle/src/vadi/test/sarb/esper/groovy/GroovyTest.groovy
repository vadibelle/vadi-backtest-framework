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
import vadi.test.sarb.event.LastEOD
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
import vadi.test.sarb.esper.data.UpIndicator;
import vadi.test.sarb.esper.groovy.*

//evaluate(new File((vadi.test.sarb.esper.Messages.getString("StatnUtil")))
 configFile = ""
 def processArgs()
 {
	def map = [:]

args.each {param ->
    def nameAndValue = param.split("=")
    map.put(nameAndValue[0], nameAndValue[1])
}
	
	 map.each {
		 if ( it.key == '-c')
		 configFile=it.value
	 }
	 println "$configFile is set"
	 if ( configFile != '')
	vadi.test.sarb.esper.Messages.loadProperties(configFile)
	 
 }
def loadModules() {
	try {
		
		//def gdir="C:/Users/Meku-laptop//git/VadiAlgoProject/EsperBundle/src/vadi/test/sarb/esper/groovy/"
		//evaluate(new File(gdir+"DbScripts.groovy"))
	
	println "$configFile is set"	
	Utility u = Utility.getInstance();
	epl_dir = vadi.test.sarb.esper.Messages.getString("epl.dir")
	u.addEPLFactory("EMA", "vadi.test.sarb.esper.util.EMAFactory")
	u.addEPLFactory("SLOPE", "vadi.test.sarb.esper.util.Regression")
	u.addEPLFactory("BUP", "vadi.test.sarb.esper.data.UpIndicator")
	u.addEPLFactory("CORREL", "vadi.test.sarb.esper.util.Correlation")
	
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("toDouble",
		"vadi.test.sarb.esper.util.SingleRowFunction", "toDouble");
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("diff",
		"vadi.test.sarb.esper.util.SingleRowFunction", "diff");
	u.getEpService().getEPAdministrator().getConfiguration().addPlugInSingleRowFunction("pnl",
		"vadi.test.sarb.esper.util.SingleRowFunction", "pnl");
	
	
	
	u.deployModule(epl_dir+"init.epl")
	u.deployModule(epl_dir+"context.epl")
	//u.deployModule(epl_dir+"bup.epl")
//	u.deployModule(epl_dir+"qstick.epl")
	u.deployModule(epl_dir+"highlow.epl")
	
	
	u.deployModule(epl_dir+"volatility.epl")
	//u.deployModule(epl_dir+"ma.epl")
	u.deployModule(epl_dir+"slope.epl")
	u.deployModule(epl_dir+"MAStdev.epl")
	
	sb = "select * from StartEODQuote";
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
	
	trdExp = 'select * from TradeSignal.std:unique(price_timestamp)'
	//'.std:unique(price_timestamp) group by symbol'
	u.registerEventListener(trdExp, new LongPosition())
	//u.registerEventListener(trdExp, new ShortPosition())
	trdExp='select * from StockSignal'
	u.registerEventListener(trdExp, new TradeListener())
	//trdExp='select * from StopLoss'
	//u.registerEventListener(trdExp, new TradeListener());
	
	def lastSig = 'select * from LastEOD'
	u.registerEventListener(lastSig, new StopSystem());
		
	//u.addModuleListener("crossover_b", new GenericListener())
	//u.addModuleListener("crossover_s", new GenericListener())
	
	
	u.registerEventListener("select * from EODQuote",new EODHandler());
	u.registerEventListener("select * from EODQuote",new ExitGenerator());
 
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
	str='select * from varcrossover'
	
	u.registerEventListener(str,new GenericListener());
	//u.registerEventListener(str,new CpListener());
	}
	catch(Throwable e){
		e.printStackTrace();
	}
	
}


//main lo
 def main()  {
	
 println args	
	 
print "Loading all the Quotes"
lp = new LoadPortfolio();
lp.setCash(10000);
lp.enqueue()


sb = new StatArb('SSO','QQQ')
//sb.enqueue();
def u = Utility.getInstance();
for( st in vadi.test.sarb.esper.Messages.getString("EOD.quote.list").split(",")){
	print st+"\n"
	u.addToSymboList(st)
	//evt = new StartEODQuote(st);
	//evt.enqueue();
}

def smbl = u.getSymbolList().get(0)
new StartEODQuote(smbl).enqueue()
//st = new StatArb('GLD','XLE')
//st.enqueue();
new File("C:\\temp\\test.csv").delete();
}

 
processArgs()
loadModules()
TradeHandler()
//debug()
 //println "load main"
main()

