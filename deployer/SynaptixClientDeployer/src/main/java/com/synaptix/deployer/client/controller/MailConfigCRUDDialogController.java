package com.synaptix.deployer.client.controller;

import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public class MailConfigCRUDDialogController extends AbstractCRUDDialogController<IMailConfig> {

	private ISynaptixDeployerViewFactory viewFactory;

	private IBeanExtensionDialogView<IMailConfig> mailConfigDialogView;

	private ICRUDBeanDialogView<IMailConfig> beanDialogView;

	public MailConfigCRUDDialogController(ISynaptixDeployerViewFactory viewFactory) {
		super(IMailConfig.class, StaticHelper.getDeployerManagementConstantsBundle().mailConfiguration(), StaticHelper.getDeployerManagementConstantsBundle().edit(), StaticHelper
				.getDeployerManagementConstantsBundle().edit());

		this.viewFactory = viewFactory;

		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		mailConfigDialogView = viewFactory.createMailConfigBeanDialogView();
		beanDialogView = viewFactory.newCRUDBeanDialogView(mailConfigDialogView);
	}

	@Override
	protected ICRUDBeanDialogView<IMailConfig> getCRUDBeanDialogView() {
		return beanDialogView;
	}
}
