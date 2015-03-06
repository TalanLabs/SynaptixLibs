package com.synaptix.mybatis.guice;

import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SynaptixMapperProvider<T> implements Provider<T> {

	private final Class<T> mapperType;

	private SqlSessionManager sqlSessionManager;

	public SynaptixMapperProvider(Class<T> mapperType) {
		super();

		this.mapperType = mapperType;
		// Pour garder l'ordre des ajouts de mapper
		// ListMapper.getInstance().addMapper(mapperType);
	}

	@Inject
	public void setSqlSessionManager(SqlSessionManager sqlSessionManager) {
		this.sqlSessionManager = sqlSessionManager;
	}

	@Override
	public T get() {
		return this.sqlSessionManager.getMapper(mapperType);
	}
}
