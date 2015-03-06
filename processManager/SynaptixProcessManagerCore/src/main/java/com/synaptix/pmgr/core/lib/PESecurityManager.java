package com.synaptix.pmgr.core.lib;

import java.io.FileDescriptor;
import java.rmi.RMISecurityManager;
import java.security.Permission;

public class PESecurityManager extends RMISecurityManager {
	public void checkAccess(Thread t) {
	}

	public void checkAccess(ThreadGroup g) {
	}

	public void checkExit(int status) {
	}

	public void checkPropertiesAccess() {
	}

	public void checkAccept(String host, int port) {
	}

	public void checkConnect(String host, int port) {
	}

	public void checkConnect(String host, int port, Object contect) {
	}

	public boolean checkTopLevelWindow(Object window) {
		return true;
	}

	public void checkPackageAccess(String pkg) {
	}

	public void checkAwtEventQueueAccess() {
	}

	public void checkRead(FileDescriptor fd) {
	}

	public void checkRead(String file) {
	}

	public void createClassLoader() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	public void checkPermission(Permission arg0) {
	}
}