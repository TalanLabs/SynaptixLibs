package com.synaptix.core.dock;

import javax.swing.Icon;
import javax.swing.event.EventListenerList;

import com.synaptix.client.view.io.StatsInput;
import com.synaptix.client.view.io.StatsOutput;
import com.synaptix.core.event.ViewDockableEvent;
import com.synaptix.core.event.ViewDockableListener;
import com.synaptix.core.event.ViewDockableStateEvent;

public abstract class AbstractViewDockable implements IViewDockable {

	protected EventListenerList listenerList;

	public AbstractViewDockable() {
		listenerList = new EventListenerList();
	}

	public String getCategorie() {
		return "Inconnue";
	}

	public Icon getIcon() {
		return null;
	}

	public void viewDockableStateChanged(ViewDockableStateEvent e) {
	}

	public void readStats(StatsInput in) {
	}

	public void writeStats(StatsOutput out) {
	}

	public void addViewDockableListener(ViewDockableListener l) {
		listenerList.add(ViewDockableListener.class, l);
	}

	public void removeViewDockableListener(ViewDockableListener l) {
		listenerList.remove(ViewDockableListener.class, l);
	}

	protected void fireHeaderChanged() {
		ViewDockableListener[] listeners = listenerList
				.getListeners(ViewDockableListener.class);
		ViewDockableEvent e = new ViewDockableEvent(this,
				ViewDockableEvent.Type.HEADER_CHANGED);

		for (ViewDockableListener listener : listeners) {
			listener.viewDockableChanged(e);
		}
	}
}
