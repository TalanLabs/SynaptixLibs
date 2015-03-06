package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerManager;

public class TriggerInstallEvent extends BaseTriggerManagerEvent {
	Trigger trigger;
	boolean isInstall;
	/**
	 * @param source
	 */
	public TriggerInstallEvent(TriggerManager source,Trigger trigger, boolean isInstall) {
		super(source);
		this.trigger = trigger;
		this.isInstall = isInstall;
	}
	public Trigger getTrigger(){
		return trigger;
	}
	public boolean isInstall(){
		return isInstall;
	}
	public String toString(){
		return "Trigger '"+trigger.toString()+"' "+(isInstall?"Installed":"Unistalled");
	}
}
