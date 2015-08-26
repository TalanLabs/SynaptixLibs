package com.synaptix.taskmanager.antlr;

import org.apache.commons.lang.builder.ToStringBuilder;

public class IdGraphNode extends AbstractGraphNode {

	private final String id;

	public IdGraphNode(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
