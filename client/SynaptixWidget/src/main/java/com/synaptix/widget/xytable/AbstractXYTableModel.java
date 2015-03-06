package com.synaptix.widget.xytable;

import javax.swing.event.EventListenerList;

public abstract class AbstractXYTableModel implements XYTableModel {

	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public void addXYTableModelListener(XYTableModelListener l) {
		listenerList.add(XYTableModelListener.class, l);
	}

	@Override
	public void removeXYTableModelListener(XYTableModelListener l) {
		listenerList.remove(XYTableModelListener.class, l);
	}

	public XYTableModelListener[] getXYTableModelListeners() {
		return (XYTableModelListener[]) listenerList.getListeners(XYTableModelListener.class);
	}

	public void fireXYTableDataChanged() {
		fireXYTableChanged(new XYTableModelEvent(this));
	}

	public void fireXYTableChanged(XYTableModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == XYTableModelListener.class) {
				((XYTableModelListener) listeners[i + 1]).xyTableChanged(e);
			}
		}
	}

	@Override
	public Object getColumnValue(int column) {
		return null;
	}

	@Override
	public String getRowName(int row) {
		return null;
	}

	@Override
	public Object getRowValue(int row) {
		return null;
	}
}
