package vadi.test.sarb.listeners;

import java.io.Serializable;
import java.util.HashMap;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class LongPosition implements UpdateListener,Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.listeners");

	PFManager pfm ;
	boolean print = false;
	
	public LongPosition() {
		super();
		if ( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
			print  = true;
		if ( print)
			log.info("Long listener");
		pfm = PFManager.getInstance();
		
	}

	
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
		//System.out.println(arg0[0].toString())		;	
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
		//System.out.println("print "+print+" Sig "+sig);
		if (print)
		log.info(sig.toString());
		/*if ( Long.parseLong(sig.price_timestamp) < Utility.getInstance().getCurrentTime() )
		{
			Utility.getInstance().info("Old event ignoring "+sig.toString());
			return;
		}*/
		if ( sig.getType().equalsIgnoreCase("BUY") && !sig.getIndicator().equalsIgnoreCase("STOPLOSS"))
			pfm.addLongPosition(sig);
		
			
		if ( sig.getType().equalsIgnoreCase("SELL") || 
				sig.getType().equalsIgnoreCase("CLOSE_LONG"))
			pfm.closeLongPosition(sig);
		
		pfm.positionValue(true);
		
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}

}
