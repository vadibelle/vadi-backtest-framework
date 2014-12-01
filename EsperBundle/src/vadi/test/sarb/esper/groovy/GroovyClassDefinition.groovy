package vadi.test.sarb.esper.groovy

import java.awt.geom.Arc2D.Double;
import java.awt.image.Kernel;

import vadi.test.sarb.esper.db.DbUtil
import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.event.EODQuote
import vadi.test.sarb.event.LastEOD
import vadi.test.sarb.event.LoadPortfolio
import vadi.test.sarb.event.StartEODQuote
import vadi.test.sarb.event.StockSignal
import vadi.test.sarb.event.StopLoss
import vadi.test.sarb.event.TradeSignal

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import vadi.test.sarb.esper.util.*
import vadi.test.sarb.esper.Messages;
import vadi.test.sarb.listeners.PositionLoader;

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
		//	def out = new File('/tmp/test.csv')
			//	println "arg0 length "+arg0.length
			for ( e in arg0 ){
				//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
				Utility.getInstance().debug( "event "+e.getUnderlying())
				/*def p = e.getProperties();
				p.each { k,v-> 
					if ( k == 'timestamp')
					
					print k+"="+v+" "} 
				println " "*/
				/*for ( i in p.values())
				{
					out.append(i);
					out.append(",");
					out.append("\n");
				}*/

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
		//	Utility.info("Inserting into "+obj.getClass().getName())
			if ( obj instanceof java.util.Map)
			{
				StockSignal sig = new StockSignal(
					obj.get('symbol'), obj.get('open'),obj.get('high'),
					obj.get('low'),obj.get('close'),obj.get('signal'),
					obj.get('indicator'), obj.get('price_timestamp'))
				PFManager.getInstance().addLastTrade(sig.getSymbol(), sig.toString())
			}
			if ( obj instanceof StockSignal )
			{
				//Utility.log("Inserting into "+obj.toString());
				StockSignal sig = (StockSignal)obj;
				PFManager.getInstance().addLastTrade(sig.getSymbol(), sig.toString())
			}
			if ( obj instanceof StopLoss )
			{
				StopLoss sig = (StopLoss)obj;
				PFManager.getInstance().addLastTrade(sig.getSymbol(), sig.toString())
			}
			
			//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
			//println "TradeReceived "+obj;
			//TradeSignal sig = arg0[0].getUnderlying();
			//PFManager.getInstance().addLastTrade(sig.getSymbol(), sig.toString())
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
	//def outfile = "C:\\temp\\output.csv"
	def outfile = Messages.getString('outfile')

	def StopSignal() {
		def f = new File(outfile)
	//	if ( f.exists())
	//		f.delete()

	}
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


class ConsolidateOutput implements UpdateListener {
	//def outfile = "C:\\temp\\output.csv"
	def outfile = Messages.getString('outfile')
	def output
	def map
	def symList
	def doExit = false
	def finish = false
	def fwd = 'false'
	def bestalgo = [:]
	def indList = []
	def dbu ;
	def ConsolidateOutput() {
		def f = new File(outfile)
		//if ( f.exists())
		//	f.delete()
		output = []
		map = [:]
		symList = []
		dbu = new DbScripts()
		if ( Messages.getString('system.exit') == 'true')
		doExit = true
		fwd  = Messages.getString("forward.test")
		println "output "+output.size()
	}

	public void update(EventBean[] arg0, EventBean[] arg1) {
		try{
			// TODO Auto-generated method stub
			//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
			//println "Shutting down"
			def f = new File(outfile)
			def varList = Utility.getInstance().getVariableValueAll()	
			LastEOD evt = arg0[0].getUnderlying();
			def u = Utility.getInstance();
			symList.add(evt.getSymbol())
			// Forward or backtest
		//	def fwd  = Messages.getString("forward.test")
			PFManager pfm = PFManager.getInstance();
			if ( fwd != 'true' ) {
				u.removeFromSymbolList(evt.getSymbol())

				println "Last event received "+evt.getSymbol();
				//println " Details for "+evt.getSymbol()
				//map =  pfm.getDetails(evt.getSymbol())
				//println "map is $map"
				//println "output is $output"
				//output.add(map)
				//output += "\n"
				if( u.isSymbolListEmpty()){
					println "Consoliddate output"
					symList.each { sym ->
						map = pfm.getDetails(sym)
						
						pfm.removePosition(sym)
						if ( map.size() > 0){
							map << varList
							output.add(map)
							dbu.persistResult(map)
						}
					}
				//	SendOutput(f)
					//if ( doExit)
					//System.exit(0)
					
				}
				else {
					//	PFManager.getInstance().setCash(10000)
					def s = u.getSymbolList().get(0)
					def sq = new StartEODQuote(s)
					sq.enqueue()

				}
			}
			// begin forward test
			else if (fwd == 'true' && !u.isPortfolioEmpty())
			{
				
				def q = u.getPortfolioList()[0]
				u.removeFromPortfolio(q)
				q.enqueue()
			}
			else {
				println "Consolidating.."
				symList.each { sym ->
					map = pfm.getDetails(sym)
					pfm.removePosition(sym)
					output.add(map)
				}
				//SendOutput(f);
			}
			if ( u.isSymbolListEmpty() && u.isPortfolioEmpty() && GroovyHelper.isstListEmpty())
			SendOutput(f);
		}

		catch(e){
			println "Error consolidating "+e
			e.printStackTrace();
		//	System.exit(0)
			
		}
	}

	def sortOutput()
	{
				
		output.each { map ->
			if (map != null && map.size() > 0 ) {
			println map
						
			def sym = map.get('symbol')
			def tot = map.get('total') as double
			def ltrade  = map.get('last Trade')
			
			if ( ltrade != null ) {
			def ks = sym+','			
			ltrade.split(',').each { k ->
				if ( k.contains('indicator')  || k.contains('price_timestamp') 
					|| k.contains('type')|| k.contains('sharpe') || k.contains('total')) 
					ks += k.split('=')[1]+','
			}
			
			//def ks = ltrade.get 'symbol' +','+ ltrade.get 'indicator' +','+ ltrade.get 'price_timestamp'
			indList.push ks
			}			
			if ( bestalgo.containsKey(sym)){
				def ntot =  bestalgo.get(sym).get('total') as double
			//	def ntot =  bestalgo.get(sym).get('sharpe') as double
				//println "$sym $tot $ntot"
				if ( tot > ntot  ){
					bestalgo.put(sym,map)
					//println "inserted > $map"
				}
			}	
			else{ 
				bestalgo.put(sym, map)
				//println "inserted $map entry null"
			}
			
			}
		}
		println "Best algo"
		def tmp = ['Best algo':'symbol']
		
	//	output.add(tmp)
		
		bestalgo.each {
			println it
		//	output.add(it)
		}
		println 'Algo list'
		//output.add(['algo list':'symbol'])
		indList.each {
				println it
				//output.add(it)
			}
		
		
	}
	private SendOutput(File f) {
		if (! GroovyHelper.isstListEmpty()){
			return
		}
		//Get index
		def indx = Messages.getString('mkt.indicators')
		
		def indxRtn = 1
		def indxVol= 0
		//Get index volatility, returns for sharpe
		output.each {
		if ( indx != null && it != null && it.getAt('symbol') == indx )
			{
				indxRtn = it.getAt('returns') as float
				indxVol = it.getAt('volatility') as float
			}
		}
		//add sharpe and relative volatility for each
		output.each {
			def eqRtn = 0;
			if ( it != null ) {
			if ( it.containsKey('returns'))
			eqRtn = it.getAt('returns') as float
			
			def vol = 0 
			def relVol = 0
			if ( it.containsKey('volatility'))
					vol = it.getAt('volatility') as float
			def ddr = 0; def dd = 0
			if ( it.containsKey('drawDown'))
				dd = it.getAt('drawDown') as float
			
			def sr = 0
			if ( vol != 0 ){
				sr = (eqRtn - indxRtn)*100/vol as float
				ddr = (eqRtn-dd)*100/vol as float
			 it.putAt('sharpe', sr)
			 it.put('ddSharpe',ddr)
			 relVol = vol/indxVol as float
			 it.putAt('relVolatility',relVol)
			}
			
			}
							
		}
		
		sortOutput()
		f = new File(outfile)
		//f.withWriter { fw ->
		//{
			output = output.sort { it.getAt("price_timestamp")}
			output = output.sort { it.getAt("returns")}
			def fk = new File("/tmp/keys.csv")
			def kv = new File("/tmp/value.csv")
			def mailStr = ""
			output.each {
				println it
				//println ""
				//fw.writeLine(it.toString())
				f << it.toString()+'\n'
				mailStr += it.toString()+"\n"
				fk << it.keySet() +'\n'	
				kv << it.values() + '\n'		
				}
				mailStr += "Best Algo list \n"
				bestalgo.each {
					//fw.writeLine(it.toString())
					f<< it.toString()+'\n'
					mailStr += it.toString()+"\n"
					
				}
				mailStr += "Indicators list\n"
				indList.each {
					//fw.writeLine(it.toString())
					f << it.toString()+'\n'
					mailStr += it.toString()+"\n"
				}
				
			def isMail = Messages.getString('send.mail')
			if (isMail == 'true') {
			def sm = new SendMail()
			if ( Messages.getString('forward.test').equalsIgnoreCase('true'))
				sm.subject = 'ForwardTest'
			//	sm.send(output.toString())
			//sm.send(mailStr)
			}
		//}
		
		println "Processing output"
		Utility.getInstance().release()
		output = [] 
		bestalgo = [:]
		indList = []
		map = [:]
		symList = []
		
		if ( doExit)
		System.exit(0)
			
	}
}


class UpdateStatistics implements UpdateListener {
	def p = PFManager.getInstance()
	def avol = 0
	def os  = 0 
	def aswing = 0
	def UpdateStatistics()
	{
		Utility.getInstance().trace("CREATED updatedstatistices")
	}
	public void update(EventBean[] arg0, EventBean[] arg1){
		try {
					
			def symbol = arg0[0].get('symbol')
			if (!p.hasPosition(symbol))
				return
			def pos = p.getPosition(symbol)
			
			// Utility.getInstance().info(arg0[0].getUnderlying().toString())
			Utility.getInstance().debug("updatestats "+arg0[0].getUnderlying())
			def p = arg0[0].getProperties()
			p.each { k ,v->
				if ( v == null || v.equals('null'))
				return
				}
			
			 avol =  arg0[0].get('avgVol')
			 aswing = arg0[0].get('avgSwing')
			os = arg0[0].get('openSwing')
		
			pos.avgVol = avol
			pos.avgSwing = aswing
			pos.openSwing = os
			
			pos.rsi = arg0[0].get('rsi')
			pos.macd = arg0[0].get('macd')
			pos.vol = arg0[0].get('vol')
				
		}
		catch(e)
		{
			//e.printStackTrace()
			println e
		}
		
	}
}

/*class RSIIndicator
implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod{
	
	def name = ''
	def start = false
	def list = []
	def emaU = 0 
	def emaD = 0
	def max = 0
	def counter = 0
	def prev = 0
	def k = 0.75
	def rsi=0
	
	
	public RSIIndicator()
	{
		
	}
	def void print(String str){
		if ( Messages.getString('do.print').equals('true'))
		println str
	}
	public void clear() {
		list = []
		start = false
		emaU=emaD=0
		
	}
	
	
	public void enter(Object arg0) {
		// TODO Auto-generated method stub
		counter ++;
		if ( max < counter )
		max = counter;
		print('counter='+counter+'max='+max)
	//	def k = (2/(1+counter))
		
		def c = Double.parseDouble(arg0.toString());
		if ( prev > c)
			emaD = (prev-c)k + (1-k)emaD
		if ( c > prev)
			emaU = (c-prev)k + (1-k)emaU
		
		if( c == prev )
		{
			emaD = (1-k)emaD
			emaU = (1-k)emaU
		}
		prev = c
		if ( emaD !=0 )
			rsi = emaU/emaD
		
		if ( rsi != 0 )
			rsi = 100 - (100/(1+rsi))
		print('rsi='+rsi)
		list.add(arg0)
			
	}

	
	public Object getValue() {
		if ( !start )
		return 0
		else
		return rsi
		
		
	}

	
	public void leave(Object arg0) {
		start = true
		def up = 0
		def down = 0
		//for ( i in 1..list.size())
		counter--;
		list.remove(arg0)
				
	}

	
	public Class getValueType() {
		// TODO Auto-generated method stub
		return Double.class;
	}

	
	public AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		return new RSIIndicator()
	}

	
	public void setFunctionName(String arg0) {
		// TODO Auto-generated method stub
		name = arg0
		
	}

	
	public void validate(AggregationValidationContext arg0) {
		// TODO Auto-generated method stub
		if (arg0.getParameterTypes()[0] != Float.class) {
			throw new IllegalArgumentException("Concat aggregation requires a parameter of type float");
		  
	}
		
	}
	
}*/

class ProcessArgs_old {
	def configFile = ''
	def symbolList = ''
	def ProcessArgs(args)
	{
		def map = [:]
		
		args.each {param ->
			println "param is "+param
			def nameAndValue = param.split("=")
			map.put(nameAndValue[0], nameAndValue[1])
		}

		map.each {
			if ( it.key == '-c')
				configFile=it.value
			if ( it.key == '-s')
				symbolList = it.value
		}
		println "$configFile is set"
		if ( configFile != '')
			vadi.test.sarb.esper.Messages.loadProperties(configFile)

	}

}

class ProcessArgs{
	def configFile = ''
	def symbolList = ''
	def ProcessArgs(args)
	{
		def cli = new CliBuilder( usage: 'groovy BuildNRun -c <property file name> -s <symbols list file>')
		cli.c(argName:'config', longOpt:'config', args:1, required:true, type:GString, 'config')
		cli.s(argName:'symbol', longOpt:'symbol', args:1, required:false, type:GString, 'symbol')
		def opt = cli.parse(args)
		if (!opt)
		return
		if ( opt.c)
		{	
			configFile = opt.c;
			 Messages.loadProperties(configFile)
			 println "$configFile is set"
			 }
		if (opt.s )
			symbolList  = opt.s
	}
	
	
}


class Plotter implements UpdateListener {
		GenericChart chart 
		
		Plotter(){
			chart = Utility.addChart("signal")
		}
	
		public void update(EventBean[] arg0, EventBean[] arg1) {
			try{
				def obj = arg0[0].getUnderlying();
			//	Utility.info("Inserting into "+obj.getClass().getName())
				if ( obj instanceof EODQuote){
					def series = obj.getAt('symbol')+'-Eod'
					def cl = obj.getAt('close') as float
					def ts = obj.getAt('timestamp')
					
					chart.addSeries(series)
					chart.addData(series,ts,cl)
				}
				if ( obj instanceof java.util.Map)
				{
					def series = obj.get('symbol')+obj.get('indicator-')+obj.get('signal')
					def ts = obj.get('price_timestap') as long
					def cl = obj.get('close') as float
					chart.addSeries(series)
					chart.addData(series,ts,cl)
					
				}
				if ( obj instanceof StockSignal )
				{
					//Utility.log("Inserting into "+obj.toString());
					StockSignal sig = (StockSignal)obj;
					def series = sig.getSymbol()+sig.getIndicator()+sig.getType()
					def cl = sig.getClose() as float
					def ts = sig.getPrice_timestamp() as long
					chart.addSeries(series)
					chart.addData(series,ts,cl)
					
				}
				if ( obj instanceof StopLoss )
				{
					StopLoss sig = (StopLoss)obj;
					def series = sig.getSymbol()+'-STOPLOSS-'+sig.getType()
					def cl = sig.getClose() as float
					def ts = sig.getTimestamp()
					chart.addSeries(series)
					chart.addData(series,ts,cl)
					
				}	
				
			}
			catch(e){
				e.printStackTrace();
			}
	
		}
	}
