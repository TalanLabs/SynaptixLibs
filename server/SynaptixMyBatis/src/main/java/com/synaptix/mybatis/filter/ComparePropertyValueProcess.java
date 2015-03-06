package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.ComparePropertyValue;

@NodeProcess(ComparePropertyValue.class)
public class ComparePropertyValueProcess extends AbstractPropertyValueProcess<ComparePropertyValue<?>> {

	@Inject
	public ComparePropertyValueProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, ComparePropertyValue<?> node) {
		StringBuilder sb = new StringBuilder();

		sb.append(context.getSqlName(node, node.getPropertyName()));

		switch (node.getType()) {
		case equal:
			sb.append(" = ");
			break;
		case lower:
			sb.append(" < ");
			break;
		case lower_equal:
			sb.append(" <= ");
			break;
		case upper_equal:
			sb.append(" >= ");
			break;
		case upper:
			sb.append(" > ");
			break;
		case not_equal:
			sb.append(" <> ");
			break;
		}
		sb.append(context.getMyBatisParameterField(node, null, null, context.getParameterFieldName(node, node.getPropertyName())));
		return sb.toString();
	}
}
