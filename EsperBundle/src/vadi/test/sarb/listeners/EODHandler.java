package vadi.test.sarb.listeners;


import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.EODQuote;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EODHandler implements UpdateListener  {
	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("vadi.sarb");
	volatile int count=0;
	PFManager pfm = PFManager.getInstance();
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		//log.info("EOD Received count="+(count++)+" "+arg0[0].getUnderlying().toString());
		pfm.updateLastPrice((EODQuote)arg0[0].getUnderlying());
						
	}

}
