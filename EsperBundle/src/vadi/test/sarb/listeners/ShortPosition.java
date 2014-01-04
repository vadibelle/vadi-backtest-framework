package vadi.test.sarb.listeners;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class ShortPosition implements UpdateListener  {
	/**
	 * 
	 */
	
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");

	PFManager pfm ;
	
	public ShortPosition() {
		super();
		if( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
		log.info("short listener");
		pfm = PFManager.getInstance();
		
	}

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
		TradeSignal sig = (TradeSignal)(arg0[0].getUnderlying());
		if( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
		log.info(sig.toString());
		if ( sig.getType().equalsIgnoreCase("BUY")|| 
				sig.getType().equalsIgnoreCase("CLOSE_SHORT"))
			pfm.closeShortPosition(sig);
		
			
		if ( sig.getType().equalsIgnoreCase("SELL") && !sig.getIndicator().equals("STOPLOSS"))
			pfm.openShortPosition(sig);
		
	//	pfm.positionValue(true);
		
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
