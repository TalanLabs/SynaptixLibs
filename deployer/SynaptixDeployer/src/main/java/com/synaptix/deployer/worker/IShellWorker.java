package com.synaptix.deployer.worker;

public interface IShellWorker {

	/**
	 * Force end
	 */
	public void stop();

	/**
	 * Wait for the end of execution<br>
	 * Returns the result of the work method
	 */
	public String synchronize();

}
