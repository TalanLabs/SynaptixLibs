package com.synaptix.tmgr.libs.tasks;

import com.synaptix.tmgr.libs.ThreadedTrigger;
import com.synaptix.tmgr.libs.ThreadedTriggerTask;

public abstract class AbstractTriggerTask implements ThreadedTriggerTask {
	private ThreadedTrigger trigger;

	/**
	 * 
	 */
	public AbstractTriggerTask() {
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.libs.ThreadedTriggerTask#setTrigger(com.synaptix.tmgr.libs.ThreadedTrigger)
	 */
	public void setTrigger(ThreadedTrigger trigger) {
		this.trigger = trigger;
	}

	public ThreadedTrigger getTrigger(){
		return trigger;
	}
}
