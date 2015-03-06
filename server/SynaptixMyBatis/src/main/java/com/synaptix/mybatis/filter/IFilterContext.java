package com.synaptix.mybatis.filter;

import java.util.Map;
import java.util.Set;

import org.apache.ibatis.type.JdbcType;

import com.synaptix.service.filter.AbstractNode;

public interface IFilterContext {

	public <E extends AbstractNode> String getParameterFieldName(E node, String propertyName);

	public <E extends AbstractNode> String getSqlName(E node, String propertyName);

	public <E extends AbstractNode> String process(E childNode);

	public <E extends AbstractNode> Map<String, Object> buildValueFilterMap(E childNode);

	public <F extends AbstractNode> Set<String> buildPropertyNames(F childNode);

	public <E extends AbstractNode> String getMyBatisParameterField(E node, Class<?> javaType, JdbcType jdbcType, String propertyName);

}
