package com.synaptix.swing.roulement;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.roulement.event.RoulementTimelineModelEvent;
import com.synaptix.swing.roulement.event.RoulementTimelineModelListener;

public abstract class AbstractRoulementTimelineModel implements
		RoulementTimelineModel {

	protected EventListenerList listenerList;

	public AbstractRoulementTimelineModel() {
		listenerList = new EventListenerList();
	}

	public void addRoulementTimelineModelListener(
			RoulementTimelineModelListener l) {
		listenerList.add(RoulementTimelineModelListener.class, l);
	}

	public void removeRoulementTimelineModelListener(
			RoulementTimelineModelListener l) {
		listenerList.remove(RoulementTimelineModelListener.class, l);
	}

	public RoulementTimelineModelListener[] getRoulementTimelineDataListeners() {
		return (RoulementTimelineModelListener[]) listenerList
				.getListeners(RoulementTimelineModelListener.class);
	}

	protected void fireDataResourceChanged(int resource) {
		RoulementTimelineModelListener[] listeners = listenerList
				.getListeners(RoulementTimelineModelListener.class);
		RoulementTimelineModelEvent e = new RoulementTimelineModelEvent(this,
				RoulementTimelineModelEvent.Type.DATA_RESOURCE_CHANGED,
				resource);

		for (RoulementTimelineModelListener listener : listeners) {
			listener.roulementTimelineChanged(e);
		}
	}

	protected void fireHeaderResourceChanged(int resource) {
		RoulementTimelineModelListener[] listeners = listenerList
				.getListeners(RoulementTimelineModelListener.class);
		RoulementTimelineModelEvent e = new RoulementTimelineModelEvent(this,
				RoulementTimelineModelEvent.Type.HEADER_RESOURCE_CHANGED,
				resource);

		for (RoulementTimelineModelListener listener : listeners) {
			listener.roulementTimelineChanged(e);
		}
	}

	protected void fireDataChanged() {
		RoulementTimelineModelListener[] listeners = listenerList
				.getListeners(RoulementTimelineModelListener.class);
		RoulementTimelineModelEvent e = new RoulementTimelineModelEvent(this,
				RoulementTimelineModelEvent.Type.DATA_CHANGED);

		for (RoulementTimelineModelListener listener : listeners) {
			listener.roulementTimelineChanged(e);
		}
	}

	protected void fireStructureChanged() {
		RoulementTimelineModelListener[] listeners = listenerList
				.getListeners(RoulementTimelineModelListener.class);
		RoulementTimelineModelEvent e = new RoulementTimelineModelEvent(this,
				RoulementTimelineModelEvent.Type.STRUCTURE_CHANGED);

		for (RoulementTimelineModelListener listener : listeners) {
			listener.roulementTimelineChanged(e);
		}
	}
}
