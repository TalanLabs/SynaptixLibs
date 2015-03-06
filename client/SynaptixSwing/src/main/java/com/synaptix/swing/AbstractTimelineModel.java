package com.synaptix.swing;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.TimelineModelEvent;
import com.synaptix.swing.event.TimelineModelListener;

public abstract class AbstractTimelineModel implements TimelineModel {

	protected EventListenerList listenerList;
	
	public AbstractTimelineModel() {
		listenerList = new EventListenerList();
	}
	
	public void addTimelineModelListener(TimelineModelListener l) {
		listenerList.add(TimelineModelListener.class, l);
	}

	public void removeTimelineModelListener(TimelineModelListener l) {
		listenerList.remove(TimelineModelListener.class, l);
	}

	public TimelineModelListener[] getTimelineDataListeners() {
		return (TimelineModelListener[]) listenerList
				.getListeners(TimelineModelListener.class);
	}

	protected void fireModifyRessource(int ressource) {
		TimelineModelListener[] listeners = listenerList.getListeners(TimelineModelListener.class);
		TimelineModelEvent e = new TimelineModelEvent(this,TimelineModelEvent.Type.MODIFY,ressource);

		for(TimelineModelListener listener : listeners) {
			listener.timelineChanged(e);
		}
	}

	protected void fireHeaderRessourceChanged(int ressource) {
		TimelineModelListener[] listeners = listenerList.getListeners(TimelineModelListener.class);
		TimelineModelEvent e = new TimelineModelEvent(this,TimelineModelEvent.Type.HEADER_RESSOURCES,ressource);

		for(TimelineModelListener listener : listeners) {
			listener.timelineChanged(e);
		}
	}
	
	protected void fireHeaderRessourcesChanged() {
		TimelineModelListener[] listeners = listenerList.getListeners(TimelineModelListener.class);
		TimelineModelEvent e = new TimelineModelEvent(this,TimelineModelEvent.Type.HEADER_RESSOURCES,-1);

		for(TimelineModelListener listener : listeners) {
			listener.timelineChanged(e);
		}
	}
	
	protected void fireContentsChanged() {
		TimelineModelListener[] listeners = listenerList.getListeners(TimelineModelListener.class);
		TimelineModelEvent e = new TimelineModelEvent(this,TimelineModelEvent.Type.CONTENTS_CHANGED);

		for(TimelineModelListener listener : listeners) {
			listener.timelineChanged(e);
		}
	}
}
