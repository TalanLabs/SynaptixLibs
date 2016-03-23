package com.synaptix.tmgr.libs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEvent;
import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.tmgr.apis.TriggerManager;
import com.synaptix.tmgr.apis.TriggerManagerEventListener;

public class SimpleTriggerManager implements TriggerManager {

	private Map<String, Trigger> triggers;
	private TriggerEventListener teListener;
	private TriggerManagerEventListener tmeListener;

	public SimpleTriggerManager(TriggerManagerEventListener tmeListener, TriggerEventListener lstnr) {
		this.tmeListener = tmeListener;
		this.teListener = lstnr;
		this.triggers = new HashMap<String, Trigger>();
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#installTrigger(com.synaptix.tmgr.apis.Trigger, boolean)
	 */
	public void installTrigger(Trigger trigger, boolean auto_activate) {
		triggers.put(trigger.getID(), trigger);
		if (auto_activate) {
			trigger.activate(this);
		}
		tmeListener.notifyEvent(new TriggerInstallEvent(this, trigger, true));
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#uninstallTrigger(java.lang.String)
	 */
	public void uninstallTrigger(String id) {
		Trigger trigger = getTrigger(id);
		if (trigger != null) {
			trigger.deactivate();
			triggers.remove(id);
			tmeListener.notifyEvent(new TriggerInstallEvent(this, trigger, false));
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#activateTrigger(java.lang.String)
	 */
	public void activateTrigger(String id) {
		Trigger trigger = getTrigger(id);
		if (trigger != null) {
			trigger.activate(this);
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#deactivateTrigger(java.lang.String)
	 */
	public void deactivateTrigger(String id) {
		Trigger trigger = getTrigger(id);
		if (trigger != null) {
			trigger.deactivate();
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getTrigger(java.lang.String)
	 */
	public Trigger getTrigger(String id) {
		return triggers.get(id);
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getActiveTriggers()
	 */
	public List<Trigger> getActiveTriggers() {
		List<Trigger> lst = new ArrayList<Trigger>();
		Iterator<Trigger> it = triggers.values().iterator();
		while (it.hasNext()) {
			Trigger t = it.next();
			if (t.isActive()) {
				lst.add(t);
			}
		}
		return lst;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getInactiveTriggers()
	 */
	public List<Trigger> getInactiveTriggers() {
		List<Trigger> lst = new ArrayList<Trigger>();
		Iterator<Trigger> it = triggers.values().iterator();
		while (it.hasNext()) {
			Trigger t = it.next();
			if (!t.isActive()) {
				lst.add(t);
			}
		}
		return lst;
	}

	public void setTriggerEventListener(TriggerEventListener lst) {
		teListener = lst;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEventListener#notifyEvent(com.synaptix.tmgr.apis.TriggerEvent)
	 */
	public void notifyEvent(TriggerEvent event) {
		teListener.notifyEvent(event);
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#setTriggerManagerEventListener(com.synaptix.tmgr.apis.TriggerManagerEventListener)
	 */
	public void setTriggerManagerEventListener(TriggerManagerEventListener lstnr) {
		tmeListener = lstnr;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#shutdown()
	 */
	public void shutdown() {
		synchronized (triggers) {
			Iterator<Trigger> it = triggers.values().iterator();
			while (it.hasNext()) {
				Trigger t = it.next();
				t.deactivate();
			}
		}
	}
}
