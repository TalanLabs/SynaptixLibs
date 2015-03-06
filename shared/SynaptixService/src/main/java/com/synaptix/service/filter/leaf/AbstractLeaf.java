package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.service.filter.AbstractNode;

public abstract class AbstractLeaf extends AbstractNode {

	private static final long serialVersionUID = 4303634588126903868L;

	public AbstractLeaf() {
		super();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
