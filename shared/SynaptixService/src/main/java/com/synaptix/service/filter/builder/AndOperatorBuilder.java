package com.synaptix.service.filter.builder;

import com.synaptix.service.filter.branch.AndOperator;

public class AndOperatorBuilder extends AbstractOperatorBuilder<AndOperator, AndOperatorBuilder> {

	public AndOperatorBuilder() {
		super();
	}

	@Override
	protected AndOperatorBuilder get() {
		return this;
	}

	@Override
	public AndOperator build() {
		return new AndOperator(getChildNodes());
	}
}
