package com.synaptix.taskmanager.manager;

import de.jkeylockmanager.manager.KeyLockManager;
import de.jkeylockmanager.manager.KeyLockManagers;

public class TaskServiceLockManagerProvider {

	private KeyLockManager keyLockManager;

	public KeyLockManager get() {
		if (keyLockManager == null) {
			keyLockManager = KeyLockManagers.newLock();
		}
		return keyLockManager;
	}

}
