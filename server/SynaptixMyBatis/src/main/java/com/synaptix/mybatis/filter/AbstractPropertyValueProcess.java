package com.synaptix.mybatis.filter;

import java.util.HashMap;
import java.util.Map;

import com.synaptix.entity.IEntity;
import com.synaptix.service.filter.leaf.AbstractPropertyValue;

public abstract class AbstractPropertyValueProcess<E extends AbstractPropertyValue<?>> extends AbstractPropertyProcess<E> {

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, E node) {
		Map<String, Object> res = new HashMap<String, Object>();
		String parameterFieldName = context.getParameterFieldName(node, node.getPropertyName());
		res.put(IEntity.class.isAssignableFrom(node.getValue().getClass()) ? parameterFieldName.replaceAll(".id", "") :
				parameterFieldName, node.getValue());
		return res;
	}
}
