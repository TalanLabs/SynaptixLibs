package com.synaptix.deployer.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.helper.DatabaseHelper;
import com.synaptix.deployer.model.IConfig;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public class DeployerDaoSession implements IDeployerDaoSession {

	// private static final Log LOG = LogFactory.getLog(DeployerDaoSession.class);

	protected final Injector injector;

	protected final ThreadLocal<SqlSessionManager> sqlSessionManager = new ThreadLocal<SqlSessionManager>();

	@Inject
	public DeployerDaoSession(Injector injector) {
		super();

		this.injector = injector;
	}

	private void checkSqlSession() {
		if ((getSqlSessionManager() == null) || (!getSqlSessionManager().isManagedSessionStarted())) {
			throw new IllegalAccessError("Execute begin first");
		}
	}

	private SqlSessionManager getSqlSessionManager() {
		return sqlSessionManager.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin(ISynaptixDatabaseSchema database) {
		IConfig config = buildConfig(database, null, null);
		sqlSessionManager.set(SqlSessionManager.newInstance(DatabaseHelper.getInstance().getSqlSessionFactory(config, null)));
		if (getSqlSessionManager().isManagedSessionStarted()) {
			throw new IllegalAccessError("Begin already started");
		}
		getSqlSessionManager().startManagedSession(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin(ISynaptixDatabaseSchema database, String user, char[] password) {
		IConfig config = buildConfig(database, user, password);
		SqlSessionFactory sessionFactory = DatabaseHelper.getInstance().getSqlSessionFactory(config, null);
		sqlSessionManager.set(SqlSessionManager.newInstance(sessionFactory));
		if (getSqlSessionManager().isManagedSessionStarted()) {
			throw new IllegalAccessError("Begin already started");
		}
		getSqlSessionManager().startManagedSession(false);
	}

	private IConfig buildConfig(ISynaptixDatabaseSchema database, String user, char[] password) {
		IConfig config = ComponentFactory.getInstance().createInstance(IConfig.class);
		config.setEnvironment(database.getEnvironment());
		config.setUser(user);
		config.setPassword(password);
		return config;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		getSqlSession().close();
	}

	/**
	 * {@inheritDoc}
	 */
	private SqlSession getSqlSession() {
		checkSqlSession();
		return getSqlSessionManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getMapper(Class<T> type) {
		checkSqlSession();
		return getSqlSessionManager().getMapper(type);
	}
}
