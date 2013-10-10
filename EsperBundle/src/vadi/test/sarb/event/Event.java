package vadi.test.sarb.event;

import vadi.test.sarb.esper.util.Utility;

public abstract class Event {
	
	public synchronized void enqueue(){
		Utility.getInstance().getEpService().getEPRuntime().sendEvent(this);
	}
	public void route()
	{
		Utility.getInstance().getEpService().getEPRuntime().route(this);
	}

}
