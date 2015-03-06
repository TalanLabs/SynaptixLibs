package com.synaptix.swing.path;

import javax.swing.event.EventListenerList;

public class PathSelectionModel {

	protected EventListenerList listenerList;

	private int selectedNodeFirst = -1;

	private int selectedNodeLast = -1;

	public PathSelectionModel() {
		listenerList = new EventListenerList();
	}

	public int getSelectedNodeFirst() {
		return selectedNodeFirst;
	}

	public int getSelectedNodeLast() {
		return selectedNodeLast;
	}

	public void setSelectedNode(int selectedNode) {
		setSelectedNode(selectedNode, -1);
	}

	public void setSelectedNode(int selectedNodeFirst, int selectedNodeLast) {
		this.selectedNodeFirst = selectedNodeFirst;
		this.selectedNodeLast = selectedNodeLast;
		firePathChanged();
	}

	public void clearSelection() {
		setSelectedNode(-1, -1);
	}

	public boolean isSelectionNode(int index) {
		return (selectedNodeLast == -1 && index == selectedNodeFirst)
				|| (index >= selectedNodeFirst && index <= selectedNodeLast);
	}

	public boolean isSelectionNode() {
		return selectedNodeFirst != -1 && selectedNodeLast == -1;
	}

	public boolean isSelectionLine(int index1, int index2) {
		return selectedNodeFirst <= index1 && selectedNodeLast >= index2;
	}

	public boolean isSelectionLine() {
		return selectedNodeFirst != -1 && selectedNodeLast != -1;
	}

	public void addPathSelectionListener(PathSelectionListener l) {
		listenerList.add(PathSelectionListener.class, l);
	}

	public void removePathSelectionListener(PathSelectionListener l) {
		listenerList.remove(PathSelectionListener.class, l);
	}

	public PathSelectionListener[] getPathSelectionListeners() {
		return (PathSelectionListener[]) listenerList
				.getListeners(PathSelectionListener.class);
	}

	protected void firePathChanged() {
		PathSelectionListener[] listeners = listenerList
				.getListeners(PathSelectionListener.class);

		for (PathSelectionListener listener : listeners) {
			listener.selectionChanged();
		}
	}
}
