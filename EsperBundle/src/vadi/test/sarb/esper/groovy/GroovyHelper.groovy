package vadi.test.sarb.esper.groovy

import vadi.test.sarb.esper.portfolio.PFManager;

class GroovyHelper {
	
	def static hasExit(String symbol)
	{
		return PFManager.getInstance().hasExit(symbol)
		
	}
	
}
