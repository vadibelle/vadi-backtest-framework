package vadi.test.sarb.listeners;

import vadi.test.sarb.esper.portfolio.PFManager;
import vadi.test.sarb.event.LoadPortfolio;
import vadi.test.sarb.event.TradeSignal;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class PositionLoader implements UpdateListener {

	PFManager pfm = PFManager.getInstance();
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub
		try {
		
		LoadPortfolio sig = (LoadPortfolio)(arg0[0].getUnderlying());
		pfm.setCash(sig.getCash());
		pfm.loadPositions();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
