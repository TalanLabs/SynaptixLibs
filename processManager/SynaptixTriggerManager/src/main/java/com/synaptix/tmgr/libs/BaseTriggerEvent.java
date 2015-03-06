package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEvent;

public class BaseTriggerEvent implements TriggerEvent {
	Object attchment;
	long tt;
	Trigger source;
	public BaseTriggerEvent(Trigger source, Object attachment){
		tt=System.currentTimeMillis();
		this.attchment = attachment;
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
	public Trigger getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEvent#getAttachment()
	 */
	public Object getAttachment() {
		return attchment;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "TRIGGER_EVEN (from "+getSource().getID()+") "+((attchment!=null)?attchment.toString():"no attachment");
	}

}
