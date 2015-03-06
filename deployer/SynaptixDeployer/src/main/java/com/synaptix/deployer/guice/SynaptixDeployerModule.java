package com.synaptix.deployer.guice;

import java.io.InputStream;

import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.synaptix.deployer.dao.DeployerDaoContext;
import com.synaptix.deployer.delegate.DeployerDelegate;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.deployer.service.IDeployerService;
import com.synaptix.deployer.service.server.DeployerServerService;
import com.synaptix.mybatis.guice.SynaptixMyBatisModule;
import com.synaptix.server.service.guice.AbstractSynaptixServerServiceModule;

public class SynaptixDeployerModule extends AbstractSynaptixServerServiceModule {

	private String configFile;

	public SynaptixDeployerModule(String configFile) {
		super();

		this.configFile = configFile;
	}

	@Override
	protected void configure() {
		InputStream configInputStream = SynaptixDeployerModule.class.getResourceAsStream(configFile);
		install(Modules.override(new SynaptixMyBatisModule(configInputStream, DeployerDaoContext.class)).with(new SynaptixDeployerMyBatisModule(configFile)));

		bindService(DeployerServerService.class).with(IDeployerService.class);

		bindDelegate(DeployerDelegate.class);

		bind(IMailConfig.class).toProvider(MailConfigProvider.class).in(Singleton.class);
	}
}
