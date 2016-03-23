package com.synaptix.tmgr;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEvent;
import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.tmgr.apis.TriggerManager;
import com.synaptix.tmgr.apis.TriggerManagerEvent;
import com.synaptix.tmgr.apis.TriggerManagerEventListener;
import com.synaptix.tmgr.libs.SimpleTriggerManager;
import com.synaptix.tmgr.libs.SpoolableTriggerEventListener;
import com.synaptix.toolkits.thread.Spoolable;

public class TriggerEngine implements TriggerManager{
	private static TriggerEngine instance;
	private TriggerManager manager;
	TriggerEventListenerBroker triggerEventListenerBroker;
	public synchronized static TriggerEngine getInstance(){
		if (instance == null){
			instance = new TriggerEngine();
		} 
		return instance;
	}
	
	private TriggerEngine(){
		triggerEventListenerBroker = new TriggerEventListenerBroker();
		
		this.manager = new SimpleTriggerManager(new TriggerManagerEventListener() {
			public void notifyEvent(TriggerManagerEvent event) {
			}
		},triggerEventListenerBroker);
	}
	
	public void addBag(Spoolable bag){
		triggerEventListenerBroker.addBag(bag);
	}

	public void addListener(TriggerEventListener listener){
		triggerEventListenerBroker.addListener(listener);
	}

	public void removeBag(Spoolable bag){
		triggerEventListenerBroker.removeBag(bag);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#activateTrigger(java.lang.String)
	 */
	public void activateTrigger(String id) {
		manager.activateTrigger(id);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#deactivateTrigger(java.lang.String)
	 */
	public void deactivateTrigger(String id) {
		manager.deactivateTrigger(id);

	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getActiveTriggers()
	 */
	public List<Trigger> getActiveTriggers() {
		return manager.getActiveTriggers();
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getInactiveTriggers()
	 */
	public List<Trigger> getInactiveTriggers() {
		return manager.getInactiveTriggers();
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#getTrigger(java.lang.String)
	 */
	public Trigger getTrigger(String id) {
		return manager.getTrigger(id);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#installTrigger(com.synaptix.tmgr.apis.Trigger, boolean)
	 */
	public void installTrigger(Trigger trigger, boolean auto_activate) {
		manager.installTrigger(trigger,auto_activate);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerEventListener#notifyEvent(com.synaptix.tmgr.apis.TriggerEvent)
	 */
	public void notifyEvent(TriggerEvent event) {
		manager.notifyEvent(event);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#setTriggerManagerEventListener(com.synaptix.tmgr.apis.TriggerManagerEventListener)
	 */
	public void setTriggerManagerEventListener(TriggerManagerEventListener lstnr) {
		manager.setTriggerManagerEventListener(lstnr);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#setTriggerEventListener(com.synaptix.tmgr.apis.TriggerEventListener)
	 */
	public void setTriggerEventListener(TriggerEventListener lstnr) {
		manager.setTriggerEventListener(lstnr);
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#uninstallTrigger(java.lang.String)
	 */
	public void uninstallTrigger(String id) {
		manager.uninstallTrigger(id);
	}
	
	static class TriggerEventListenerBroker implements TriggerEventListener {
		SpoolableTriggerEventListener bagsListener;
		List<TriggerEventListener> classicListeners;
		public TriggerEventListenerBroker(){
			bagsListener = new SpoolableTriggerEventListener();
			classicListeners = new ArrayList<TriggerEventListener>();
		}
		/* (non-Javadoc)
		 * @see com.synaptix.tmgr.apis.TriggerEventListener#notifyEvent(com.synaptix.tmgr.apis.TriggerEvent)
		 */
		public void notifyEvent(TriggerEvent event) {
			bagsListener.notifyEvent(event);
			for (int i=0; i<classicListeners.size(); i++){
				classicListeners.get(i).notifyEvent(event);
			}
		}
		
		public void addBag(Spoolable bag){
			bagsListener.addBag(bag);
		}
		public void removeBag(Spoolable bag){
			bagsListener.removeBag(bag);
		}

		public void addListener(TriggerEventListener tel){
			classicListeners.add(tel);
		}
		public void removeListener(TriggerEventListener tel){
			classicListeners.remove(tel);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.apis.TriggerManager#shutdown()
	 */
	public void shutdown() {
		manager.shutdown();
	}
}
