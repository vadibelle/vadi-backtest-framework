package vadi.test.sarb.listeners;

import java.util.HashMap;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class ShortPosition implements UpdateListener  {
	/**
	 * 
	 */
	
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.listeners");

	PFManager pfm ;
	boolean print = false;
	
	public ShortPosition() {
		super();
			if ( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
				print  = true;
			if ( print)
				log.info("Long listener");
		log.info("short listener");
		pfm = PFManager.getInstance();
		
	}

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
	
			TradeSignal sig = null;
			if ( arg0[0].getUnderlying() instanceof HashMap)
			{
				HashMap map = (HashMap)arg0[0].getUnderlying();
				sig = new TradeSignal();
				sig.close = map.get("close").toString();
				sig.high = map.get("high").toString();
				sig.indicator = map.get("indicator").toString();
				sig.low  = map.get("low").toString();
				sig.open = map.get("open").toString();
				sig.price_timestamp = map.get("price_timestamp").toString();
				sig.symbol = map.get("symbol").toString();
				sig.type = map.get("signal").toString();
				
			}
			else
			 sig = (TradeSignal)(arg0[0].getUnderlying());			
			
		
		if( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
		
			if (print)
		log.info(sig.toString());
		
		if ( sig.getType().equalsIgnoreCase("BUY")|| 
				sig.getType().equalsIgnoreCase("CLOSE_SHORT"))
			pfm.closeShortPosition(sig);
		
			
		if ( sig.getType().equalsIgnoreCase("SELL") ||(sig.getType().equalsIgnoreCase("CLOSE_LONG"))&& !sig.getIndicator().equals("STOPLOSS"))
			pfm.openShortPosition(sig);
		
		//pfm.positionValue(true);
		
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
