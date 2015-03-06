package com.synaptix.core.controller;

import javax.swing.event.EventListenerList;

import com.synaptix.core.event.CoreConfigurationListener;

public abstract class AbstractCoreConfiguration implements
		CoreConfiguration {

	private EventListenerList listenerList;

	public AbstractCoreConfiguration() {
		listenerList = new EventListenerList();
	}

	public void addCoreConfigurationListener(CoreConfigurationListener l) {
		listenerList.add(CoreConfigurationListener.class, l);
	}

	public void removeCoreConfigurationListener(CoreConfigurationListener l) {
		listenerList.remove(CoreConfigurationListener.class, l);
	}

	protected void fireTitleChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CoreConfigurationListener.class) {
				((CoreConfigurationListener) listeners[i + 1]).titleChanged();
			}
		}
	}
}
