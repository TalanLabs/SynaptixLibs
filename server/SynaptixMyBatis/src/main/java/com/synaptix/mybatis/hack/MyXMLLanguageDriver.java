package com.synaptix.mybatis.hack;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

/**
 * Created by NicolasP on 01/12/2015.
 */
public class MyXMLLanguageDriver extends XMLLanguageDriver {

	@Override
	public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		return new MyDefaultParameterHandler(mappedStatement, parameterObject, boundSql);
	}
}
