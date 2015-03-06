package com.synaptix.deployer.client.view.swing;

import com.synaptix.deployer.client.controller.CompareController;
import com.synaptix.deployer.client.controller.DDLController;
import com.synaptix.deployer.client.controller.DatabaseCheckerController;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.controller.FileSystemSpaceController;
import com.synaptix.deployer.client.controller.WatcherController;
import com.synaptix.deployer.client.view.ICompareView;
import com.synaptix.deployer.client.view.IDDLView;
import com.synaptix.deployer.client.view.IDatabaseCheckerView;
import com.synaptix.deployer.client.view.IDeployerManagementView;
import com.synaptix.deployer.client.view.IFileSystemSpaceView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.client.view.IWatcherView;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.SwingSynaptixViewFactory;

public class SwingSynaptixDeployerViewFactory extends SwingSynaptixViewFactory implements ISynaptixDeployerViewFactory {

	@Override
	public IWatcherView createWatcherView(WatcherController watcherController) {
		return new WatcherPanel(watcherController);
	}

	@Override
	public IDeployerManagementView createDeployerManagementView(DeployerManagementController deployerManagementController) {
		return new DeployerManagementPanel(deployerManagementController);
	}

	@Override
	public ICompareView createCompareView(CompareController compareController) {
		return new ComparePanel(compareController);
	}

	@Override
	public IDDLView createDDLView(DDLController ddlController) {
		return new DDLPanel(ddlController);
	}

	@Override
	public IFileSystemSpaceView createFileSystemSpaceView(FileSystemSpaceController fileSystemSpaceController) {
		return new FileSystemSpacePanel(fileSystemSpaceController);
	}

	@Override
	public IBeanExtensionDialogView<IMailConfig> createMailConfigBeanDialogView() {
		return new MailConfigBeanDialog();
	}

	@Override
	public IDatabaseCheckerView createDatabaseCheckerView(DatabaseCheckerController databaseCheckerController) {
		return new DatabaseCheckerPanel(databaseCheckerController);
	}
}
