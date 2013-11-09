package vadi.test.sarb.listeners;

import java.io.Serializable;
import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class LongPosition implements UpdateListener,Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");

	PFManager pfm ;
	
	public LongPosition() {
		super();
		log.info("Long listener");
		pfm = PFManager.getInstance();
		
	}

	
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
						
		TradeSignal sig = (TradeSignal)(arg0[0].getUnderlying());
		log.info(sig.toString());
		/*if ( Long.parseLong(sig.price_timestamp) < Utility.getInstance().getCurrentTime() )
		{
			Utility.getInstance().info("Old event ignoring "+sig.toString());
			return;
		}*/
		if ( sig.getType().equalsIgnoreCase("BUY") && !sig.getIndicator().equals("STOPLOSS"))
			pfm.addLongPosition(sig);
		
			
		if ( sig.getType().equalsIgnoreCase("SELL"))
			pfm.closeLongPosition(sig);
		
		pfm.positionValue();
		
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
