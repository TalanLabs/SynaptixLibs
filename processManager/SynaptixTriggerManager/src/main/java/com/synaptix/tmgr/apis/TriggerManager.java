package com.synaptix.tmgr.apis;

import java.util.List;

public interface TriggerManager extends TriggerEventListener {
	public void installTrigger(Trigger trigger, boolean auto_activate);
	public void uninstallTrigger(String id);

	public void activateTrigger(String id);
	public void deactivateTrigger(String id);
	
	public Trigger getTrigger(String id);
	public List<Trigger> getActiveTriggers();
	public List<Trigger> getInactiveTriggers();
	
	public void setTriggerManagerEventListener(TriggerManagerEventListener lstnr);
	public void setTriggerEventListener(TriggerEventListener lstnr);
	
	public void shutdown();
}
