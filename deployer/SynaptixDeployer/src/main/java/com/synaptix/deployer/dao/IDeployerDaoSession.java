package com.synaptix.deployer.dao;

import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public interface IDeployerDaoSession {

	public <T> T getMapper(Class<T> type);

	public void begin(ISynaptixDatabaseSchema database);

	public void begin(ISynaptixDatabaseSchema database, String user, char[] password);

	public void end();

}
