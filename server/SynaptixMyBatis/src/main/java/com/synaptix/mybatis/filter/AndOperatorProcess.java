package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.branch.AndOperator;

@NodeProcess(AndOperator.class)
public class AndOperatorProcess extends AbstractBinaryOperatorProcess<AndOperator> {

	@Inject
	public AndOperatorProcess() {
		super("AND");
	}
}
