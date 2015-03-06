package com.synaptix.swing.simpletimeline;

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

import com.synaptix.swing.event.SimpleTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleTimelineResourcesModelListener;

public class DefaultSimpleTimelineResourcesModel implements
		SimpleTimelineResourcesModel, PropertyChangeListener,
		ListSelectionListener, Serializable {

	private static final long serialVersionUID = 4139411379986126705L;

	protected Vector<SimpleTimelineResource> timelineResources;

	protected ListSelectionModel selectionModel;

	protected int resourceMargin;

	protected EventListenerList listenerList = new EventListenerList();

	transient protected ChangeEvent changeEvent = null;

	protected boolean resourceSelectionAllowed;

	protected int totalResourceHeight;

	public DefaultSimpleTimelineResourcesModel() {
		super();

		timelineResources = new Vector<SimpleTimelineResource>();
		setSelectionModel(createSelectionModel());
		setResourceMargin(1);
		invalidateWidthCache();
		setResourceSelectionAllowed(false);
	}

	public void addResource(SimpleTimelineResource aResource) {
		if (aResource == null) {
			throw new IllegalArgumentException("Object is null"); //$NON-NLS-1$
		}

		timelineResources.addElement(aResource);
		aResource.addPropertyChangeListener(this);
		invalidateWidthCache();

		fireResourceAdded(new SimpleTimelineResourcesModelEvent(this, 0,
				getResourceCount() - 1));
	}

	public void removeResource(SimpleTimelineResource resource) {
		int resourceIndex = timelineResources.indexOf(resource);

		if (resourceIndex != -1) {
			if (selectionModel != null) {
				selectionModel.removeIndexInterval(resourceIndex,
						resourceIndex);
			}

			resource.removePropertyChangeListener(this);
			timelineResources.removeElementAt(resourceIndex);
			invalidateWidthCache();

			fireResourceRemoved(new SimpleTimelineResourcesModelEvent(this,
					resourceIndex, 0));
		}
	}

	public void moveResource(int resourceIndex, int newIndex) {
		if ((resourceIndex < 0) || (resourceIndex >= getResourceCount())
				|| (newIndex < 0) || (newIndex >= getResourceCount()))
			throw new IllegalArgumentException(
					"moveResource() - Index out of range"); //$NON-NLS-1$

		SimpleTimelineResource aResource;

		if (resourceIndex == newIndex) {
			fireResourceMoved(new SimpleTimelineResourcesModelEvent(this,
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

		fireResourceMoved(new SimpleTimelineResourcesModelEvent(this,
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

	public Enumeration<SimpleTimelineResource> getResources() {
		return timelineResources.elements();
	}

	public int getResourceIndex(Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier is null"); //$NON-NLS-1$
		}

		Enumeration<SimpleTimelineResource> enumeration = getResources();
		SimpleTimelineResource aResource;
		int index = 0;

		while (enumeration.hasMoreElements()) {
			aResource = enumeration.nextElement();
			if (identifier.equals(aResource.getIdentifier()))
				return index;
			index++;
		}
		throw new IllegalArgumentException("Identifier not found"); //$NON-NLS-1$
	}

	public SimpleTimelineResource getResource(int resourceIndex) {
		return timelineResources.elementAt(resourceIndex);
	}

	public int getDefaultHeight() {
		return SimpleTimelineResource.DEFAULT_HEIGHT;
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

	public void addResourcesModelListener(SimpleTimelineResourcesModelListener x) {
		listenerList.add(SimpleTimelineResourcesModelListener.class, x);
	}

	public void removeResourcesModelListener(
			SimpleTimelineResourcesModelListener x) {
		listenerList.remove(SimpleTimelineResourcesModelListener.class, x);
	}

	public SimpleTimelineResourcesModelListener[] getResourceModelListeners() {
		return (SimpleTimelineResourcesModelListener[]) listenerList
				.getListeners(SimpleTimelineResourcesModelListener.class);
	}

	protected void fireResourceAdded(SimpleTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineResourcesModelListener.class) {
				((SimpleTimelineResourcesModelListener) listeners[i + 1])
						.resourceAdded(e);
			}
		}
	}

	protected void fireResourceRemoved(SimpleTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineResourcesModelListener.class) {
				((SimpleTimelineResourcesModelListener) listeners[i + 1])
						.resourceRemoved(e);
			}
		}
	}

	protected void fireResourceMoved(SimpleTimelineResourcesModelEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineResourcesModelListener.class) {
				((SimpleTimelineResourcesModelListener) listeners[i + 1])
						.resourceMoved(e);
			}
		}
	}

	protected void fireResourceSelectionChanged(ListSelectionEvent e) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineResourcesModelListener.class) {
				((SimpleTimelineResourcesModelListener) listeners[i + 1])
						.resourceSelectionChanged(e);
			}
		}
	}

	protected void fireResourceMarginChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineResourcesModelListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((SimpleTimelineResourcesModelListener) listeners[i + 1])
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
		Enumeration<SimpleTimelineResource> enumeration = getResources();
		totalResourceHeight = 0;
		while (enumeration.hasMoreElements()) {
			totalResourceHeight += enumeration.nextElement().getHeight();
		}
	}

	private void invalidateWidthCache() {
		totalResourceHeight = -1;
	}
}
