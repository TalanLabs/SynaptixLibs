package com.synaptix.mybatis.filter;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.AssossiationPropertyValue;

@NodeProcess(AssossiationPropertyValue.class)
public class AssossiationPropertyValueProcess extends AbstractPropertyValueProcess<AssossiationPropertyValue<?>> {

	@Inject
	public AssossiationPropertyValueProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, AssossiationPropertyValue<?> node) {
		StringBuilder sb = new StringBuilder();

		switch (node.getType()) {
		case exists:
			sb.append("EXISTS (");
			break;
		case not_exists:
			sb.append("NOT EXISTS (");
			break;
		}
		sb.append("SELECT 1 FROM ").append(node.getAssossiationSqlTable()).append(" ").append(node.getAssossiationSqlTable());
		sb.append(" WHERE ").append(node.getAssossiationSqlTable()).append(".").append(node.getAssossiationSqlColumn());
		sb.append(" = ").append(context.getMyBatisParameterField(node, null, null, context.getParameterFieldName(node, node.getPropertyName())));
		sb.append(")");

		return sb.toString();
	}
}
