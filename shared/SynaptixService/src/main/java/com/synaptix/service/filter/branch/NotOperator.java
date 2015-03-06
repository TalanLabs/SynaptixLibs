package com.synaptix.service.filter.branch;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.service.filter.AbstractNode;

/**
 * Not operator
 * 
 * NOT(node1)
 * 
 * @author Gaby
 * 
 */
public class NotOperator extends AbstractNode {

	private static final long serialVersionUID = -5232703998400460150L;

	public NotOperator(AbstractNode childNode) {
		super(childNode);
	}

	public final AbstractNode getChildNode() {
		return getChildNodes().get(0);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
