package vadi.test.sarb.event;

import vadi.test.sarb.esper.util.Utility;

public abstract class Event {
	public void enqueue(){
		Utility.getInstance().getEpService().getEPRuntime().sendEvent(this);
	}

}
