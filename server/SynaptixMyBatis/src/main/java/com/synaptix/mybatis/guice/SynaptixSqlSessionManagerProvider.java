package com.synaptix.mybatis.guice;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

public class SynaptixSqlSessionManagerProvider implements Provider<SqlSessionManager> {

	private SqlSessionManager sqlSessionManager;

	@Inject
	public SynaptixSqlSessionManagerProvider(SqlSessionFactory sqlSessionFactory) {
		super();

		this.sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
	}

	public SqlSessionManager get() {
		return sqlSessionManager;
	}
}
