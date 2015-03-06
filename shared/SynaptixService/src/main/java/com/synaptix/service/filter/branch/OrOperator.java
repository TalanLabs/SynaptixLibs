package com.synaptix.service.filter.branch;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.service.filter.AbstractNode;

/**
 * Or operator
 * 
 * node1 OR node2 OR ...
 * 
 * @author Gaby
 * 
 */
public class OrOperator extends AbstractBinaryOperator {

	private static final long serialVersionUID = -5232703998400460150L;

	public OrOperator(AbstractNode... childNodes) {
		super(childNodes);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
