package com.synaptix.service.filter.builder;

import com.synaptix.service.filter.branch.OrOperator;

public class OrOperatorBuilder extends AbstractOperatorBuilder<OrOperator, OrOperatorBuilder> {

	public OrOperatorBuilder() {
		super();
	}

	@Override
	protected OrOperatorBuilder get() {
		return this;
	}

	@Override
	public OrOperator build() {
		return new OrOperator(getChildNodes());
	}
}
