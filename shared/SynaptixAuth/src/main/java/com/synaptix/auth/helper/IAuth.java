package com.synaptix.auth.helper;

import com.synaptix.auth.AuthsBundleManager;

public interface IAuth {

	/**
	 * Has authorization
	 * 
	 * @param authsBundleManager
	 * @return
	 */
	public boolean hasAuth(AuthsBundleManager authsBundleManager);

}
