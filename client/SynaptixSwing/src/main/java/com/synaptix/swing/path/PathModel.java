package com.synaptix.swing.path;

public interface PathModel {

	public abstract int getNodeCount();

	public abstract String getNodeName(int index);

	public abstract String getLineName(int index1, int index2);

	public abstract boolean isSelectedNode(int index);

	public abstract boolean isSelectedLine(int index1, int index2);

	public void addPathModelListener(PathModelListener l);

	public void removePathModelListener(PathModelListener l);
}
