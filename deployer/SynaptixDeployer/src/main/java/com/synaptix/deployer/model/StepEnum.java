package com.synaptix.deployer.model;

public enum StepEnum {

	STOP(1),
	/**
	 * 
	 */
	RENAME(2),
	/**
	 * 
	 */
	DOWNLOAD(3),
	/**
	 * 
	 */
	DATABASE(4),
	/**
	 * 
	 */
	LAUNCH(5);

	private int step;

	private StepEnum(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}
}
