package com.synaptix.tmgr.apis;

import java.util.List;

public interface TriggerManager extends TriggerEventListener {
	void installTrigger(Trigger trigger, boolean auto_activate);
	void uninstallTrigger(String id);

	void activateTrigger(String id);
	void deactivateTrigger(String id);
	
	Trigger getTrigger(String id);
	List<Trigger> getActiveTriggers();
	List<Trigger> getInactiveTriggers();
	
	void setTriggerManagerEventListener(TriggerManagerEventListener lstnr);
	void setTriggerEventListener(TriggerEventListener lstnr);
	
	void shutdown();
}
