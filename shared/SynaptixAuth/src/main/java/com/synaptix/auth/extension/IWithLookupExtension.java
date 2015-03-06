package com.synaptix.auth.extension;

public interface IWithLookupExtension {

	/**
	 * Has auth for object and action
	 * 
	 * @param object
	 * @param action
	 * @return
	 */
	public boolean hasAuth(String object, String action);

}
