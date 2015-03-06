package com.synaptix.tmgr.libs;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.tmgr.apis.TriggerEvent;
import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.toolkits.thread.Spoolable;

public class SpoolableTriggerEventListener implements TriggerEventListener {
	List<Spoolable> bags;
	
	public SpoolableTriggerEventListener(){
		bags = new ArrayList<Spoolable>();
	}
	public void addBag(Spoolable bag){
		bags.add(bag);
	}
	public void removeBag(Spoolable bag){
		bags.remove(bag);
	}
	
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEventListener#notifyEvent(com.synaptix.tmgr.apis.TriggerEvent)
	 */
	public void notifyEvent(TriggerEvent event) {
		for (int i=0; i<bags.size(); i++){
			Spoolable teb = (Spoolable) bags.get(i);
			teb.push(event);
		}

	}

}
