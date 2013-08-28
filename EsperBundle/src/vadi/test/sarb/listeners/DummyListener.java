package vadi.test.sarb.listeners;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class DummyListener implements UpdateListener {

	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		// TODO Auto-generated method stub

		System.out.println("Event recived "+arg0[0].getUnderlying());
	}

}
