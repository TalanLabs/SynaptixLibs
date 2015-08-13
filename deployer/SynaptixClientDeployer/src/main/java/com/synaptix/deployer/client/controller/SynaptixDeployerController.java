package com.synaptix.deployer.client.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Provider;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.SynaptixDeployer;
import com.synaptix.deployer.client.core.IDeployerContextInitialize;
import com.synaptix.deployer.client.util.IDeployerManager;
import com.synaptix.deployer.client.util.IShellDeployerManager;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.widget.core.controller.AbstractController;

public class SynaptixDeployerController extends AbstractController implements IDeployerContextInitialize {

	private static final Log LOG = LogFactory.getLog(SynaptixDeployerController.class);

	private ISynaptixDeployerViewFactory viewFactory;

	private IDeployerManager environmentManager;

	private WatcherController watcher;

	private IShellDeployerManager shellDeployerManager;

	private IMailConfig mailConfig;

	@Inject
	private Provider<DatabaseCheckerController> databaseCheckerControllerProvider;

	@Inject
	public SynaptixDeployerController(ISynaptixDeployerViewFactory viewFactory) {
		super();

		this.viewFactory = viewFactory;
	}

	@Inject
	public void setShellDeployerManager(IShellDeployerManager shellDeployerManager) {
		this.shellDeployerManager = shellDeployerManager;
	}

	@Inject
	public void setEnvironmentManager(IDeployerManager environmentManager) {
		this.environmentManager = environmentManager;
	}

	@Inject
	public void setMailConfig(IMailConfig mailConfig) {
		this.mailConfig = mailConfig;

		if (mailConfig != null) {
			try {
				File tempFile = File.createTempFile("tempFile", null);
				File propertyFile = new File(tempFile.getParent(), "deployerMail.properties");
				if (!propertyFile.exists()) {
					propertyFile.createNewFile();
				}
				Properties p = new Properties();
				IMailConfig mailConfigToSave = ComponentFactory.getInstance().createInstance(IMailConfig.class);
				mailConfigToSave.straightSetProperties(mailConfig.straightGetProperties());
				String[] defaultReceivers = mailConfigToSave.getDefaultReceivers();
				mailConfigToSave.setDefaultReceivers(null);
				for (Entry<String, Object> entry : mailConfigToSave.straightGetProperties().entrySet()) {
					if (entry.getValue() != null) {
						if (char[].class.isAssignableFrom(entry.getValue().getClass())) {
							p.put(entry.getKey(), new String((char[]) entry.getValue()));
						} else {
							p.put(entry.getKey(), entry.getValue().toString());
						}
					}
				}
				if (defaultReceivers != null) {
					StringBuilder receivers = new StringBuilder();
					boolean first = true;
					for (String receiver : defaultReceivers) {
						if (!first) {
							receivers.append(",");
						}
						receivers.append(receiver.trim());
						first = false;
					}
					p.put("defaultReceivers", receivers.toString());
				}
				FileOutputStream out = new FileOutputStream(propertyFile);
				p.store(out, null);
				out.close();
			} catch (IOException e) {
				LOG.error(e, e);
			}
		}
	}

	@Override
	public void initializeDeployerContext(IDeployerContext deployerContext) {
		deployerContext.registerController(this);

		deployerContext.registerController(new DeployerManagementController(viewFactory, this));

		watcher = new WatcherController(viewFactory, this);
		deployerContext.registerController(watcher);

		deployerContext.registerController(new FileSystemSpaceController(viewFactory, this));

		deployerContext.registerController(new CompareController(viewFactory, this));

		deployerContext.registerController(new DDLController(viewFactory, this));

		DatabaseCheckerController controller = databaseCheckerControllerProvider.get();
		controller.initialize();
		deployerContext.registerController(controller);
	}

	public List<ISynaptixEnvironment> getSupportedEnvironments() {
		return environmentManager.getSupportedEnvironmentList();
	}

	public List<ISynaptixJob> getSupportedJobs() {
		return environmentManager.getSupportedJobList();
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbs() {
		return environmentManager.getSupportedDb();
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbsForEnvironment(ISynaptixEnvironment synaptixEnvironment) {
		return environmentManager.getSupportedDbsForEnvironment(synaptixEnvironment);
	}

	public void watchEnvironment(ISynaptixEnvironment environment, IEnvironmentInstance instance) {
		watcher.setEnvironment(environment, instance);
	}

	public SynaptixDeployer getShellDeployer(ISynaptixEnvironment environment) {
		if (shellDeployerManager == null) {
			LOG.error("There is no standard shell deployer manager binded. Bind one to deploy.");
			return null;
		} else {
			return shellDeployerManager.getShellDeployer(environment);
		}
	}

	public IMailConfig getMailConfig() {
		return mailConfig;
	}
}
