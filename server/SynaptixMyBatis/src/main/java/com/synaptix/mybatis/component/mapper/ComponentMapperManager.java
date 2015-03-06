package com.synaptix.mybatis.component.mapper;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.helper.SelectSqlMappedStatement;

public class ComponentMapperManager {

	private final IDaoSession daoSession;

	private final SelectSqlMappedStatement selectSqlMappedStatement;

	private Map<Class<? extends IComponentMapper>, IComponentMapper> map;

	@Inject
	public ComponentMapperManager(IDaoSession daoSession, SelectSqlMappedStatement selectSqlMappedStatement) {
		super();

		this.daoSession = daoSession;
		this.selectSqlMappedStatement = selectSqlMappedStatement;

		this.map = new HashMap<Class<? extends IComponentMapper>, IComponentMapper>();
	}

	@SuppressWarnings("unchecked")
	public <E extends IComponentMapper> void addComponentMapper(Class<E> componentMapperClass) {
		assert componentMapperClass != null;

		ClassLoader loader = componentMapperClass.getClassLoader();
		Class<?>[] interfaces = new Class<?>[] { componentMapperClass };
		ComponentMapperProxy<E> componentMapperProxy = new ComponentMapperProxy<E>(daoSession, selectSqlMappedStatement, componentMapperClass);
		E componentMapper = (E) Proxy.newProxyInstance(loader, interfaces, componentMapperProxy);
		map.put(componentMapperClass, componentMapper);
	}

	@SuppressWarnings("unchecked")
	public <E extends IComponentMapper> E getComponentMapper(Class<E> componentMapperClass) {
		assert componentMapperClass != null;
		if (!map.containsKey(componentMapperClass)) {
			throw new IllegalArgumentException("ComponentMapperClass is not added " + componentMapperClass);
		}
		return (E) map.get(componentMapperClass);
	}
}
