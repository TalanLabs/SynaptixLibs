package com.synaptix.mybatis.filter;

import java.util.HashMap;
import java.util.Map;

import com.synaptix.service.filter.leaf.AbstractPropertyValue;

public abstract class AbstractPropertyValueProcess<E extends AbstractPropertyValue<?>> extends AbstractPropertyProcess<E> {

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, E node) {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(context.getParameterFieldName(node, node.getPropertyName()), node.getValue());
		return res;
	}
}
