package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEventListener;

public abstract class AbstractTrigger implements Trigger {
	private boolean isActive;
	private String id;
	
	public AbstractTrigger(String id){
		isActive=false;
		this.id=id;	
	}

	public String getID(){
		return id;
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.Trigger#activate(com.synaptix.tmgr.apis.TriggerEventListener)
	 */
	public synchronized final void activate(TriggerEventListener telistener) {
		if (!isActive){
			isActive=true;
			doActivate( telistener);
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.Trigger#deactivate()
	 */
	public synchronized final void deactivate() {
		if (isActive){
			isActive=false;
			doDeactivate();
		}
	}
	
	public abstract void doDeactivate();
	public abstract void doActivate(TriggerEventListener telistener);

	public boolean isActive(){
		return isActive;
	}
}
