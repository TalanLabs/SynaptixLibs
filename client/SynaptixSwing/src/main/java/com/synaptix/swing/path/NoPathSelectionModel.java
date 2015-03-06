package com.synaptix.swing.path;

public class NoPathSelectionModel extends PathSelectionModel {

	@Override
	public int getSelectedNodeFirst() {
		return -1;
	}

	@Override
	public int getSelectedNodeLast() {
		return -1;
	}

	@Override
	public void setSelectedNode(int selectedNode) {
		setSelectedNode(selectedNode, -1);
	}

	@Override
	public void setSelectedNode(int selectedNodeFirst, int selectedNodeLast) {
		firePathChanged();
	}

	@Override
	public void clearSelection() {
		setSelectedNode(-1, -1);
	}

	@Override
	public boolean isSelectionNode(int index) {
		return false;
	}

	@Override
	public boolean isSelectionNode() {
		return false;
	}

	@Override
	public boolean isSelectionLine(int index1, int index2) {
		return false;
	}

	@Override
	public boolean isSelectionLine() {
		return false;
	}

}
