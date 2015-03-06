package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.TriggerManager;
import com.synaptix.tmgr.apis.TriggerManagerEvent;

public class BaseTriggerManagerEvent implements TriggerManagerEvent {
	long tt;
	TriggerManager source;
	public BaseTriggerManagerEvent(TriggerManager source){
		tt=System.currentTimeMillis();
		this.source = source;
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEvent#getTimeStamp()
	 */
	public long getTimeStamp() {
		return tt;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEvent#getSource()
	 */
	public TriggerManager getSource() {
		return source;
	}


}
