package vadi.test.sarb.esper.groovy

import vadi.test.sarb.esper.portfolio.PFManager
import  vadi.test.sarb.esper.Messages

class GroovyHelper {
	
	//def static stlist=['BuyNHold','MA','HighLow','momentum']
	def static tmp = Messages.getString('load.strategy').split(',')
	def static stlist = tmp as Set
	def static hasExit(String symbol)
	{
		return PFManager.getInstance().hasExit(symbol)
		
	}
	def static nextStrategy()
	{
		println "stlist is "+stlist
	
		def st =''
		if ( !stlist.isEmpty()){
		st = stlist.iterator().next()
			 stlist.remove(st)
		}
		st
	}
	
	def static isstListEmpty(){
		//stlist.size() == 0 
		stlist.isEmpty()
	}
	
	def static reloadStrategy()
	{
		def sttmp = Messages.getString('load.strategy').split(',')
		stlist = sttmp as Set
	}
	def static double getFunds(String symbol)
	{
		return PFManager.getInstance().getFunds(symbol)
	}
}
