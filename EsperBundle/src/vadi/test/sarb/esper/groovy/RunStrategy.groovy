package vadi.test.sarb.esper.groovy

import vadi.test.sarb.event.ResetVariables
import  vadi.test.sarb.esper.Messages
import vadi.test.sarb.esper.util.Utility

class RunStrategy {
	
	
	static void main(String[] args){
	new SignalGenerator().generateSignal(args)
	println "Long only done"
	Messages.setProperty('long.short','true')
	Utility.getInstance().reset()
	new SignalGenerator().generateSignal(args)
	println " long short done"
	System.exit(0)
	
	}

}
