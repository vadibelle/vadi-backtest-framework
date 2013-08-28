package vadi.test.sarb.event;

import java.util.concurrent.ConcurrentHashMap;

public interface EventHandler {
	
	public void handle(ConcurrentHashMap<String,Object> state);

	
}
