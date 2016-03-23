package com.synaptix.tmgr.apis;

public interface Trigger {
	String getID();
	boolean isActive();
	void activate(TriggerEventListener telistener);
	void deactivate();
}
