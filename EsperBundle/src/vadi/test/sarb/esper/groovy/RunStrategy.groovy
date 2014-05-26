package vadi.test.sarb.esper.groovy

import vadi.test.sarb.event.ResetVariables

class RunStrategy {
	
	
	static void main(String[] args){
	new SignalGenerator().generateSignal(args)
	println "done"
	}

}
