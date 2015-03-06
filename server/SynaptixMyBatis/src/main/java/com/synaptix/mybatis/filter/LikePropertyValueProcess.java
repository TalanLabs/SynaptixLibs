package com.synaptix.mybatis.filter;

import org.apache.ibatis.type.JdbcType;

import com.google.inject.Inject;
import com.synaptix.mybatis.filter.AbstractNodeProcess.NodeProcess;
import com.synaptix.service.filter.leaf.LikePropertyValue;

@NodeProcess(LikePropertyValue.class)
public class LikePropertyValueProcess extends AbstractPropertyValueProcess<LikePropertyValue> {

	@Inject
	public LikePropertyValueProcess() {
		super();
	}

	@Override
	public String process(IFilterContext context, LikePropertyValue node) {
		StringBuilder sb = new StringBuilder();

		// we could check if the property is upperOnly
		sb.append("UPPER(").append(context.getSqlName(node, node.getPropertyName())).append(")");
		sb.append(" like ");

		switch (node.getType()) {
		case like_left:
		case like_left_right:
			sb.append("'%' || ");
			break;
		default:
			break;
		}
		sb.append("UPPER(").append(context.getMyBatisParameterField(node, String.class, JdbcType.VARCHAR, context.getParameterFieldName(node, node.getPropertyName()))).append(")");
		switch (node.getType()) {
		case like_right:
		case like_left_right:
			sb.append(" || '%'");
			break;
		default:
			break;
		}
		return sb.toString();
	}
}
