package vadi.test.sarb.esper.groovy

import vadi.test.sarb.event.ResetVariables
import  vadi.test.sarb.esper.Messages
import vadi.test.sarb.esper.util.Utility

class RunStrategy {
	
	
	static void main(String[] args){
	def st = new Date()
	println st
	//println System.getProperty('java.class.path')
	println args
	new File('C:/temp/output.csv').delete()
	def sig = new SignalGenerator()	
	sig.init(args)
	//Messages.setProperty('init.db','true')
	sig.initDb()
	sig.generateSignal(args)
	//sig.generateSignal(args)
	println "Long short done"
	
	Utility.getInstance().reset()
	def f = new File('/tmp/output.csv')
	f << 'long only \n'
	sig = new SignalGenerator()
	sig.init(args)
	Messages.setProperty('long.short','false')
	Messages.setProperty('clean.db','false')
	Messages.setProperty('init.db','false')
	sig.initDb()
	sig.generateSignal(args)
	//sig.reset()
	//sig.generateSignal(args)
	println " long  done"
	println new Date()
	new DbScripts().calcSharpe()
	if ( !Messages.getString('do.chart').equals('true'))
	System.exit(0)
	
	
	}

}
