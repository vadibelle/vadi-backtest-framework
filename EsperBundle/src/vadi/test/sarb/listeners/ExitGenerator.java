package vadi.test.sarb.listeners;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.esper.util.Utility;
import vadi.test.sarb.event.EODQuote;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class ExitGenerator implements UpdateListener {

	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");
	volatile int count=0;
	PFManager pfm = PFManager.getInstance();
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
				// TODO Auto-generated method stub
			//log.info("EOD Received count="+(count++)+" "+arg0[0].getUnderlying().toString());
		EODQuote q = (EODQuote)arg0[0].getUnderlying();
		if ( q.getTimestamp() < Utility.getInstance().getCurrentTime() )
		{
			Utility.getInstance().info("Old event ignoring "+q.toString());
			return;
		}
			pfm.generateExits(q);
					
		}

	}


