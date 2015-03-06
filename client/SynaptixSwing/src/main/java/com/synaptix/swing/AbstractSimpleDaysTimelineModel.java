package com.synaptix.swing;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.SimpleDaysTimelineModelEvent;
import com.synaptix.swing.event.SimpleDaysTimelineModelListener;

public abstract class AbstractSimpleDaysTimelineModel implements
		SimpleDaysTimelineModel {

	protected EventListenerList listenerList;

	public AbstractSimpleDaysTimelineModel() {
		listenerList = new EventListenerList();
	}

	public void addSimpleDaysTimelineModelListener(
			SimpleDaysTimelineModelListener l) {
		listenerList.add(SimpleDaysTimelineModelListener.class, l);
	}

	public void removeSimpleDaysTimelineModelListener(
			SimpleDaysTimelineModelListener l) {
		listenerList.remove(SimpleDaysTimelineModelListener.class, l);
	}

	public SimpleDaysTimelineModelListener[] getSimpleDaysTimelineDataListeners() {
		return (SimpleDaysTimelineModelListener[]) listenerList
				.getListeners(SimpleDaysTimelineModelListener.class);
	}

	protected void fireDataResourceChanged(int resource) {
		SimpleDaysTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineModelListener.class);
		SimpleDaysTimelineModelEvent e = new SimpleDaysTimelineModelEvent(this,
				SimpleDaysTimelineModelEvent.Type.DATA_RESOURCE_CHANGED,
				resource);

		for (SimpleDaysTimelineModelListener listener : listeners) {
			listener.simpleDaysTimelineChanged(e);
		}
	}

	protected void fireHeaderResourceChanged(int resource) {
		SimpleDaysTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineModelListener.class);
		SimpleDaysTimelineModelEvent e = new SimpleDaysTimelineModelEvent(this,
				SimpleDaysTimelineModelEvent.Type.HEADER_RESOURCE_CHANGED,
				resource);

		for (SimpleDaysTimelineModelListener listener : listeners) {
			listener.simpleDaysTimelineChanged(e);
		}
	}

	protected void fireDataChanged() {
		SimpleDaysTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineModelListener.class);
		SimpleDaysTimelineModelEvent e = new SimpleDaysTimelineModelEvent(this,
				SimpleDaysTimelineModelEvent.Type.DATA_CHANGED);

		for (SimpleDaysTimelineModelListener listener : listeners) {
			listener.simpleDaysTimelineChanged(e);
		}
	}

	protected void fireStructureChanged() {
		SimpleDaysTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineModelListener.class);
		SimpleDaysTimelineModelEvent e = new SimpleDaysTimelineModelEvent(this,
				SimpleDaysTimelineModelEvent.Type.STRUCTURE_CHANGED);

		for (SimpleDaysTimelineModelListener listener : listeners) {
			listener.simpleDaysTimelineChanged(e);
		}
	}
}
