package com.synaptix.deployer.client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.util.IOUtils;

import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.deployer.SynaptixDeployer;
import com.synaptix.deployer.callback.ITrackingCallback;
import com.synaptix.deployer.client.deployerManagement.BuildNode;
import com.synaptix.deployer.client.deployerManagement.EnvironmentNode;
import com.synaptix.deployer.client.deployerManagement.InstructionsNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode.NodeType;
import com.synaptix.deployer.client.deployerManagement.PlayScriptsNode;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IDeployerManagementView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.instructions.SynaptixInstructions;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.IEnvironmentState;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.StepEnum;
import com.synaptix.sender.mail.JavaxMailFactory;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class DeployerManagementController extends AbstractController {

	private ISynaptixDeployerViewFactory viewFactory;

	private IDeployerManagementView deployerManagementView;

	private SynaptixDeployerController synaptixDeployerController;

	private ITrackingCallback trackingCallback;

	private ISynaptixEnvironment environment;

	private SynaptixInstructions instructions;

	private List<File> scripts;

	private boolean running;

	private char[] dbPassword;

	private ISynaptixDatabaseSchema db;

	public DeployerManagementController(ISynaptixDeployerViewFactory viewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.viewFactory = viewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		initialize();
	}

	private void initialize() {
		this.deployerManagementView = viewFactory.createDeployerManagementView(this);

		trackingCallback = new ITrackingCallback() {

			@Override
			public void markDone(IEnvironmentInstance instance, StepEnum stepEnum) {
				deployerManagementView.markDone(instance, stepEnum);
			}

			@Override
			public void markRejected(IEnvironmentInstance instance, StepEnum stepEnum, String cause) {
				deployerManagementView.markRejected(instance, stepEnum, cause);
			}

			@Override
			public void markPlay(IEnvironmentInstance instance, StepEnum stepEnum) {
				deployerManagementView.markPlay(instance, stepEnum);
			}

			@Override
			public void log(String log) {
				deployerManagementView.log(log);
			}
		};

		exploreNode(new EnvironmentNode(StaticHelper.getDeployerManagementConstantsBundle().environment()));
	}

	@Override
	public IView getView() {
		return deployerManagementView;
	}

	public List<ISynaptixEnvironment> getSupportedEnvironments() {
		return synaptixDeployerController.getSupportedEnvironments();
	}

	public List<ISynaptixJob> getSupportedJobs() {
		return synaptixDeployerController.getSupportedJobs();
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbs() {
		return synaptixDeployerController.getSupportedDbs();
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbsForEnvironment(ISynaptixEnvironment synaptixEnvironment) {
		return synaptixDeployerController.getSupportedDbsForEnvironment(synaptixEnvironment);
	}

	public void exploreNode(ManagementNode node) {
		deployerManagementView.exploreNode(node);
	}

	public void selectEnvironment(EnvironmentNode environmentNode, ISynaptixEnvironment environment) {
		this.environment = environment;
		environmentNode.setName(StaticHelper.getDeployerManagementConstantsBundle().environment() + " (" + environment.getName() + ")");
		InstructionsNode instructionsNode = new InstructionsNode(StaticHelper.getDeployerManagementConstantsBundle().instructions(), environmentNode);
		exploreNode(instructionsNode);
	}

	public void selectInstructions(InstructionsNode instructionsNode, SynaptixInstructions instructions) {
		this.instructions = instructions;
		if ((instructions != null) && (instructions.isPlayScripts())) {
			PlayScriptsNode playScriptsNode = new PlayScriptsNode(StaticHelper.getDeployerManagementConstantsBundle().playScripts(), instructionsNode);
			exploreNode(playScriptsNode);
		} else if ((instructions != null) && (!instructions.getDownloadInstanceSet().isEmpty())) {
			BuildNode buildNode = new BuildNode(StaticHelper.getDeployerManagementConstantsBundle().build(), instructionsNode);
			exploreNode(buildNode);
		} else {
			launch(null, null, false);
		}
	}

	public void selectScripts(PlayScriptsNode playScriptsNode) {
		if ((instructions != null) && (!instructions.getDownloadInstanceSet().isEmpty())) {
			BuildNode buildNode = new BuildNode(StaticHelper.getDeployerManagementConstantsBundle().build(), playScriptsNode);
			exploreNode(buildNode);
		} else {
			launch(null, null, false);
		}
	}

	public void launch(final ISynaptixJob selectedJob, final String build, final boolean sendMail) {
		if (deployerManagementView.hasError()) {
			viewFactory.showErrorMessageDialog(deployerManagementView, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().error(), StaticHelper.getDeployerManagementConstantsBundle()
					.youHaveAnErrorPleaseCorrectItFirst());
		} else if (viewFactory.showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().confirmation(), StaticHelper.getDeployerManagementConstantsBundle()
				.areYouSureYouWantToDeploy(environment.getName()))) {

			deployerManagementView.initResultPanel(environment, instructions);

			viewFactory.waitSilentViewWorker(new AbstractWorkInProgressViewWorker<Void>() {

				@Override
				protected Void doLoading() throws Exception {
					SynaptixDeployer shellDeployer = synaptixDeployerController.getShellDeployer(environment);
					if (shellDeployer != null) {
						setRunning(true);
						shellDeployer.proceed(instructions, (selectedJob != null ? selectedJob.getConsoleFullUrl().replaceAll("#BUILD#", build) : null), dbPassword, db, buildScriptsMap(),
								trackingCallback);
					}
					return null;
				}

				private Map<String, byte[]> buildScriptsMap() {
					Map<String, byte[]> scriptsMap = new LinkedHashMap<String, byte[]>(CollectionHelper.size(scripts));
					if (instructions.isPlayScripts()) {
						if (scripts != null) {
							for (File f : scripts) {
								FileInputStream fis;
								try {
									fis = new FileInputStream(f);
									try {
										byte[] bytes = IOUtils.toByteArray(fis);
										scriptsMap.put(f.getName(), bytes);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										fis.close();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
					return scriptsMap;
				}

				@Override
				public void success(Void result) {
					setRunning(false);

					if (sendMail) {

						JavaxMailFactory mailFactory = new JavaxMailFactory(getMailConfig().getMailSmtpHost(), getMailConfig().getMailSmtpPort(), getMailConfig().getLogin(), new String(
								getMailConfig().getPassword()), getMailConfig().isSSL());

						StringBuilder downloadBuilder = new StringBuilder();
						int count = 0;
						for (IEnvironmentInstance environmentInstance : instructions.getDownloadInstanceSet()) {
							for (String war : environmentInstance.getWars()) {
								if (count > 0) {
									downloadBuilder.append(", ");
								}
								downloadBuilder.append(war);
								count++;
							}
						}
						try {
							mailFactory.sendMail(getMailConfig().getSenderMail(), getMailConfig().getDefaultReceivers(), null, null,
									StaticHelper.getDeployerManagementConstantsBundle().deploy(environment.getName()),
									StaticHelper.getDeployerManagementConstantsBundle().deployDetails(environment.getName(), count, downloadBuilder.toString(), build), null);
						} catch (Exception e) {
							viewFactory.showErrorMessageDialog(getView(), e);
						}
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(getView(), t);
				}
			});
		}
	}

	public void checkState(final IResultCallback<IEnvironmentState> resultCallback, final ISynaptixEnvironment environment) {
		viewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<IEnvironmentState>() {

			@Override
			protected IEnvironmentState doLoading() throws Exception {
				SynaptixDeployer shellDeployer = synaptixDeployerController.getShellDeployer(environment);
				if (shellDeployer != null) {
					return shellDeployer.checkState();
				} else {
					return null;
				}
			}

			@Override
			public void success(IEnvironmentState result) {
				resultCallback.setResult(result);
			}

			@Override
			public void fail(Throwable e) {
				viewFactory.showErrorMessageDialog(getView(), e);
			}
		});

	}

	public void watchEnvironment(ISynaptixEnvironment environment, IEnvironmentInstance instance) {
		synaptixDeployerController.watchEnvironment(environment, instance);
	}

	public void checkFinalStep(NodeType node) {
		deployerManagementView.checkFinalStep(node);
	}

	private void setRunning(boolean running) {
		this.running = running;
		deployerManagementView.setRunning(running);
	}

	public boolean isRunning() {
		return running;
	}

	public void configMail(final boolean activated) {
		MailConfigCRUDDialogController mailConfigCRUDDialogController = new MailConfigCRUDDialogController(viewFactory);
		mailConfigCRUDDialogController.editEntity(getView(), getMailConfig(), new IResultCallback<IMailConfig>() {

			@Override
			public void setResult(IMailConfig mailConfig) {
				mailConfig.setDefaultSend(activated);
				synaptixDeployerController.setMailConfig(mailConfig);
				deployerManagementView.updateMailCheckbox();
			}
		});
	}

	public IMailConfig getMailConfig() {
		return synaptixDeployerController.getMailConfig();
	}

	public ISynaptixEnvironment getEnvironment() {
		return environment;
	}

	public SynaptixInstructions getInstructions() {
		return instructions;
	}

	public void setInstructions(SynaptixInstructions instructions) {
		this.instructions = instructions;
	}

	public void setScripts(char[] dbPassword, ISynaptixDatabaseSchema db, List<File> scripts) {
		this.dbPassword = dbPassword;
		this.db = db;
		this.scripts = scripts;
	}

	public File browse() {
		return viewFactory.chooseOpenDirectory(getView());
	}

	public void showLog(String message) {
		viewFactory.showHtmlMessageDialog(deployerManagementView, StaticWidgetHelper.getSynaptixWidgetConstantsBundle().information(), message);
	}
}
