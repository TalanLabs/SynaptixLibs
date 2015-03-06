package com.synaptix.deployer.client.view.swing.deployerManagement;

import javax.swing.JComponent;

import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.ManagementNode;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.view.swing.IValidationView;

public abstract class AbstractManagementPanel<N extends ManagementNode> extends WaitComponentFeedbackPanel implements IValidationView {

	private static final long serialVersionUID = 7217567041679647590L;

	protected final ValidationResultModel validationResultModel;

	private N node;

	private DeployerManagementController managementController;

	public AbstractManagementPanel(N node, DeployerManagementController managementController) {
		super();

		this.node = node;
		this.managementController = managementController;

		validationResultModel = new DefaultValidationResultModel();

		initComponents();

		this.addContent(buildContent());
	}

	protected final DeployerManagementController getManagementController() {
		return managementController;
	}

	protected abstract void initComponents();

	protected abstract JComponent buildContent();

	public N getNode() {
		return node;
	}

	public void setActive(boolean active) {
	}

	public void setRunning(boolean running) {
	}

	public final boolean hasError() {
		return validationResultModel.hasErrors();
	}
}
