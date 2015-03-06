package com.synaptix.service.filter.branch;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.service.filter.AbstractNode;

/**
 * And operator
 * 
 * node1 AND node2 AND ...
 * 
 * @author Gaby
 * 
 */
public class AndOperator extends AbstractBinaryOperator {

	private static final long serialVersionUID = -5045299804608440897L;

	public AndOperator(AbstractNode... childNodes) {
		super(childNodes);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
