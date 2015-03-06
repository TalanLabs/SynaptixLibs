package com.synaptix.mybatis.filter;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.AssossiationLeaf;

@NodeProcess(AssossiationLeaf.class)
public class AssossiationProcess extends AbstractPropertyValueProcess<AssossiationLeaf<?>> {

	@Inject
	public AssossiationProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, AssossiationLeaf<?> node) {
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
		sb.append(" WHERE ").append(node.getAssossiationSqlTable()).append(".").append(node.getAssossiationSqlColumn1());
		sb.append(" = ").append(context.getSqlName(node, node.getPropertyName()));
		sb.append(" and ").append(node.getAssossiationSqlTable()).append(".").append(node.getAssossiationSqlColumn2());
		sb.append(" = ").append(context.getMyBatisParameterField(node, null, null, buildFieldName(context, node)));
		sb.append(")");

		return sb.toString();
	}

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, AssossiationLeaf<?> node) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(buildFieldName(context, node), node.getValue());
		return res;
	}

	private String buildFieldName(IFilterContext context, AssossiationLeaf<?> node) {
		StringBuilder sb = new StringBuilder();
		sb.append("_").append("value").append("_").append(node.hashCode());
		return sb.toString();
	}

}
