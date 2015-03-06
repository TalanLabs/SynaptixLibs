package com.synaptix.mybatis.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.synaptix.service.filter.AbstractNode;

public abstract class AbstractOperatorProcess<E extends AbstractNode> extends AbstractNodeProcess<E> {

	@Override
	public Map<String, Object> buildValueFilterMap(IFilterContext context, E node) {
		Map<String, Object> res = new HashMap<String, Object>();
		for (AbstractNode childNode : node.getChildNodes()) {
			Map<String, Object> map = context.buildValueFilterMap(childNode);
			if (map != null) {
				res.putAll(map);
			}
		}
		return res;
	}

	@Override
	public Set<String> buildPropertyNames(IFilterContext context, E node) {
		Set<String> res = new HashSet<String>();
		for (AbstractNode childNode : node.getChildNodes()) {
			Set<String> set = context.buildPropertyNames(childNode);
			if (set != null) {
				res.addAll(set);
			}
		}
		return res;
	}
}
