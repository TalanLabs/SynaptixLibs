package com.synaptix.taskmanager.antlr;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

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
		return ToStringBuilder.reflectionToString(this);
	}
}
