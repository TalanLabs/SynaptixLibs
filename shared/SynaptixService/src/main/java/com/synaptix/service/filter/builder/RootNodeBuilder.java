package com.synaptix.service.filter.builder;

import com.synaptix.service.filter.RootNode;
import com.synaptix.service.filter.branch.AndOperator;

public class RootNodeBuilder extends AbstractOperatorBuilder<RootNode, RootNodeBuilder> {

	public RootNodeBuilder() {
		super();
	}

	@Override
	protected RootNodeBuilder get() {
		return this;
	}

	@Override
	public RootNode build() {
		return new RootNode(new AndOperator(getChildNodes()));
	}
}
