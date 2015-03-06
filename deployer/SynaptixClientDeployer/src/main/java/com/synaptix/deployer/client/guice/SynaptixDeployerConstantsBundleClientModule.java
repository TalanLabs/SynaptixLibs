package com.synaptix.deployer.client.guice;

import com.synaptix.deployer.client.message.CompareConstantsBundle;
import com.synaptix.deployer.client.message.DDLConstantsBundle;
import com.synaptix.deployer.client.message.DatabaseCheckerConstantsBundle;
import com.synaptix.deployer.client.message.DeployerConstantsBundle;
import com.synaptix.deployer.client.message.DeployerManagementConstantsBundle;
import com.synaptix.deployer.client.message.FileSystemSpaceConstantsBundle;
import com.synaptix.deployer.client.message.WatcherConstantsBundle;
import com.synaptix.guice.module.AbstractSynaptixConstantsBundleModule;
import com.synaptix.widget.guice.SwingConstantsBundleManager;

public class SynaptixDeployerConstantsBundleClientModule extends AbstractSynaptixConstantsBundleModule {

	public SynaptixDeployerConstantsBundleClientModule() {
		super(SwingConstantsBundleManager.class);
	}

	@Override
	protected void configure() {
		bindConstantsBundle(DeployerConstantsBundle.class);
		bindConstantsBundle(DeployerManagementConstantsBundle.class);
		bindConstantsBundle(WatcherConstantsBundle.class);
		bindConstantsBundle(CompareConstantsBundle.class);
		bindConstantsBundle(DDLConstantsBundle.class);
		bindConstantsBundle(FileSystemSpaceConstantsBundle.class);
		bindConstantsBundle(DatabaseCheckerConstantsBundle.class);
	}

}
