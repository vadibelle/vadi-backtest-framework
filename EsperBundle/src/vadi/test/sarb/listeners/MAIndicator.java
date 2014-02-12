package vadi.test.sarb.listeners;

import java.util.HashMap;
import vadi.test.sarb.event.EODQuote;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class MAIndicator implements UpdateListener {
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.test.sarb.listeners");
	
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
			log.info(arg0[0].getUnderlying().toString());
			log.info(arg0[0].get("eq").toString());
			log.info(arg0[0].get("hl1").toString());
			EODQuote evt = (EODQuote)arg0[0].get("eq");
			HashMap h1 =  (HashMap)(arg0[0].get("hl1"));
			HashMap h2 = (HashMap)(arg0[0].get("hl2"));
			if (h1.get("indicator").equals("A") && 
					h2.get("indicator").equals("B"))
			{
			log.info("SELL");
			new TradeSignal(evt.getSymbol(),evt.getOpen(),evt.getHigh(),
					evt.getLow(),evt.getClose(),"SELL","sma",
					String.valueOf(evt.getTimestamp())).enqueue();
			}
			if (h1.get("indicator").equals("B") && 
					h2.get("indicator").equals("A"))
			{		
				log.info("BUY");
				new TradeSignal(evt.getSymbol(),evt.getOpen(),evt.getHigh(),
						evt.getLow(),evt.getClose(),"BUY","sma",
						String.valueOf(evt.getTimestamp())).enqueue();
					
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
