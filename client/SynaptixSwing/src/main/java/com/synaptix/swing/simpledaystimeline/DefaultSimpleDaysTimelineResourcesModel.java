package com.synaptix.swing.simpledaystimeline;

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

import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelListener;

public class DefaultSimpleDaysTimelineResourcesModel implements
		SimpleDaysTimelineResourcesModel, PropertyChangeListener,
		ListSelectionListener, Serializable {

	private static final long serialVersionUID = 4139411379986126705L;

	protected Vector<SimpleDaysTimelineResource> timelineResources;

	protected ListSelectionModel selectionModel;

	protected int resourceMargin;

	protected EventListenerList listenerList = new EventListenerList();

	transient protected ChangeEvent changeEvent = null;

	protected boolean resourceSelectionAllowed;

	protected int totalResourceHeight;

	public DefaultSimpleDaysTimelineResourcesModel() {
		super();

		timelineResources = new Vector<SimpleDaysTimelineResource>();
		setSelectionModel(createSelectionModel());
		setResourceMargin(1);
		invalidateWidthCache();
		setResourceSelectionAllowed(false);
	}

	public void addResource(SimpleDaysTimelineResource aResource) {
		if (aResource == null) {
			throw new IllegalArgumentException("Object is null"); //$NON-NLS-1$
		}

		timelineResources.addElement(aResource);
		aResource.addPropertyChangeListener(this);
		invalidateWidthCache();

		fireResourceAdded(new SimpleDaysTimelineResourcesModelEvent(this, 0,
				getResourceCount() - 1));
	}

	public void removeResource(SimpleDaysTimelineResource resource) {
		int resourceIndex = timelineResources.indexOf(resource);

		if (resourceIndex != -1) {
			if (selectionModel != null) {
				selectionModel
						.removeIndexInterval(resourceIndex, resourceIndex);
			}

			resource.removePropertyChangeListener(this);
			timelineResources.removeElementAt(resourceIndex);
			invalidateWidthCache();

			fireResourceRemoved(new SimpleDaysTimelineResourcesModelEvent(this,
					resourceIndex, 0));
		}
	}

	public void moveResource(int resourceIndex, int newIndex) {
		if ((resourceIndex < 0) || (resourceIndex >= getResourceCount())
				|| (newIndex < 0) || (newIndex >= getResourceCount()))
			throw new IllegalArgumentException(
					"moveResource() - Index out of range"); //$NON-NLS-1$

		SimpleDaysTimelineResource aResource;

		if (resourceIndex == newIndex) {
			fireResourceMoved(new SimpleDaysTimelineResourcesModelEvent(this,
					resourceIndex, newIndex));
			return;
		}
		aResource = timelineResources.elementAt(resourceIndex);

		timelineResources.removeElementAt(resourceIndex);
		boolean selected = selectionModel.isSelectedIndex(resourceIndex);
		selectionModel.removeIndexInterval(resourceIndex, resourceIndex);

		timelineResources.insertElementAt(aResource, newIndex);
		selectionModel.insertIndexInterval(newIndex, 1, true);
		if (selected) {
			selectionModel.addSelectionInterval(newIndex, newIndex);
		} else {
			selectionModel.removeSelectionInterval(newIndex, newIndex);
		}

		fireResourceMoved(new SimpleDaysTimelineResourcesModelEvent(this,
				resourceIndex, newIndex));
	}

	public void setResourceMargin(int newMargin) {
		if (newMargin != resourceMargin) {
			resourceMargin = newMargin;
			fireResourceMarginChanged();
		}
	}

	public int getResourceCount() {
		return timelineResources.size();
	}

	public Enumeration<SimpleDaysTimelineResource> getResources() {
		return timelineResources.elements();
	}

	public int getResourceIndex(Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier is null"); //$NON-NLS-1$
		}

		Enumeration<SimpleDaysTimelineResource> enumeration = getResources();
		SimpleDaysTimelineResource aResource;
		int index = 0;

		while (enumeration.hasMoreElements()) {
			aResource = enumeration.nextElement();
			if (identifier.equals(aResource.getIdentifier()))
				return index;
			index++;
		}
		throw new IllegalArgumentException("Identifier not found"); //$NON-NLS-1$
	}

	public SimpleDaysTimelineResource getResource(int resourceIndex) {
		return timelineResources.elementAt(resourceIndex);
	}

	public int getDefaultHeight() {
		return SimpleDaysTimelineResource.DEFAULT_HEIGHT;
	}

	public int getHeight(int resourceIndex) {
		return getResource(resourceIndex).getHeight();
	}

	public int getResourceMargin() {
		return resourceMargin;
	}

	public int getResourceIndexAtY(int y) {
		if (y < 0) {
			return -1;
		}
		int cc = getResourceCount();
		for (int resource = 0; resource < cc; resource++) {
			y = y - getResource(resource).getHeight();
			if (y < 0) {
				return resource;
			}
		}
		return -1;
	}

	public int getTotalResourceHeight() {
		if (totalResourceHeight == -1) {
			recalcWidthCache();
		}
		return totalResourceHeight;
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

	public void setResourceSelectionAllowed(boolean flag) {
		resourceSelectionAllowed = flag;
	}

	public boolean getResourceSelectionAllowed() {
		return resourceSelectionAllowed;
	}

	public int[] getSelectedResources() {
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

	public int getSelectedResourceCount() {
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

	public void addResourcesModelListener(
			SimpleDaysTimelineResourcesModelListener x) {
		listenerList.add(SimpleDaysTimelineResourcesModelListener.class, x);
	}

	public void removeResourcesModelListener(
			SimpleDaysTimelineResourcesModelListener x) {
		listenerList.remove(SimpleDaysTimelineResourcesModelListener.class, x);
	}

	public SimpleDaysTimelineResourcesModelListener[] getResourceModelListeners() {
		return (SimpleDaysTimelineResourcesModelListener[]) listenerList
				.getListeners(SimpleDaysTimelineResourcesModelListener.class);
	}

	protected void fireResourceAdded(SimpleDaysTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineResourcesModelListener.class) {
				((SimpleDaysTimelineResourcesModelListener) listeners[i + 1])
						.resourceAdded(e);
			}
		}
	}

	protected void fireResourceRemoved(SimpleDaysTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineResourcesModelListener.class) {
				((SimpleDaysTimelineResourcesModelListener) listeners[i + 1])
						.resourceRemoved(e);
			}
		}
	}

	protected void fireResourceMoved(SimpleDaysTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineResourcesModelListener.class) {
				((SimpleDaysTimelineResourcesModelListener) listeners[i + 1])
						.resourceMoved(e);
			}
		}
	}

	protected void fireResourceSelectionChanged(ListSelectionEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineResourcesModelListener.class) {
				((SimpleDaysTimelineResourcesModelListener) listeners[i + 1])
						.resourceSelectionChanged(e);
			}
		}
	}

	protected void fireResourceMarginChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineResourcesModelListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((SimpleDaysTimelineResourcesModelListener) listeners[i + 1])
						.resourceMarginChanged(changeEvent);
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
			fireResourceMarginChanged();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		fireResourceSelectionChanged(e);
	}

	protected ListSelectionModel createSelectionModel() {
		return new DefaultListSelectionModel();
	}

	protected void recalcWidthCache() {
		Enumeration<SimpleDaysTimelineResource> enumeration = getResources();
		totalResourceHeight = 0;
		while (enumeration.hasMoreElements()) {
			totalResourceHeight += enumeration.nextElement().getHeight();
		}
	}

	private void invalidateWidthCache() {
		totalResourceHeight = -1;
	}
}
