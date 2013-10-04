package vadi.test.sarb.esper.util;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPException;
import com.espertech.esper.client.EPServiceDestroyedException;
import com.espertech.esper.client.time.CurrentTimeEvent;

public class SendTimeEvents implements Runnable {
	final long init;
	long ctime = 0;
	public SendTimeEvents()
	{
		init = System.currentTimeMillis();
		ctime = init;
						
		Utility.getInstance().getEpService()
	    	   .getEPRuntime().sendEvent(new CurrentTimeEvent(0));
	    	  
	   }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true ) {
		try {
			ctime = System.currentTimeMillis();
			CurrentTimeEvent timeEvent = new CurrentTimeEvent(ctime-init);
			Utility.getInstance().getEpService().getEPRuntime().sendEvent(timeEvent);
			Thread.sleep(100);
		} catch (EPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EPServiceDestroyedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		

	}

}
