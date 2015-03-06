package com.synaptix.service.filter.helper;

import java.util.Map;
import java.util.Map.Entry;

import com.synaptix.service.filter.branch.AndOperator;
import com.synaptix.service.filter.builder.AndOperatorBuilder;
import com.synaptix.service.filter.leaf.LikePropertyValue;

public class FilterNodeHelper {

	/**
	 * Convert value filter map to node
	 * 
	 * @param valueFilterMap
	 * @return
	 */
	public static final AndOperator convertValueFilterMapToNode(Map<String, Object> valueFilterMap) {
		AndOperator andOperator = null;
		if (valueFilterMap != null && !valueFilterMap.isEmpty()) {
			AndOperatorBuilder andOperatorBuilder = new AndOperatorBuilder();
			for (Entry<String, Object> entry : valueFilterMap.entrySet()) {
				convertValueFilterToNode(andOperatorBuilder, entry.getKey(), entry.getValue());

			}
			andOperator = andOperatorBuilder.build();
		}
		return andOperator;
	}

	public static final void convertValueFilterToNode(AndOperatorBuilder andOperatorBuilder, String propertyName, Object value) {
		if (value != null) {
			if (value instanceof String) {
				andOperatorBuilder.addLikePropertyValue(LikePropertyValue.Type.like_right, propertyName, (String) value);
			} else {
				andOperatorBuilder.addEqualsPropertyValue(propertyName, value);
			}
		}
	}
}
