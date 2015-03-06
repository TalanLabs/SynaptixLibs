package com.synaptix.mybatis.filter;

import java.util.Map;
import java.util.Set;

import com.synaptix.service.filter.AbstractNode;

public abstract class AbstractFilterContext implements IFilterContext {

	private final NodeProcessFactory nodeProcessFactory;

	public AbstractFilterContext(NodeProcessFactory nodeProcessFactory) {
		super();

		this.nodeProcessFactory = nodeProcessFactory;
	}

	@Override
	public <F extends AbstractNode> String process(F childNode) {
		return nodeProcessFactory.getNodeProcess(childNode).process(this, childNode);
	}

	@Override
	public <F extends AbstractNode> Map<String, Object> buildValueFilterMap(F childNode) {
		return nodeProcessFactory.getNodeProcess(childNode).buildValueFilterMap(this, childNode);
	}

	@Override
	public <F extends AbstractNode> Set<String> buildPropertyNames(F childNode) {
		return nodeProcessFactory.getNodeProcess(childNode).buildPropertyNames(this, childNode);
	}
}
