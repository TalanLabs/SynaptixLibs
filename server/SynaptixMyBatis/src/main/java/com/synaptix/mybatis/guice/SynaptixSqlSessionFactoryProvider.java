package com.synaptix.mybatis.guice;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SynaptixSqlSessionFactoryProvider implements Provider<SqlSessionFactory> {

	private SqlSessionFactory sqlSessionFactory;

	@Inject
	public SynaptixSqlSessionFactoryProvider(Configuration configuration) {
		super();

		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}

	@Override
	public SqlSessionFactory get() {
		return sqlSessionFactory;
	}
}
