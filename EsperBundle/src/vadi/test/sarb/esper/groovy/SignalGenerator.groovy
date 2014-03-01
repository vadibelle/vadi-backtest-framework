package vadi.test.sarb.esper.groovy
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

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.esper.portfolio.Portfolio
import vadi.test.sarb.esper.util.GenericChart
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote
import vadi.test.sarb.event.LastEOD
import vadi.test.sarb.event.LoadPortfolio
import vadi.test.sarb.event.StartEODQuote
import vadi.test.sarb.event.StatArb;
import vadi.test.sarb.event.StockQuote
import vadi.test.sarb.event.TradeSignal
import vadi.test.sarb.listeners.DummyListener
import vadi.test.sarb.listeners.EODHandler
import vadi.test.sarb.listeners.ExitGenerator
import vadi.test.sarb.listeners.LongPosition
import vadi.test.sarb.listeners.MAIndicator

import vadi.test.sarb.listeners.MyListener
import vadi.test.sarb.listeners.PositionLoader
import vadi.test.sarb.listeners.ShortPosition
import vadi.test.sarb.listeners.StartEOD
import vadi.test.sarb.listeners.StatArbHandler


import vadi.test.sarb.esper.Messages;
import vadi.test.sarb.esper.data.UpIndicator;
import vadi.test.sarb.esper.db.DbUtil;
import vadi.test.sarb.esper.groovy.*

class SignalGenerator {
//evaluate(new File((vadi.test.sarb.esper.Messages.getString("StatnUtil")))
def configFile = ''
def symbolList = ''
def sList = []
def init=false
def modlist = [:]
 

def loadModules(namelist){
	def ed = Messages.getString("epl.dir")
	def u = Utility.getInstance()
	
	while( !u.isSymbolListEmpty())
	sleep(1100)
	
	modlist.each {
		u.undeploy(it.value)
	}
	println "loading modules "+namelist
	
	namelist.split(',').each {f->
		def str = u.deployModule(ed+f+'.epl')
		modlist.putAt(f,str)
		}
	
//	def sb = "select * from StartEODQuote";
//	u.registerEventListener(sb, new StartEOD());
}

def loadModules() {
	if ( !init )
	{
		println "not initialized"
		return
	}
	
	println "$configFile is set"	
	
	def mods = Messages.getString("load.modules")
	loadModules(mods)
	
//	u.deployModule(epl_dir+"init.epl")
//	u.deployModule(epl_dir+"context.epl")
//	u.deployModule(epl_dir+"bup.epl")
//	u.deployModule(epl_dir+"qstick.epl")
	//u.deployModule(epl_dir+"Highlow.epl")
	
	
//	u.deployModule(epl_dir+"volatility.epl")
	//u.deployModule(epl_dir+"ma.epl")
	//u.deployModule(epl_dir+"slope.epl")
	//u.deployModule(epl_dir+"MAStdev.epl")
	//u.deployModule(epl_dir+"Momentum.epl")
	

}
def loadStrategy(name)
{
	if (name == '')
	name = Messages.getString('load.strategy')
 	def namelist = Messages.getString('strategy.'+name)
	 loadModules(namelist)
}

def TradeHandler() {
	if ( !init )
	{
		println "not initialized"
		return
	}
	println "Registering handlers"
	Utility u = Utility.getInstance();
	
	//u.registerEventListener("select * from TradeSignal.win:length(10)", new LongPosition());
	//u.registerEventListener("select * from TradeSignal", new ShortPosition());
	
	//u.registerEventListener('select * from emalong',new GenericListener())
	//su.registerEventListener('select * from emashort',new GenericListener())
	
//	u.registerEventListener('select * from LoadPortfolio', new PositionLoader());
	
	def trdExp = 'select * from TradeSignal.std:unique(price_timestamp)'
	//'.std:unique(price_timestamp) group by symbol'
	u.registerEventListener(trdExp, new LongPosition())
	if (vadi.test.sarb.esper.Messages.getString('long.short') == 'true')
		u.registerEventListener(trdExp, new ShortPosition())
	trdExp='select * from StockSignal'
	def l = new TradeListener()
	u.registerEventListener(trdExp, l)
	trdExp='select * from StopLoss'
	u.registerEventListener(trdExp, l);
	
	def lastSig = 'select * from LastEOD'
	u.registerEventListener(lastSig, new ConsolidateOutput());
		
	//u.addModuleListener("crossover_b", new GenericListener())
	//u.addModuleListener("crossover_s", new GenericListener())
	
	
	u.registerEventListener("select * from EODQuote",new EODHandler());
	if (vadi.test.sarb.esper.Messages.getString('stop.loss') == 'true')
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
	//def str='select * from StockSignal'
	def l = new GenericListener()
//u.registerEventListener('select * from rsi',l)
//u.registerEventListener('select * from emashort',l)
//u.registerEventListener('select * from emalong',l)
//	u.registerEventListener('select * from bupnumber',l)
	//u.registerEventListener('select * from nullstr',l)
	
	//u.registerEventListener(str,new CpListener());
	}
	catch(Throwable e){
		e.printStackTrace();
	}
	
}


def loadSymbols() {
		def u = Utility.getInstance()
		if ( symbolList != '')
			new File(symbolList).eachLine { line ->
				if (!line.startsWith('#'))
					line.split(',').each { st->
						print st +" "
						u.addToSymboList(st)
						sList.add(st)
					}
				println ""

			}
		def qList = vadi.test.sarb.esper.Messages.getString("EOD.quote.file")
		println "quotes file $qList"

		if ( qList != '')
			new File(qList).eachLine { line ->
				for ( st in line.split(',')) {
					print st+" "
					u.addToSymboList(st)
					sList.add(st)
				}
				println ""
			}

		println "Loading all the Quotes"
		//def lp = new LoadPortfolio();
		//lp.setCash(10000);
		//lp.enqueue()

		for( st in vadi.test.sarb.esper.Messages.getString("EOD.quote.list").split(",")){
			print st+"\n"
			u.addToSymboList(st)
			//evt = new StartEODQuote(st);
			//evt.enqueue();
		}

		//println "SYMBOL LIST"+u.getSymbolList()
		//println "Slist is $sList"
		def smbl = u.getSymbolList().get(0)
		new StartEODQuote(smbl).enqueue()
	
		
	}


def init(args) {

ProcessArgs pArgs = new ProcessArgs(args)
configFile = pArgs.configFile
symbolList = pArgs.symbolList

//sb.enqueue();
def u = Utility.getInstance();


if ( vadi.test.sarb.esper.Messages.getString("clean.db") == "true" ) {
	def db = new DbScripts()
	db.cleanDB()
}	

new File("C:\\temp\\test.csv").delete();

def config = u.getEpService().getEPAdministrator().getConfiguration();
config.addEventTypeAutoName("vadi.test.sarb.event");

def epl_dir = Messages.getString("epl.dir")
u.createIntVar('si', Integer.parseInt(Messages.getString("var.si")))
u.createIntVar('li', Integer.parseInt(Messages.getString("var.li")))
u.createIntVar('st', Integer.parseInt(Messages.getString("var.si")))
u.createIntVar('lt', Integer.parseInt(Messages.getString("var.lt")))
u.createIntVar('ml', Integer.parseInt(Messages.getString("var.ml")))


u.addEPLFactory("EMA", "vadi.test.sarb.esper.util.EMAFactory")
u.addEPLFactory("SLOPE", "vadi.test.sarb.esper.util.Regression")
u.addEPLFactory("BUP", "vadi.test.sarb.esper.data.UpIndicator")
u.addEPLFactory("CORREL", "vadi.test.sarb.esper.util.Correlation")


config.addPlugInSingleRowFunction("toDouble",
	"vadi.test.sarb.esper.util.SingleRowFunction", "toDouble");
config.addPlugInSingleRowFunction("diff",
	"vadi.test.sarb.esper.util.SingleRowFunction", "diff");
config.addPlugInSingleRowFunction("pnl",
	"vadi.test.sarb.esper.util.SingleRowFunction", "pnl");
config.addPlugInSingleRowFunction("pnld",
	"vadi.test.sarb.esper.util.SingleRowFunction", "pnld");
config.addPlugInSingleRowFunction("atr",
	"vadi.test.sarb.esper.util.SingleRowFunction", "atr");
config.addPlugInSingleRowFunction("longPosition",
	"vadi.test.sarb.esper.util.SingleRowFunction", "longPosition");
config.addPlugInSingleRowFunction("hasExit",
	"vadi.test.sarb.esper.groovy.GroovyHelper", "hasExit");

def sb = "select * from StartEODQuote";
u.registerEventListener(sb, new StartEOD());

init = true

  }

def loadPosition()
{
	try{
	def db = new DbUtil()
	def u = Utility.getInstance()
	def sql = ''
	def posFile = Messages.getString('position.list')
	db.execute('truncate table position')
	sql = 'insert into position select * from CSVREAD(\''+posFile+'\')'
	db.execute(sql)
	sql = 'select distinct symbol from position'
	def res = db.execute(sql)
	//sql = 'select top 1 date from position order by date desc'
	//def dt = db.execute(sql)
	def today = new Date().format('yyyy-mm-dd')
	res.each{
		if (it.get(0) != 'SYMBOL'){
		println "row "+it.get(0)
		sql = "select top 1 date from position  where symbol='"+it.get(0)+"'"
		' order by date desc'
		def dt = db.execute(sql)
		def d = dt.get(1).get(0)
		println 'date '+d.split(' ')[0]
		def q = new StartEODQuote()
		q.symbol = it.get(0)
		q.stDate = d.split(' ')[0]
		q.endDate = today
		u.addToPortfolio(q)
		//q.enqueue()
		/*def p = new Portfolio()
		p.symbol = it.get(1)
		
		p.ammount = Messages.getString("original.ammount") as double
		p.stopLossAmmount = Messages.getString('stop.loss.ammount') as double
		p.stopLoss = Messages.getString('stop.loss.exit.ratio') as double
		p.tradeSize = Messages.getString('trade.size') as double
		def d1 = it.get(2) as double
		p.cash =  ( p.ammount - d1 ) > 0  ? ( p.ammount - d1) : 0
		//println p.cash
		if (it.get(3) == 'BUY')
			p.positions = it.get(0) as long
		else
			p.short_positions = it.get(0) as long*/
		
		 	
		 PFManager.getInstance().loadPosition(it.get(0))
		 println PFManager.getInstance().positionValue(true)
				
		}
		
	}
	
	}
	catch (Throwable e) {
		e.printStackTrace()
	}
	
}

def buyNHoldTest()
{
	def u = Utility.getInstance();
	while( !u.isSymbolListEmpty()){
		sleep(1100)
		
	}
	Messages.setProrperty("system.exit",'true')
	Messages.setProrperty('stop.loss','false')
	modlist.each {
		u.undeploy(it.value)
	}
	
	def epl_dir = Messages.getString("epl.dir")
	def str = u.deployModule(epl_dir+"buynhold.epl")
	modlist.putAt('buynhold',str)
	
			
	loadSymbols()
	
}

//main lo
 static  main(String[] args)  {
	def  gv = new SignalGenerator()
	gv.init(args)
	gv.loadStrategy('')
	gv.TradeHandler()
	gv.loadSymbols()
	gv.debug()
	def fwdTest = Messages.getString('forward.test')
	/*if ( fwdTest != 'true' ) {
	gv.stratergyTest()
	}
	else
	{
		gv.loadPosition()
		Utility.getInstance().getPortfolioList()[0].enqueue()
	}*/
	
	// load mkt
	gv.buyNHoldTest()

 }

}