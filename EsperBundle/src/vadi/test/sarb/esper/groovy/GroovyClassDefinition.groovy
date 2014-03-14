package vadi.test.sarb.esper.groovy

import java.awt.geom.Arc2D.Double;
import java.awt.image.Kernel;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.esper.util.GenericChart;
import vadi.test.sarb.event.LastEOD
import vadi.test.sarb.event.LoadPortfolio
import vadi.test.sarb.event.StartEODQuote
import vadi.test.sarb.event.StockSignal
import vadi.test.sarb.event.StopLoss

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
			if ( obj instanceof StockSignal )
			{
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
	def outfile = "C:\\temp\\output.csv"

	def StopSignal() {
		def f = new File(outfile)
		if ( f.exists())
			f.delete()

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
	def outfile = "C:\\temp\\output.csv"
	def output
	def map
	def symList
	def ConsolidateOutput() {
		def f = new File(outfile)
		if ( f.exists())
			f.delete()
		output = []
		map = [:]
		symList = []

		
		

	}

	public void update(EventBean[] arg0, EventBean[] arg1) {
		try{
			// TODO Auto-generated method stub
			//print "Event1 received"+arg0[0].getUnderlying()+" length "+arg0.length+"\n";
			//println "Shutting down"
			def f = new File(outfile)
			def doExit = false
			if ( Messages.getString('system.exit') == 'true')
				doExit = true
			LastEOD evt = arg0[0].getUnderlying();
			def u = Utility.getInstance();
			symList.add(evt.getSymbol())
			// Forward or backtest
			def fwd  = Messages.getString("forward.test")
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
						output.add(map)
						
					}
					SendOutput(f)
					if ( doExit)
					System.exit(0)
					
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
				SendOutput(f);
				if ( doExit)
				System.exit(0)
			}
			//SendOutput(f);
		}

		catch(e){
			println "Error consolidating "+e
			//e.printStackTrace();
		//	System.exit(0)
			
		}
		

	}

	private SendOutput(File f) {
		f = new File(outfile)
		f.withWriter { fw ->
			output = output.sort { it.get("price_timestamp")}
			output = output.sort { it.get("returns")}
			def mailStr = ""
			output.each {
				println it
				//println ""
				fw.writeLine(it.toString())
				mailStr += it.toString()+"\n"
			}
			def isMail = Messages.getString('send.mail')
			if (isMail == 'true') {
			def sm = new SendMail()
			if ( Messages.getString('forward.test').equalsIgnoreCase('true'))
				sm.subject = 'ForwardTest'
			//	sm.send(output.toString())
			sm.send(mailStr)
			}
		}
		
	}
}

class UpdateStatistics implements UpdateListener {
	def p = PFManager.getInstance()
	def avol = 0
	def os  = 0 
	def aswing = 0
	def UpdateStatistics()
	{
		
	}
	public void update(EventBean[] arg0, EventBean[] arg1){
		try {
					
			def symbol = arg0[0].get('symbol')
			if (!p.hasPosition(symbol))
				return
			def pos = p.getPosition(symbol)
			
			 avol =  arg0[0].get('avgVol')
			 aswing = arg0[0].get('avgSwing')
			os = arg0[0].get('openSwing')
			
			pos.avgVol = avol
			pos.avgSwing = aswing
			pos.openSwing = os
		
				
		}
		catch(e)
		{
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

class ProcessArgs {
	def configFile = ''
	def symbolList = ''
	def ProcessArgs(args)
	{
		def map = [:]

		args.each {param ->
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