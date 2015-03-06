package com.synaptix.component.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.component.IPropertyChangeCapable;

public abstract class PropertyChangeCapableImpl implements IPropertyChangeCapable {

	protected transient final List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();

	protected transient final Map<String, List<PropertyChangeListener>> propertyChangeListenerMap = new HashMap<String, List<PropertyChangeListener>>();

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.add(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.remove(l);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		List<PropertyChangeListener> ls = propertyChangeListenerMap.get(propertyName);
		if (ls == null) {
			ls = new ArrayList<PropertyChangeListener>();
			propertyChangeListenerMap.put(propertyName, ls);
		}
		ls.add(l);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		List<PropertyChangeListener> ls = propertyChangeListenerMap.get(propertyName);
		if (ls != null) {
			ls.remove(l);
			if (ls.isEmpty()) {
				propertyChangeListenerMap.remove(propertyName);
			}
		}
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for (PropertyChangeListener l : propertyChangeListeners) {
			l.propertyChange(evt);
		}
		List<PropertyChangeListener> ls = propertyChangeListenerMap.get(propertyName);
		if (ls != null) {
			for (PropertyChangeListener l : ls) {
				l.propertyChange(evt);
			}
		}
	}
}
