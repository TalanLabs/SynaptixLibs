package com.synaptix.deployer.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public class DeployerManager implements IDeployerManager {

	private final List<ISynaptixEnvironment> supportedEnvironmentList;

	private final List<ISynaptixJob> supportedJobList;

	private final List<ISynaptixDatabaseSchema> supportedDatabaseList;

	@Inject
	private Map<Class<? extends ISynaptixEnvironment>, List<ISynaptixDatabaseSchema>> environmentDatabase;

	@Inject
	public DeployerManager() {
		super();

		this.supportedEnvironmentList = new ArrayList<ISynaptixEnvironment>();
		this.supportedJobList = new ArrayList<ISynaptixJob>();
		this.supportedDatabaseList = new ArrayList<ISynaptixDatabaseSchema>();
	}

	@Override
	public List<ISynaptixEnvironment> getSupportedEnvironmentList() {
		return Collections.unmodifiableList(supportedEnvironmentList);
	}

	@Override
	public List<ISynaptixJob> getSupportedJobList() {
		return Collections.unmodifiableList(supportedJobList);
	}

	@Override
	public List<ISynaptixDatabaseSchema> getSupportedDb() {
		return Collections.unmodifiableList(supportedDatabaseList);
	}

	@Override
	public List<ISynaptixDatabaseSchema> getSupportedDbsForEnvironment(ISynaptixEnvironment synaptixEnvironment) {
		if (synaptixEnvironment != null) {
			List<ISynaptixDatabaseSchema> dbSchemaList = environmentDatabase.get(synaptixEnvironment.getClass());
			if (dbSchemaList != null) {
				return Collections.unmodifiableList(dbSchemaList);
			}
		}
		return null;
	}

	public void addEnvironment(ISynaptixEnvironment environment) {
		supportedEnvironmentList.add(environment);
	}

	public void addJob(ISynaptixJob job) {
		supportedJobList.add(job);
	}

	public void addDatabases(List<ISynaptixDatabaseSchema> dbs) {
		supportedDatabaseList.addAll(dbs);
	}
}
