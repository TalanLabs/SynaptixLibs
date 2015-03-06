package com.synaptix.service.filter.branch;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.service.filter.AbstractNode;

public abstract class AbstractBinaryOperator extends AbstractNode {

	private static final long serialVersionUID = 8993183968426524236L;

	public AbstractBinaryOperator(AbstractNode... childNodes) {
		super(childNodes);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
