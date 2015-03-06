package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.EqualsPropertyValue;

@NodeProcess(EqualsPropertyValue.class)
public class EqualsPropertyValueProcess extends AbstractPropertyValueProcess<EqualsPropertyValue<?>> {

	@Inject
	public EqualsPropertyValueProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, EqualsPropertyValue<?> node) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getSqlName(node, node.getPropertyName()));
		sb.append(" = ");
		sb.append(context.getMyBatisParameterField(node, null, null, context.getParameterFieldName(node, node.getPropertyName())));
		return sb.toString();
	}
}
