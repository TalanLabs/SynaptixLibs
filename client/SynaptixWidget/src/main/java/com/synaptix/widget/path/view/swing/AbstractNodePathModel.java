package com.synaptix.widget.path.view.swing;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.swing.path.AbstractPathModel;

public abstract class AbstractNodePathModel<E extends AbstractBaseLocalNode<?>> extends AbstractPathModel {

	protected final List<E> nodes = new ArrayList<E>();

	protected final List<String> nodeNames = new ArrayList<String>();

	protected final List<String> lineNames = new ArrayList<String>();

	public AbstractNodePathModel() {
		super();
	}

	public void setNodes(List<E> nodes, List<String> lineNames) {
		this.nodes.clear();
		this.lineNames.clear();
		if (nodes != null) {
			this.nodes.addAll(nodes);
		}
		if (lineNames != null) {
			this.lineNames.addAll(lineNames);
		}

		buildNodeNames();

		firePathChanged();
	}

	public List<E> getNodes() {
		return nodes;
	}

	public int getNodeCount() {
		return nodes.size();
	}

	public String getNodeName(int index) {
		return nodeNames.get(index);
	}

	public String getLineName(int index1, int index2) {
		if (index1 < lineNames.size()) {
			return lineNames.get(index1);
		}
		return null;
	}

	public boolean isSelectedLine(int index1, int index2) {
		return false;
	}

	public boolean isSelectedNode(int index) {
		return false;
	}

	protected abstract void buildNodeNames();

}
