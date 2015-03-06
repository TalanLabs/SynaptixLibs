package com.synaptix.tmgr.apis;

public interface Trigger {
	public String getID();
	public boolean isActive();
	public void activate(TriggerEventListener telistener);
	public void deactivate();
}
