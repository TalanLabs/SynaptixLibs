package com.synaptix.swing.path;

import javax.swing.event.EventListenerList;

public abstract class AbstractPathModel implements PathModel {

	protected final EventListenerList listenerList;

	public AbstractPathModel() {
		listenerList = new EventListenerList();
	}

	@Override
	public String getLineName(int index1, int index2) {
		return null;
	}

	@Override
	public boolean isSelectedNode(int index) {
		return true;
	}

	@Override
	public boolean isSelectedLine(int index1, int index2) {
		return index1 + 1 == index2;
	}

	@Override
	public void addPathModelListener(PathModelListener l) {
		listenerList.add(PathModelListener.class, l);
	}

	@Override
	public void removePathModelListener(PathModelListener l) {
		listenerList.remove(PathModelListener.class, l);
	}

	public PathModelListener[] getPathModelListeners() {
		return listenerList.getListeners(PathModelListener.class);
	}

	protected void firePathChanged() {
		PathModelListener[] listeners = listenerList.getListeners(PathModelListener.class);
		PathModelEvent e = new PathModelEvent(this);

		for (PathModelListener listener : listeners) {
			listener.pathModelChanged(e);
		}
	}
}
