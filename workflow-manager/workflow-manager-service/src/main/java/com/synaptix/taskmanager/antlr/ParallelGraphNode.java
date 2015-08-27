package com.synaptix.taskmanager.antlr;

import java.util.List;

public class ParallelGraphNode extends AbstractGraphNode {

	private final List<AbstractGraphNode> nodes;

	public ParallelGraphNode(List<AbstractGraphNode> nodes) {
		super();
		this.nodes = nodes;
	}

	public List<AbstractGraphNode> getNodes() {
		return nodes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		boolean first = true;
		for (AbstractGraphNode node : nodes) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(node);
		}
		sb.append(")");
		return sb.toString();
	}
}
