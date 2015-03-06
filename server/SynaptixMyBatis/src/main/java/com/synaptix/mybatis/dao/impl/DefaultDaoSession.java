package com.synaptix.mybatis.dao.impl;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.IdRaw;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.dao.IDaoSessionExt;
import com.synaptix.mybatis.dao.IDaoUserContext;
import com.synaptix.mybatis.dao.IGUIDGenerator;
import com.synaptix.mybatis.dao.exceptions.BeginAlreadyStartedDaoException;
import com.synaptix.mybatis.dao.exceptions.NotBeginStartedDaoException;
import com.synaptix.mybatis.dao.exceptions.VersionConflictDaoException;
import com.synaptix.mybatis.dao.listener.IEntitySaveOrUpdateListener;
import com.synaptix.mybatis.dao.mapper.EntitySql;
import com.synaptix.mybatis.dao.mapper.NlsServerMessageMapper;
import com.synaptix.mybatis.dao.mapper.TempUserSessionMapper;
import com.synaptix.mybatis.helper.ComponentColumnsCache;
import com.synaptix.mybatis.helper.ComponentSqlHelper;

public class DefaultDaoSession implements IDaoSessionExt {

	private static final Log LOG = LogFactory.getLog(DefaultDaoSession.class);

	private final Map<Class<? extends IEntity>, List<IEntitySaveOrUpdateListener<? extends IEntity>>> entitySaveOrUpdateListenerMap;

	@Inject
	protected Injector injector;

	@Inject
	protected SqlSessionManager sqlSessionManager;

	@Inject
	protected IGUIDGenerator guidGenerator;

	@Inject
	protected IDaoUserContext userContext;

	@Inject
	protected MapperCacheLocal mapperCacheLocal;

	@Inject
	protected EntitySql entitySql;

	@Inject
	protected ComponentSqlHelper componentSqlHelper;

	@Inject
	protected ComponentColumnsCache componentColumnsCache;

	protected final Locale defaultMeaningLocale;

	protected final String defaultMeaningLanguage;

	protected boolean setUserInSession;

	private boolean checkDefaultVersionConflictDaoExceptionInSession;

	private final ThreadLocal<SessionInfo> sessionInfoThreadLocal = new ThreadLocal<SessionInfo>();

	@Inject
	public DefaultDaoSession(@Named("defaultMeaningLocale") Locale defaultMeaningLocale) {
		super();

		this.defaultMeaningLocale = defaultMeaningLocale;

		String defaultLanguage = null;
		if (defaultMeaningLocale != null) {
			try {
				defaultLanguage = defaultMeaningLocale.getISO3Language();
			} catch (MissingResourceException e) {
			}
		}
		this.defaultMeaningLanguage = defaultLanguage;

		this.setUserInSession = true;
		this.checkDefaultVersionConflictDaoExceptionInSession = false;

		this.entitySaveOrUpdateListenerMap = new HashMap<Class<? extends IEntity>, List<IEntitySaveOrUpdateListener<?>>>();
	}

	public void setSetUserInSession(boolean setUserInSession) {
		this.setUserInSession = setUserInSession;
	}

	public boolean isSetUserInSession() {
		return setUserInSession;
	}

	public void setCheckDefaultVersionConflictDaoExceptionInSession(boolean checkDefaultVersionConflictDaoExceptionInSession) {
		this.checkDefaultVersionConflictDaoExceptionInSession = checkDefaultVersionConflictDaoExceptionInSession;
	}

	public boolean isCheckDefaultVersionConflictDaoExceptionInSession() {
		return checkDefaultVersionConflictDaoExceptionInSession;
	}

	protected TempUserSessionMapper getTempUserSessionMapper() {
		return getMapper(TempUserSessionMapper.class);
	}

	protected NlsServerMessageMapper getNlsServerMessageMapper() {
		return getMapper(NlsServerMessageMapper.class);
	}

	private void checkSqlSession() {
		if (!sqlSessionManager.isManagedSessionStarted()) {
			throw new NotBeginStartedDaoException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin() {
		SessionInfo sessionInfo = getSessionInfo();
		if (sessionInfo != null && sessionInfo.superTransaction != null) {
			try {
				SavepointInfo savepointInfo = new SavepointInfo();
				Savepoint sp = sqlSessionManager.getConnection().setSavepoint();
				savepointInfo.savepoint = sp;
				savepointInfo.commit = false;
				sessionInfo.superTransaction.savepointInfos.add(savepointInfo);

				sessionInfo.superTransaction.numBegin++;
			} catch (SQLException e) {
				throw ExceptionFactory.wrapException(e.getMessage(), e);
			}
		} else {
			if (sqlSessionManager.isManagedSessionStarted()) {
				throw new IllegalAccessError("Begin already started");
			}

			sqlSessionManager.startManagedSession(false);

			if (sessionInfo != null && sessionInfo.checkSuperTransaction) {
				try {
					sessionInfo.superTransaction = new SuperTransaction();
					sessionInfo.superTransaction.numBegin = 0;
					sessionInfo.superTransaction.savepointInfos = new ArrayList<SavepointInfo>();

					SavepointInfo savepointInfo = new SavepointInfo();
					Savepoint sp = sqlSessionManager.getConnection().setSavepoint();
					savepointInfo.savepoint = sp;
					savepointInfo.commit = false;
					sessionInfo.superTransaction.savepointInfos.add(savepointInfo);
				} catch (SQLException e) {
					throw ExceptionFactory.wrapException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit() {
		SessionInfo sessionInfo = getSessionInfo();
		if (sessionInfo != null && sessionInfo.superTransaction != null) {
			SavepointInfo savepointInfo = sessionInfo.superTransaction.savepointInfos.get(sessionInfo.superTransaction.numBegin);
			savepointInfo.commit = true;
		}
		if (sessionInfo == null || sessionInfo.superTransaction == null || sessionInfo.superTransaction.numBegin == 0) {
			getSqlSession().commit();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		SessionInfo sessionInfo = getSessionInfo();

		if (sessionInfo != null && sessionInfo.superTransaction != null) {
			try {
				SavepointInfo savepointInfo = sessionInfo.superTransaction.savepointInfos.get(sessionInfo.superTransaction.numBegin);
				if (savepointInfo.commit) {
					// sqlSessionManager.getConnection().releaseSavepoint(savepointInfo.savepoint);
				} else {
					sqlSessionManager.getConnection().rollback(savepointInfo.savepoint);
					sqlSessionManager.clearCache();
				}
			} catch (SQLException e) {
				throw ExceptionFactory.wrapException(e.getMessage(), e);
			}
			sessionInfo.superTransaction.savepointInfos.remove(sessionInfo.superTransaction.numBegin);

		}

		if (sessionInfo == null || sessionInfo.superTransaction == null || sessionInfo.superTransaction.numBegin == 0) {
			getSqlSession().close();
			mapperCacheLocal.remove();

			if (sessionInfo != null) {
				initSessionInfo(sessionInfo);
			}
		} else {
			sessionInfo.superTransaction.numBegin--;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SqlSession getSqlSession() {
		checkSqlSession();
		return sqlSessionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getMapper(Class<T> type) {
		checkSqlSession();
		T t = mapperCacheLocal.getMapper(type);
		if (t == null) {
			t = sqlSessionManager.getMapper(type);
			mapperCacheLocal.putMapper(type, t);
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends IEntity> int saveEntity(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity is null");
		}
		entity.setId(newId());
		LOG.debug("Create GUID : " + entity.getId());
		entity.setVersion(0);
		if (entity instanceof ITracable) {
			ITracable t = (ITracable) entity;
			t.setCreatedBy(userContext.getCurrentLogin());
			t.setCreatedDate(new Date());
			t.setUpdatedBy(null);
			t.setUpdatedDate(null);
		}

		String language = null;
		if (userContext.getCurrentLocale() != null) {
			try {
				language = userContext.getCurrentLocale().getISO3Language();
			} catch (MissingResourceException e) {
			}
		}

		saveNlsMessage(entity, language);

		fireBeforeSaveEntity(entity);
		int res = entitySql.insertEntity(entity);
		fireAfterSaveEntity(entity);
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends IEntity> int saveOrUpdateEntity(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity is null");
		}
		if (entity.getId() == null) {
			return saveEntity(entity);
		}
		if (entity instanceof ITracable) {
			ITracable t = (ITracable) entity;
			t.setUpdatedBy(userContext.getCurrentLogin());
			t.setUpdatedDate(new Date());
		}

		String language = null;
		if (userContext.getCurrentLocale() != null) {
			try {
				language = userContext.getCurrentLocale().getISO3Language();
			} catch (MissingResourceException e) {
			}
		}

		saveNlsMessage(entity, language);

		fireBeforeUpdateEntity(entity);
		int res = entitySql.updateEntity(entity, !isSetUserInSession() || (language != null && defaultMeaningLanguage != null && language.equals(defaultMeaningLanguage)));
		if (res == 0 && getSessionInfo().checkVersionConflictDaoExceptionInSession) {
			throw new VersionConflictDaoException();
		}
		if (res != 0) {
			entity.setVersion(entity.getVersion() + 1);
			fireAfterUpdateEntity(entity);
		}
		return res;
	}

	private <T extends IEntity> void saveNlsMessage(T entity, String language) {
		if (isSetUserInSession()) {
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(entity);
			if (componentColumnsCache.isUseNls(cd.getComponentClass())) {
				for (String propertyName : componentColumnsCache.getNlsPropertyNames(cd.getComponentClass())) {
					PropertyDescriptor pd = cd.getPropertyDescriptor(propertyName);
					DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
					if (dp != null && dp.getNlsColumn() != null) {
						DatabasePropertyExtensionDescriptor.NlsColumn nlsMeaning = dp.getNlsColumn();

						getNlsServerMessageMapper().mergeNlsServerMessage(componentSqlHelper.getSqlTableName(cd), entity.getId(), language, nlsMeaning.getSqlName(),
								(String) entity.straightGetProperty(pd.getPropertyName()));
					}
				}
			}
		}
	}

	@Override
	public <T extends IEntity> int deleteEntity(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity is null");
		}
		deleteNlsMessages(entity);
		int res = entitySql.deleteEntity(entity);
		if (res == 0 && getSessionInfo().checkVersionConflictDaoExceptionInSession) {
			throw new VersionConflictDaoException();
		}
		return res;
	}

	private <T extends IEntity> void deleteNlsMessages(T entity) {
		if (isSetUserInSession()) {
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(entity);
			if (componentColumnsCache.isUseNls(cd.getComponentClass())) {
				getNlsServerMessageMapper().deleteNlsServerMessages(componentSqlHelper.getSqlTableName(cd), entity.getId());
			}
		}
	}

	@Override
	public <T extends IEntity & ICancellable> int cancelEntity(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity is null");
		}
		entity.setCheckCancel(true);
		entity.setCancelBy(userContext.getCurrentLogin());
		entity.setCancelDate(new Date());
		return saveOrUpdateEntity(entity);
	}

	@Override
	public IId newId() {
		return new IdRaw(guidGenerator.newGUID());
	}

	@Override
	public EntitySql getEntitySql() {
		return entitySql;
	}

	@Override
	public void setCheckSuperTransactionInSession(boolean checkSuperTransaction) {
		if (!sqlSessionManager.isManagedSessionStarted()) {
			getSessionInfo().checkSuperTransaction = checkSuperTransaction;
		} else {
			throw new BeginAlreadyStartedDaoException();
		}
	}

	@Override
	public boolean isCheckSuperTransactionInSession() {
		return getSessionInfo().checkSuperTransaction;
	}

	@Override
	public void setCheckVersionConflictDaoExceptionInSession(boolean checkVersionConflictDaoExceptionInSession) {
		if (!sqlSessionManager.isManagedSessionStarted()) {
			getSessionInfo().checkVersionConflictDaoExceptionInSession = checkVersionConflictDaoExceptionInSession;
		} else {
			throw new BeginAlreadyStartedDaoException();
		}
	}

	@Override
	public boolean isCheckThrowExceptionForNoDataInSession() {
		return getSessionInfo().checkVersionConflictDaoExceptionInSession;
	}

	@Override
	public final <T extends IEntity> void addEntitySaveOrUpdateListener(Class<T> entityClass, IEntitySaveOrUpdateListener<T> entitySaveOrUpdateListener) {
		List<IEntitySaveOrUpdateListener<? extends IEntity>> entitySaveListenerList = entitySaveOrUpdateListenerMap.get(entityClass);
		if (entitySaveListenerList == null) {
			entitySaveListenerList = new ArrayList<IEntitySaveOrUpdateListener<? extends IEntity>>();
			entitySaveOrUpdateListenerMap.put(entityClass, entitySaveListenerList);
		}
		entitySaveListenerList.add(entitySaveOrUpdateListener);
	}

	@SuppressWarnings("unchecked")
	private final <T extends IEntity> void fireBeforeSaveEntity(T entity) {
		if (entitySaveOrUpdateListenerMap != null) {
			Class<T> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
			List<IEntitySaveOrUpdateListener<? extends IEntity>> entityListenerList = entitySaveOrUpdateListenerMap.get(entityClass);
			if (entityListenerList != null) {
				for (IEntitySaveOrUpdateListener<?> entitySaveListener : entityListenerList) {
					((IEntitySaveOrUpdateListener<T>) entitySaveListener).beforeSave(entity);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final <T extends IEntity> void fireBeforeUpdateEntity(T entity) {
		if (entitySaveOrUpdateListenerMap != null) {
			Class<T> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
			List<IEntitySaveOrUpdateListener<? extends IEntity>> entityListenerList = entitySaveOrUpdateListenerMap.get(entityClass);
			if (entityListenerList != null) {
				for (IEntitySaveOrUpdateListener<?> entityUpdateListener : entityListenerList) {
					((IEntitySaveOrUpdateListener<T>) entityUpdateListener).beforeUpdate(entity);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final <T extends IEntity> void fireAfterSaveEntity(T entity) {
		if (entitySaveOrUpdateListenerMap != null) {
			Class<T> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
			List<IEntitySaveOrUpdateListener<? extends IEntity>> entityListenerList = entitySaveOrUpdateListenerMap.get(entityClass);
			if (entityListenerList != null) {
				for (IEntitySaveOrUpdateListener<?> entitySaveListener : entityListenerList) {
					((IEntitySaveOrUpdateListener<T>) entitySaveListener).afterSave(entity);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private final <T extends IEntity> void fireAfterUpdateEntity(T entity) {
		if (entitySaveOrUpdateListenerMap != null) {
			Class<T> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
			List<IEntitySaveOrUpdateListener<? extends IEntity>> entityListenerList = entitySaveOrUpdateListenerMap.get(entityClass);
			if (entityListenerList != null) {
				for (IEntitySaveOrUpdateListener<?> entityUpdateListener : entityListenerList) {
					((IEntitySaveOrUpdateListener<T>) entityUpdateListener).afterUpdate(entity);
				}
			}
		}
	}

	private SessionInfo getSessionInfo() {
		SessionInfo sessionInfo = sessionInfoThreadLocal.get();
		if (sessionInfo == null) {
			sessionInfo = new SessionInfo();
			initSessionInfo(sessionInfo);
			sessionInfoThreadLocal.set(sessionInfo);
		}
		return sessionInfo;
	}

	private void initSessionInfo(SessionInfo sessionInfo) {
		sessionInfo.checkSuperTransaction = false;
		sessionInfo.checkVersionConflictDaoExceptionInSession = checkDefaultVersionConflictDaoExceptionInSession;
		sessionInfo.superTransaction = null;
	}

	private class SessionInfo {

		SuperTransaction superTransaction;

		boolean checkVersionConflictDaoExceptionInSession;

		boolean checkSuperTransaction;

	}

	private class SuperTransaction {

		List<SavepointInfo> savepointInfos;

		int numBegin;

	}

	private class SavepointInfo {

		Savepoint savepoint;

		boolean commit;
	}
}
