package vadi.test.sarb.esper.groovy

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;

import vadi.test.sarb.event.EODQuote
import vadi.test.sarb.esper.util.Utility

class Adx implements AggregationFunctionFactory,com.espertech.esper.epl.agg.aggregator.AggregationMethod {
	def newline ='\n'
	def name =''
	def  adx=0.0
	def start = false
	def counter = 0
	def max = 0
	EODQuote cur = null
	EODQuote prev = null
	def pdm=0
	def ndm=0
	def k=0
	def adp = 0
	def adm = 0
	
	def ADX =0
	
	def trace(args){
		Utility.getInstance().trace(args)
	}
	
	def void clear() {
		start = false
		max=counter =0
		
	}

	
	def void enter(Object arg0) {
		counter ++;
		if ( max < counter )
			max = counter;
		prev  = cur
		cur = (EODQuote)arg0
		trace("current "+cur+newline)
		trace("prev "+prev+newline)
		k = 2/(1+counter) as float
		trace('k '+k+newline)
		def atr =  getTR()
		trace('atr'+atr+newline)
		//SMA
		
		adx = (adx*(max-1)+ atr)/max as float
		
		def dp = getDP()
		trace('dp'+dp+newline)
		//EMA
		if ( !start)
		adp = (adp*(max-1)*dp)/max as float
		else
		adp = (adp*(1-k)+k*dp) as float
		
		def dm = getDM()
		trace('dm'+dm+newline)
		//EMA
		if ( !start)
		adm = (adm*(max-1)*dm)/max as float
		else 
		adm = (adm*(1-k)+dm*k) as float
		if ( adx != 0 ) {
		adp = ( adp/adx)
		adm = (adm/adx)
		}
		def idp = 100*adp
		def idm = 100*adm
		trace("adx "+adx+newline)
		trace ("adm "+idm+newline)
		trace('adp '+idp+newline)
		//print("counter="+counter+"max="+max+'\n');
		if (adp ==0 && adm ==0 )
		return 
		def dx =  (idp-idm)/(idp+idm) as float
		dx = Math.abs(dx)
		if (!start)
		ADX = (ADX*(max-1)*dx)/max as float
		else
		ADX = (ADX*(1-k)+k*dx) as float
		trace('ADX ='+ADX*100+newline)
	}

	
	def getValue() {
		if ( !start )
			return 0;
			else
			return ADX*100;
	}

	
	
	def void leave(Object arg0) {
		start = true
		counter --;
		
	}

	
	def Class getValueType() {
		return Double.class;
		
	}

	
	def AggregationMethod newAggregator() {
		// TODO Auto-generated method stub
		return new Adx()
	}


def void setFunctionName(String arg0) {
		name = arg0
		
	}

	
	def void validate(AggregationValidationContext arg0) {
		// TODO Auto-generated method stub
		
	}


	
	def getTR() {
		if ( cur == null || prev == null )
		return 0
		try {

		
		def chcc=Float.parseFloat(cur.high)-Float.parseFloat(cur.close) as float
		def chpc=Math.abs(Float.parseFloat(cur.high)-Float.parseFloat(prev.close)) as float
		def clpc=Math.abs(Float.parseFloat(cur.low)-Float.parseFloat(prev.close)) as float
		trace("chcc "+chcc+newline)
		trace("chpc "+chpc+newline)
		trace("clpc "+clpc+newline)
		def tr = Math.max(Math.max(chcc,chpc),Math.max(chpc,clpc)) as float
		trace(tr+newline)
		tr
		}
		catch(all)	{
			0
		}
	}
	
	def getDP(){
		
		if ( cur == null || prev == null )
		return 0
		//current high minus the prior high is greater than the prior low minus the current low.
		def chph = Float.parseFloat(cur.high)-Float.parseFloat(prev.high) as float
		def plcl = Float.parseFloat(prev.low)-Float.parseFloat(cur.low) as float
		trace('chph '+chph+newline)
		trace('plcl '+plcl+newline)
		if ( chph > plcl && chph > 0)
			chph
		else 0
				
		
	}
	
	def getDM(){
		if ( cur == null || prev == null )
		return 0
		//Directional movement is negative (minus) when the prior low minus the current low is greater than the current high minus the prior high.
		def plcl = Float.parseFloat(prev.low)-Float.parseFloat(cur.low) as float
		def chph = Float.parseFloat(cur.high)-Float.parseFloat(prev.high) as float
		if ( plcl > chph & plcl > 0)
			plcl
		else 0
		
	}

}
