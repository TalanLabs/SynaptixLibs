package com.synaptix.deployer.client.controller;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.view.IFileSystemSpaceView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.model.IFileSystemSpace;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class FileSystemSpaceController extends AbstractController {

	private ISynaptixDeployerViewFactory viewFactory;

	private SynaptixDeployerController synaptixDeployerController;

	private IFileSystemSpaceView fileSystemSpaceView;

	public FileSystemSpaceController(ISynaptixDeployerViewFactory viewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.viewFactory = viewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		initialize();
	}

	private void initialize() {
		fileSystemSpaceView = viewFactory.createFileSystemSpaceView(this);
	}

	@Override
	public IView getView() {
		return fileSystemSpaceView;
	}

	public List<ISynaptixEnvironment> getSupportedEnvironments() {
		return synaptixDeployerController.getSupportedEnvironments();
	}

	public void refresh(final ISynaptixEnvironment environment) {
		viewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<List<IFileSystemSpace>>() {

			@Override
			protected List<IFileSystemSpace> doLoading() throws Exception {
				return synaptixDeployerController.getShellDeployer(environment).getFileSystemSpace();
			}

			@Override
			public void success(List<IFileSystemSpace> e) {
				fileSystemSpaceView.setComponents(e);
			}

			@Override
			public void fail(Throwable t) {
				viewFactory.showErrorMessageDialog(getView(), t);
			}
		});
	}
}
