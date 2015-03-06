package com.synaptix.mybatis.filter;

import java.util.HashMap;
import java.util.Map;

import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.InPropertyValue;

@NodeProcess(InPropertyValue.class)
public class InPropertyValueProcess extends AbstractPropertyValueProcess<InPropertyValue<?>> {

	public InPropertyValueProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, InPropertyValue<?> node) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getSqlName(node, node.getPropertyName()));
		switch (node.getType()) {
		case in:
			sb.append(" in ");
			break;
		case not_in:
			sb.append(" not in ");
			break;
		}
		sb.append("(");
		for (int i = 0; i < node.getValue().length; i++) {
			if (i > 0) {
				sb.append(" , ");
			}
			sb.append(context.getMyBatisParameterField(node, null, null, buildFieldName(context, node, i)));
		}
		sb.append(")");

		return sb.toString();
	}

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, InPropertyValue<?> node) {
		Map<String, Object> res = new HashMap<String, Object>();
		for (int i = 0; i < node.getValue().length; i++) {
			res.put(buildFieldName(context, node, i), node.getValue()[i]);
		}
		return res;
	}

	private String buildFieldName(IFilterContext context, InPropertyValue<?> node, int i) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getParameterFieldName(node, node.getPropertyName())).append("_").append(i);
		return sb.toString();
	}
}
