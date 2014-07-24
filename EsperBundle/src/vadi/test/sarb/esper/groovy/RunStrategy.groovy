package vadi.test.sarb.esper.groovy

import vadi.test.sarb.event.ResetVariables
import  vadi.test.sarb.esper.Messages
import vadi.test.sarb.esper.util.Utility

class RunStrategy {
	
	
	static void main(String[] args){
	def st = new Date()
	println st
	new File('C:/temp/output.csv').delete()
		
	new SignalGenerator().generateSignal(args)
	println "Long short done"
	Messages.setProperty('long.short','false')
	Messages.setProperty('clean.db','false')
	Messages.setProperty('init.db','false')
	Utility.getInstance().reset()
	def f = new File('/tmp/output.csv')
	f << 'long only \n'
	new SignalGenerator().generateSignal(args)
	println " long short done"
	println new Date()
	System.exit(0)
	
	
	}

}
