package com.synaptix.deployer.client.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.deployer.client.controller.IDeployerContext;
import com.synaptix.deployer.client.util.DeployerManager;
import com.synaptix.deployer.client.view.ISynaptixDeployerWindow;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.SynaptixDatabase;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.core.controller.IController;

public class SynaptixDeployerContext implements IDeployerContext {

	private final DeployerManager deployerManager;

	private List<IDeployerContextInitialize> deployerContextInitializes;

	@Inject
	private Provider<ISynaptixDeployerWindow> frontendViewProvider;

	private ISynaptixDeployerWindow window;

	private List<IController> controllers = new ArrayList<IController>();

	@Inject
	private Set<ISynaptixEnvironment> synaptixEnvironmentSet;

	@Inject
	private Set<ISynaptixJob> synaptixJobSet;

	@Inject
	private Set<SynaptixDatabase> dbSet;

	@Inject
	public SynaptixDeployerContext(DeployerManager deployerManager) {
		super();

		this.deployerManager = deployerManager;

		initialize();
	}

	@Inject
	public void setServiceFactory(IServiceFactory serviceFactory) {
		ServicesManager.getInstance().addServiceFactory("deployer", serviceFactory);
	}

	private void initialize() {
	}

	private void fillDeployerManagerList() {
		if (CollectionHelper.isNotEmpty(synaptixEnvironmentSet)) {
			for (ISynaptixEnvironment synaptixEnvironment : synaptixEnvironmentSet) {
				deployerManager.addEnvironment(synaptixEnvironment);
			}
		}
		if (CollectionHelper.isNotEmpty(synaptixJobSet)) {
			for (ISynaptixJob synaptixJob : synaptixJobSet) {
				deployerManager.addJob(synaptixJob);
			}
		}
		if (CollectionHelper.isNotEmpty(dbSet)) {
			for (SynaptixDatabase db : dbSet) {
				deployerManager.addDatabases(db.getDbs());
			}
		}
		// for (final Binding<?> binding : injector.getBindings().values()) {
		// final Type type = binding.getKey().getTypeLiteral().getType();
		// if (type instanceof Class) {
		// final Class<?> beanClass = (Class<?>) type;
		// if (ISynaptixEnvironment.class.isAssignableFrom(beanClass)) {
		// deployerManager.addEnvironment((ISynaptixEnvironment) injector.getInstance(beanClass));
		// } else if (ISynaptixJob.class.isAssignableFrom(beanClass)) {
		// deployerManager.addJob((ISynaptixJob) injector.getInstance(beanClass));
		// } else if (MainDatabase.class.isAssignableFrom(beanClass)) {
		// deployerManager.addDatabases(((MainDatabase) injector.getInstance(beanClass)).getDbs());
		// }
		// }
		// }
	}

	public void setDeployerContextInitializes(List<IDeployerContextInitialize> deployerContextInitializes) {
		this.deployerContextInitializes = deployerContextInitializes;
	}

	@Override
	public void registerController(IController controller) {
		controllers.add(controller);

		window.registerView(controller.getView());
	}

	public void start() {

		fillDeployerManagerList();

		window = frontendViewProvider.get();
		window.setSynaptixDeployerContext(this);

		window.setTitleSuffix("Deployer");

		window.start();

		if (deployerContextInitializes != null) {
			for (IDeployerContextInitialize deployerContextInitialize : deployerContextInitializes) {
				deployerContextInitialize.initializeDeployerContext(SynaptixDeployerContext.this);
			}
		}

		window.builds();

		for (IController controller : controllers) {
			controller.start();
		}
	}

	/**
	 * Exit application
	 */
	public void stop() {
		boolean allOK = true;
		for (IController controller : controllers) {
			boolean stop = controller.stop();
			allOK = allOK && stop;
		}
		if (!allOK) {
			return;
		}
		System.exit(-1);
	}
}
