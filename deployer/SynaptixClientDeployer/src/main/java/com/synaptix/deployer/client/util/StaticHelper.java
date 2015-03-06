package com.synaptix.deployer.client.util;

import com.synaptix.deployer.client.message.CompareConstantsBundle;
import com.synaptix.deployer.client.message.DDLConstantsBundle;
import com.synaptix.deployer.client.message.DatabaseCheckerConstantsBundle;
import com.synaptix.deployer.client.message.DeployerConstantsBundle;
import com.synaptix.deployer.client.message.DeployerManagementConstantsBundle;
import com.synaptix.deployer.client.message.FileSystemSpaceConstantsBundle;
import com.synaptix.deployer.client.message.WatcherConstantsBundle;
import com.synaptix.widget.util.AbstractStaticHelper;

public class StaticHelper extends AbstractStaticHelper {

	public static DeployerConstantsBundle getDeployerConstantsBundle() {
		return getConstantsBundleManager().getBundle(DeployerConstantsBundle.class);
	}

	public static DeployerManagementConstantsBundle getDeployerManagementConstantsBundle() {
		return getConstantsBundleManager().getBundle(DeployerManagementConstantsBundle.class);
	}

	public static CompareConstantsBundle getCompareConstantsBundle() {
		return getConstantsBundleManager().getBundle(CompareConstantsBundle.class);
	}

	public static WatcherConstantsBundle getWatcherConstantsBundle() {
		return getConstantsBundleManager().getBundle(WatcherConstantsBundle.class);
	}

	public static DDLConstantsBundle getDDLConstantsBundle() {
		return getConstantsBundleManager().getBundle(DDLConstantsBundle.class);
	}

	public static FileSystemSpaceConstantsBundle getFileSystemSpaceConstantsBundle() {
		return getConstantsBundleManager().getBundle(FileSystemSpaceConstantsBundle.class);
	}

	public static DatabaseCheckerConstantsBundle getDatabaseCheckerConstantsBundle() {
		return getConstantsBundleManager().getBundle(DatabaseCheckerConstantsBundle.class);
	}
}
