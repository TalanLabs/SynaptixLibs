package com.synaptix.deployer.callback;

import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.model.StepEnum;
import com.synaptix.service.IServiceCallback;

public interface ITrackingCallback extends IServiceCallback {

	/**
	 * Called when a task is done
	 * 
	 * @param instance
	 * @param stepEnum
	 */
	public void markDone(IEnvironmentInstance instance, StepEnum stepEnum);

	/**
	 * Called when a task is rejected
	 * 
	 * @param instance
	 * @param stepEnum
	 * @param cause
	 */
	public void markRejected(IEnvironmentInstance instance, StepEnum stepEnum, String cause);

	/**
	 * Called when a task is starting
	 * 
	 * @param instance
	 * @param stepEnum
	 */
	public void markPlay(IEnvironmentInstance instance, StepEnum stepEnum);

	/**
	 * Log some text
	 * 
	 * @param log
	 */
	public void log(String log);

}
