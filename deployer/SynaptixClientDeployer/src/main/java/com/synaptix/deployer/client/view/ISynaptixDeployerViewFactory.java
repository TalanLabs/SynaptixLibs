package com.synaptix.deployer.client.view;

import com.synaptix.deployer.client.controller.CompareController;
import com.synaptix.deployer.client.controller.DDLController;
import com.synaptix.deployer.client.controller.DatabaseCheckerController;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.controller.FileSystemSpaceController;
import com.synaptix.deployer.client.controller.WatcherController;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;

public interface ISynaptixDeployerViewFactory extends ISynaptixViewFactory {

	/**
	 * Create a watcher view
	 * 
	 * @param watcherController
	 * @return
	 */
	public IWatcherView createWatcherView(WatcherController watcherController);

	/**
	 * Create a deployer management view
	 * 
	 * @return
	 */
	public IDeployerManagementView createDeployerManagementView(DeployerManagementController deployerManagementController);

	/**
	 * Create a compare view
	 * 
	 * @param compareController
	 * @return
	 */
	public ICompareView createCompareView(CompareController compareController);

	/**
	 * Create a ddl view
	 * 
	 * @param ddlController
	 * @return
	 */
	public IDDLView createDDLView(DDLController ddlController);

	/**
	 * Create a file system space view
	 * 
	 * @param fileSystemSpaceController
	 * @return
	 */
	public IFileSystemSpaceView createFileSystemSpaceView(FileSystemSpaceController fileSystemSpaceController);

	/**
	 * Create the mail config bean dialog view
	 * 
	 * @return
	 */
	public IBeanExtensionDialogView<IMailConfig> createMailConfigBeanDialogView();

	/**
	 * Create a database checker view
	 * 
	 * @param databaseCheckerController
	 * @return
	 */
	public IDatabaseCheckerView createDatabaseCheckerView(DatabaseCheckerController databaseCheckerController);

}
