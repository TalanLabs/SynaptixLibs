package com.synaptix.mybatis.filter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.synaptix.service.filter.leaf.AbstractProperty;

public abstract class AbstractPropertyProcess<E extends AbstractProperty> extends AbstractNodeProcess<E> {

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, E node) {
		return null;
	}

	@Override
	public Set<String> buildPropertyNames(IFilterContext context, E node) {
		Set<String> res = new HashSet<String>();
		res.add(node.getPropertyName());
		return res;
	}
}
