package vadi.test.sarb.esper.groovy

import vadi.test.sarb.event.ResetVariables
import  vadi.test.sarb.esper.Messages
import vadi.test.sarb.esper.util.Utility

class RunStrategy {
	
	
	static void main(String[] args){
	new File('C:/temp/output.csv').delete()
		
	new SignalGenerator().generateSignal(args)
	println "Long short done"
	Messages.setProperty('long.short','false')
	Messages.setProperty('clean.db','false')
	Utility.getInstance().reset()
	def f = new File('C:/temp/output.csv')
	f << 'long only \n'
	new SignalGenerator().generateSignal(args)
	println " long short done"
	System.exit(0)
	
	
	}

}
