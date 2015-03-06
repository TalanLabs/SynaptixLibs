package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.branch.OrOperator;

@NodeProcess(OrOperator.class)
public class OrOperatorProcess extends AbstractBinaryOperatorProcess<OrOperator> {

	@Inject
	public OrOperatorProcess() {
		super("OR");
	}
}
