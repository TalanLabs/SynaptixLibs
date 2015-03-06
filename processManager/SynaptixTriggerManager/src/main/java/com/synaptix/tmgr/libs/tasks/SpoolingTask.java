package com.synaptix.tmgr.libs.tasks;

import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.tmgr.libs.BaseTriggerEvent;
import com.synaptix.toolkits.thread.Spoolable;

public class SpoolingTask extends AbstractTriggerTask {
	private Spoolable spoolable;
	/**
	 * 
	 */
	public SpoolingTask(Spoolable spoolable) {
		super();
		this.spoolable = spoolable;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.libs.ThreadedTriggerTask#execute(com.synaptix.tmgr.apis.TriggerEventListener)
	 */
	public void execute(TriggerEventListener lstnr) {
		try {
			synchronized (spoolable) {
				spoolable.wait(1000);
				Object msg = null;
				while ((msg = spoolable.pop()) != null) {
					lstnr.notifyEvent(new BaseTriggerEvent(getTrigger(), msg));
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace(); // TODO: handle exception
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.libs.ThreadedTriggerTask#clean()
	 */
	public void clean() {
	}

}
