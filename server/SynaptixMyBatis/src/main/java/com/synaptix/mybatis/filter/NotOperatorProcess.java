package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.branch.NotOperator;

@NodeProcess(NotOperator.class)
public class NotOperatorProcess extends AbstractOperatorProcess<NotOperator> {

	@Inject
	public NotOperatorProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, NotOperator node) {
		StringBuilder sb = new StringBuilder();
		sb.append("NOT(");
		sb.append(context.process(node.getChildNode()));
		sb.append(")");
		return sb.toString();
	}
}
