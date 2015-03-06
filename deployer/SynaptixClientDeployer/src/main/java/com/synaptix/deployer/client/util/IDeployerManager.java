package com.synaptix.deployer.client.util;

import java.util.List;

import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public interface IDeployerManager {

	/**
	 * Get the list of supported environments
	 * 
	 * @return
	 */
	public List<ISynaptixEnvironment> getSupportedEnvironmentList();

	/**
	 * Get the list of supported jobs
	 * 
	 * @return
	 */
	public List<ISynaptixJob> getSupportedJobList();

	/**
	 * Get the list of supported databases
	 * 
	 * @return
	 */
	public List<ISynaptixDatabaseSchema> getSupportedDb();

	/**
	 * Get a list of supported db for an environment
	 * 
	 * @param synaptixEnvironment
	 * @return
	 */
	public List<ISynaptixDatabaseSchema> getSupportedDbsForEnvironment(ISynaptixEnvironment synaptixEnvironment);

}
