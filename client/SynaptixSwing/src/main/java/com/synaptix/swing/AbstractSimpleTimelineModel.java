package com.synaptix.swing;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.SimpleTimelineModelEvent;
import com.synaptix.swing.event.SimpleTimelineModelListener;

public abstract class AbstractSimpleTimelineModel implements
		SimpleTimelineModel {

	protected EventListenerList listenerList;

	public AbstractSimpleTimelineModel() {
		listenerList = new EventListenerList();
	}

	public void addSimpleTimelineModelListener(SimpleTimelineModelListener l) {
		listenerList.add(SimpleTimelineModelListener.class, l);
	}

	public void removeSimpleTimelineModelListener(SimpleTimelineModelListener l) {
		listenerList.remove(SimpleTimelineModelListener.class, l);
	}

	public SimpleTimelineModelListener[] getSimpleTimelineDataListeners() {
		return (SimpleTimelineModelListener[]) listenerList
				.getListeners(SimpleTimelineModelListener.class);
	}

	protected void fireDataResourceChanged(int resource) {
		SimpleTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleTimelineModelListener.class);
		SimpleTimelineModelEvent e = new SimpleTimelineModelEvent(this,
				SimpleTimelineModelEvent.Type.DATA_RESOURCE_CHANGED, resource);

		for (SimpleTimelineModelListener listener : listeners) {
			listener.simpleTimelineChanged(e);
		}
	}

	protected void fireHeaderResourceChanged(int resource) {
		SimpleTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleTimelineModelListener.class);
		SimpleTimelineModelEvent e = new SimpleTimelineModelEvent(this,
				SimpleTimelineModelEvent.Type.HEADER_RESOURCE_CHANGED, resource);

		for (SimpleTimelineModelListener listener : listeners) {
			listener.simpleTimelineChanged(e);
		}
	}

	protected void fireDataChanged() {
		SimpleTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleTimelineModelListener.class);
		SimpleTimelineModelEvent e = new SimpleTimelineModelEvent(this,
				SimpleTimelineModelEvent.Type.DATA_CHANGED);

		for (SimpleTimelineModelListener listener : listeners) {
			listener.simpleTimelineChanged(e);
		}
	}

	protected void fireStructureChanged() {
		SimpleTimelineModelListener[] listeners = listenerList
				.getListeners(SimpleTimelineModelListener.class);
		SimpleTimelineModelEvent e = new SimpleTimelineModelEvent(this,
				SimpleTimelineModelEvent.Type.STRUCTURE_CHANGED);

		for (SimpleTimelineModelListener listener : listeners) {
			listener.simpleTimelineChanged(e);
		}
	}
}
