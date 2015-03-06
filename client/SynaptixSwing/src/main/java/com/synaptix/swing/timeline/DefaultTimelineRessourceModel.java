package com.synaptix.swing.timeline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.synaptix.swing.event.TimelineRessourceModelEvent;
import com.synaptix.swing.event.TimelineRessourceModelListener;

public class DefaultTimelineRessourceModel implements TimelineRessourceModel,
		PropertyChangeListener, ListSelectionListener, Serializable {

	private static final long serialVersionUID = 4139411379986126705L;
	
	protected Vector<TimelineRessource> timelineRessources;

	protected ListSelectionModel selectionModel;

	protected int ressourceMargin;

	protected EventListenerList listenerList = new EventListenerList();

	transient protected ChangeEvent changeEvent = null;

	protected boolean ressourceSelectionAllowed;

	protected int totalRessourceHeight;

	public DefaultTimelineRessourceModel() {
		super();

		timelineRessources = new Vector<TimelineRessource>();
		setSelectionModel(createSelectionModel());
		setRessourceMargin(1);
		invalidateWidthCache();
		setRessourceSelectionAllowed(false);
	}

	public void addRessource(TimelineRessource aRessource) {
		if (aRessource == null) {
			throw new IllegalArgumentException("Object is null"); //$NON-NLS-1$
		}

		timelineRessources.addElement(aRessource);
		aRessource.addPropertyChangeListener(this);
		invalidateWidthCache();

		fireRessourceAdded(new TimelineRessourceModelEvent(this, 0,
				getRessourceCount() - 1));
	}

	public void removeRessource(TimelineRessource ressource) {
		int ressourceIndex = timelineRessources.indexOf(ressource);

		if (ressourceIndex != -1) {
			if (selectionModel != null) {
				selectionModel.removeIndexInterval(ressourceIndex,
						ressourceIndex);
			}

			ressource.removePropertyChangeListener(this);
			timelineRessources.removeElementAt(ressourceIndex);
			invalidateWidthCache();

			fireRessourceRemoved(new TimelineRessourceModelEvent(this,
					ressourceIndex, 0));
		}
	}

	public void moveRessource(int ressourceIndex, int newIndex) {
		if ((ressourceIndex < 0) || (ressourceIndex >= getRessourceCount())
				|| (newIndex < 0) || (newIndex >= getRessourceCount()))
			throw new IllegalArgumentException(
					"moveRessource() - Index out of range"); //$NON-NLS-1$

		TimelineRessource aRessource;

		if (ressourceIndex == newIndex) {
			fireRessourceMoved(new TimelineRessourceModelEvent(this,
					ressourceIndex, newIndex));
			return;
		}
		aRessource = timelineRessources.elementAt(ressourceIndex);

		timelineRessources.removeElementAt(ressourceIndex);
		boolean selected = selectionModel.isSelectedIndex(ressourceIndex);
		selectionModel.removeIndexInterval(ressourceIndex, ressourceIndex);

		timelineRessources.insertElementAt(aRessource, newIndex);
		selectionModel.insertIndexInterval(newIndex, 1, true);
		if (selected) {
			selectionModel.addSelectionInterval(newIndex, newIndex);
		} else {
			selectionModel.removeSelectionInterval(newIndex, newIndex);
		}

		fireRessourceMoved(new TimelineRessourceModelEvent(this,
				ressourceIndex, newIndex));
	}

	public void setRessourceMargin(int newMargin) {
		if (newMargin != ressourceMargin) {
			ressourceMargin = newMargin;
			fireRessourceMarginChanged();
		}
	}

	public int getRessourceCount() {
		return timelineRessources.size();
	}

	public Enumeration<TimelineRessource> getRessources() {
		return timelineRessources.elements();
	}

	public int getRessourceIndex(Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier is null"); //$NON-NLS-1$
		}

		Enumeration<TimelineRessource> enumeration = getRessources();
		TimelineRessource aRessource;
		int index = 0;

		while (enumeration.hasMoreElements()) {
			aRessource = enumeration.nextElement();
			if (identifier.equals(aRessource.getIdentifier()))
				return index;
			index++;
		}
		throw new IllegalArgumentException("Identifier not found"); //$NON-NLS-1$
	}

	public TimelineRessource getRessource(int ressourceIndex) {
		return timelineRessources.elementAt(ressourceIndex);
	}

	public int getDefaultHeight() {
		return TimelineRessource.DEFAULT_HEIGHT;
	}

	public int getHeight(int ressourceIndex) {
		return getRessource(ressourceIndex).getHeight();
	}

	public int getRessourceMargin() {
		return ressourceMargin;
	}

	public int getRessourceIndexAtY(int y) {
		if (y < 0) {
			return -1;
		}
		int cc = getRessourceCount();
		for (int ressource = 0; ressource < cc; ressource++) {
			y = y - getRessource(ressource).getHeight();
			if (y < 0) {
				return ressource;
			}
		}
		return -1;
	}

	public int getTotalRessourceHeight() {
		if (totalRessourceHeight == -1) {
			recalcWidthCache();
		}
		return totalRessourceHeight;
	}

	public void setSelectionModel(ListSelectionModel newModel) {
		if (newModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null SelectionModel"); //$NON-NLS-1$
		}

		ListSelectionModel oldModel = selectionModel;

		if (newModel != oldModel) {
			if (oldModel != null) {
				oldModel.removeListSelectionListener(this);
			}

			selectionModel = newModel;
			newModel.addListSelectionListener(this);
		}
	}

	public ListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setRessourceSelectionAllowed(boolean flag) {
		ressourceSelectionAllowed = flag;
	}

	public boolean getRessourceSelectionAllowed() {
		return ressourceSelectionAllowed;
	}

	public int[] getSelectedRessources() {
		if (selectionModel != null) {
			int iMin = selectionModel.getMinSelectionIndex();
			int iMax = selectionModel.getMaxSelectionIndex();

			if ((iMin == -1) || (iMax == -1)) {
				return new int[0];
			}

			int[] rvTmp = new int[1 + (iMax - iMin)];
			int n = 0;
			for (int i = iMin; i <= iMax; i++) {
				if (selectionModel.isSelectedIndex(i)) {
					rvTmp[n++] = i;
				}
			}
			int[] rv = new int[n];
			System.arraycopy(rvTmp, 0, rv, 0, n);
			return rv;
		}
		return new int[0];
	}

	public int getSelectedRessourceCount() {
		if (selectionModel != null) {
			int iMin = selectionModel.getMinSelectionIndex();
			int iMax = selectionModel.getMaxSelectionIndex();
			int count = 0;

			for (int i = iMin; i <= iMax; i++) {
				if (selectionModel.isSelectedIndex(i)) {
					count++;
				}
			}
			return count;
		}
		return 0;
	}

	public void addRessourceModelListener(TimelineRessourceModelListener x) {
		listenerList.add(TimelineRessourceModelListener.class, x);
	}

	public void removeRessourceModelListener(TimelineRessourceModelListener x) {
		listenerList.remove(TimelineRessourceModelListener.class, x);
	}

	public TimelineRessourceModelListener[] getRessourceModelListeners() {
		return (TimelineRessourceModelListener[]) listenerList
				.getListeners(TimelineRessourceModelListener.class);
	}

	protected void fireRessourceAdded(TimelineRessourceModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineRessourceModelListener.class) {
				((TimelineRessourceModelListener) listeners[i + 1])
						.ressourceAdded(e);
			}
		}
	}

	protected void fireRessourceRemoved(TimelineRessourceModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineRessourceModelListener.class) {
				((TimelineRessourceModelListener) listeners[i + 1])
						.ressourceRemoved(e);
			}
		}
	}

	protected void fireRessourceMoved(TimelineRessourceModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineRessourceModelListener.class) {
				((TimelineRessourceModelListener) listeners[i + 1])
						.ressourceMoved(e);
			}
		}
	}

	protected void fireRessourceSelectionChanged(ListSelectionEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineRessourceModelListener.class) {
				((TimelineRessourceModelListener) listeners[i + 1])
						.ressourceSelectionChanged(e);
			}
		}
	}

	protected void fireRessourceMarginChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TimelineRessourceModelListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((TimelineRessourceModelListener) listeners[i + 1])
						.ressourceMarginChanged(changeEvent);
			}
		}
	}

	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();

		if (name == "height" || name == "preferredHeight") { //$NON-NLS-1$ //$NON-NLS-2$
			invalidateWidthCache();
			fireRessourceMarginChanged();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		fireRessourceSelectionChanged(e);
	}

	protected ListSelectionModel createSelectionModel() {
		return new DefaultListSelectionModel();
	}

	protected void recalcWidthCache() {
		Enumeration<TimelineRessource> enumeration = getRessources();
		totalRessourceHeight = 0;
		while (enumeration.hasMoreElements()) {
			totalRessourceHeight += enumeration.nextElement().getHeight();
		}
	}

	private void invalidateWidthCache() {
		totalRessourceHeight = -1;
	}
}
