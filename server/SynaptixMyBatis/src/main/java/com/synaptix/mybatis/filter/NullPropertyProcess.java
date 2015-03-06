package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.NullProperty;

@NodeProcess(NullProperty.class)
public class NullPropertyProcess extends AbstractPropertyProcess<NullProperty> {

	@Inject
	public NullPropertyProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, NullProperty node) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getSqlName(node, node.getPropertyName()));
		switch (node.getType()) {
		case is_null:
			sb.append(" is null");
			break;
		case is_not_null:
			sb.append(" is not null");
			break;
		}
		return sb.toString();
	}
}
