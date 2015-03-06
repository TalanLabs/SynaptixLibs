package com.synaptix.swing;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.TriageModelEvent;
import com.synaptix.swing.event.TriageModelListener;

public abstract class AbstractTriageModel implements TriageModel {

	protected EventListenerList listenerList = new EventListenerList();

	public void addTriageModelListener(TriageModelListener listener) {
		listenerList.add(TriageModelListener.class, listener);
	}

	public void removeTriageModelListener(TriageModelListener listener) {
		listenerList.remove(TriageModelListener.class, listener);
	}

	protected void fireDataChanged() {
		fireDataChanged(new TriageModelEvent(this));
	}

	protected void fireDataChanged(TriageModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TriageModelListener.class) {
				((TriageModelListener) listeners[i + 1]).triageChanged(e);
			}
		}
	}
}
