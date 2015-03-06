package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.service.filter.AbstractNode;
import com.synaptix.service.filter.branch.AbstractBinaryOperator;

public abstract class AbstractBinaryOperatorProcess<E extends AbstractBinaryOperator> extends AbstractOperatorProcess<E> {

	private final String operator;

	@Inject
	public AbstractBinaryOperatorProcess(String operator) {
		super();

		this.operator = operator;
	}

	@Override
	public String process(IFilterContext context, E node) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (AbstractNode childNode : node.getChildNodes()) {
			if (first) {
				first = false;
			} else {
				sb.append(" ").append(operator).append(" ");
			}
			sb.append("(");
			sb.append(context.process(childNode));
			sb.append(")");
		}
		return sb.toString();
	}
}
