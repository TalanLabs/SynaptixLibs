package com.synaptix.widget.path.view.swing;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.swing.path.PathSelectionModel;

public class ExtendedPathSelectionModel extends PathSelectionModel {

	private int selectedNodeFirst = -1;

	private int selectedNodeLast = -1;

	private List<Integer> selectedNodes = new ArrayList<Integer>();

	private boolean selectable = true;

	public ExtendedPathSelectionModel() {
		super();
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public List<Integer> getSelectedNodes() {
		return selectedNodes;
	}

	@Override
	public void setSelectedNode(int selectedNode) {
		if (selectable) {
			setSelectedNode(-1, -1);
			selectedNodes.add(Integer.valueOf(selectedNode));
		}
		// firePathChanged();
	}

	@Override
	public void setSelectedNode(int selectedNodeFirst, int selectedNodeLast) {
		if (selectable) {
			this.selectedNodeFirst = selectedNodeFirst;
			this.selectedNodeLast = selectedNodeLast;
		}
		// selectedNodes = new ArrayList<Integer>();
		// firePathChanged();
	}

	@Override
	public void clearSelection() {
		selectedNodes.clear();
		setSelectedNode(-1, -1);
	}

	@Override
	public boolean isSelectionNode(int index) {
		for (Integer i : selectedNodes) {
			if (i.intValue() == index) {
				return true;
			}
		}
		return (index >= selectedNodeFirst && index <= selectedNodeLast);
	}

	@Override
	public boolean isSelectionNode() {
		return (selectedNodeFirst != -1 && selectedNodeLast == -1) || !selectedNodes.isEmpty();
	}

	@Override
	public boolean isSelectionLine(int index1, int index2) {
		for (int i = index1; i < index2; i++) {
			if (!isSelectionNode(i)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isSelectionLine() {
		return selectedNodeFirst != -1 && selectedNodeLast != -1;
	}

}