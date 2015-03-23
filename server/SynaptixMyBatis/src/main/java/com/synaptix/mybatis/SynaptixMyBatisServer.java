package com.synaptix.mybatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import com.google.inject.Inject;
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.listener.IEntitySaveOrUpdateListener;

public class SynaptixMyBatisServer {

	private static final Log LOG = LogFactory.getLog(SynaptixMyBatisServer.class);

	@Inject
	private Configuration configuration;

	@Inject(optional = true)
	private Set<TypeHandler<?>> typeHandlers;

	@Inject(optional = true)
	private Map<Class<? extends IEntity>, IEntitySaveOrUpdateListener<?>> entitySaveListenerMap;

	@Inject
	private IDaoSession daoSession;

	@Inject(optional = true)
	private Map<Integer, Class<?>> mapperMap;

	@Inject
	public SynaptixMyBatisServer() {
		super();
	}

	public void start() {
		LOG.info("Start SynaptixMyBatisServer");

		if (typeHandlers != null && !typeHandlers.isEmpty()) {
			for (TypeHandler<?> typeHandler : typeHandlers) {
				LOG.info("Add type handler " + typeHandler);
				configuration.getTypeHandlerRegistry().register(typeHandler);
			}
		}

		if (mapperMap != null && !mapperMap.isEmpty()) {
			List<Integer> ls = new ArrayList<Integer>(mapperMap.keySet());
			Collections.sort(ls);
			for (Integer l : ls) {
				Class<?> mapper = mapperMap.get(l);
				LOG.info("Add mapper for " + mapper);
				configuration.addMapper(mapper);
			}
		}

		if (entitySaveListenerMap != null && !entitySaveListenerMap.isEmpty()) {
			for (Entry<Class<? extends IEntity>, IEntitySaveOrUpdateListener<?>> entry : entitySaveListenerMap.entrySet()) {
				addEntitySaveListener(entry.getKey(), entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends IEntity> void addEntitySaveListener(Class<? extends IEntity> entityClass, IEntitySaveOrUpdateListener<?> entitySaveOrUpdateListener) {
		daoSession.addEntitySaveOrUpdateListener((Class<E>) entityClass, (IEntitySaveOrUpdateListener<E>) entitySaveOrUpdateListener);
	}

	public void stop() {
		LOG.info("Stop SynaptixMyBatisServer");
	}
}
