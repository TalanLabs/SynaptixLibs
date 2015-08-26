package com.synaptix.taskmanager.controller.helper;

public interface INlsMessageExtendedContext {

	/**
	 * Has export nls meanings
	 * 
	 * @return
	 */
	public boolean hasAuthChangeNlsMeanings();

	/**
	 * Export nls meanings
	 * 
	 */
	public void exportNlsMeanings();

	/**
	 * Import nls meanings
	 * 
	 */
	public void importNlsMeanings();
}
