package com.synaptix.taskmanager.model.domains;

/**
 * The todo will first be created for the executant.
 * After some time (can be configured in the application) an other todo for the same task will be created for the manager.
 */
public enum TodoOwner {
	MANAGER,
	EXECUTANT
}
