package com.synaptix.service.filter;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Principal node
 * 
 * @author Gaby
 * 
 */
public class RootNode extends AbstractNode {

	private static final long serialVersionUID = 8342461828037954831L;

	public RootNode(AbstractNode node) {
		super(node);
	}

	public final AbstractNode getNode() {
		return getChildNodes().get(0);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
