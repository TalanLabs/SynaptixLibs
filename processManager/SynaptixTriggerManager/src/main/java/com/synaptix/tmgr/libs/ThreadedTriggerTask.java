package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.TriggerEventListener;


public interface ThreadedTriggerTask {
	public void execute(TriggerEventListener lstnr);
	public void setTrigger(ThreadedTrigger trigger);
	public void clean();
}