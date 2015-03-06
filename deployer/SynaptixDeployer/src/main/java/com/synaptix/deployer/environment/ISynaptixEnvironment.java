package com.synaptix.deployer.environment;

import java.util.List;

public interface ISynaptixEnvironment {

	/**
	 * Get the name of the environment
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get the ip of the machine
	 * 
	 * @return
	 */
	public String getIp();

	/**
	 * Get the user
	 * 
	 * @return
	 */
	public String getLogin();

	/**
	 * Get the password
	 * 
	 * @return
	 */
	public String getPassword();

	/**
	 * Get list of instances (example: jboss instances)
	 * 
	 * @return
	 */
	public List<IEnvironmentInstance> getInstances();

	/**
	 * Can deploy on this environment or not
	 * 
	 * @return
	 */
	public boolean isReadOnly();

	/**
	 * Get environment home
	 * 
	 * @return
	 */
	public String getHome();

	/**
	 * Get the SQL Plus path
	 * 
	 * @return
	 */
	public String getSqlPlusPath();

}
