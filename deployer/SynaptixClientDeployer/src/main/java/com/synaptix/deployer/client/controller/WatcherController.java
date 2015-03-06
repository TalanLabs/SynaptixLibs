package com.synaptix.deployer.client.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.ReaderListener;
import com.synaptix.deployer.SynaptixDeployer;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.client.view.IWatcherView;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.worker.IShellWorker;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.filefilter.view.TxtFileFilter;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class WatcherController extends AbstractController {

	private ISynaptixDeployerViewFactory viewFactory;

	private IWatcherView watcherView;

	private IShellWorker watcher = null;

	private ISynaptixEnvironment environment;

	private IEnvironmentInstance instance;

	private SynaptixDeployerController synaptixDeployerController;

	public WatcherController(ISynaptixDeployerViewFactory viewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.viewFactory = viewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		initialize();
	}

	private void initialize() {
		this.watcherView = viewFactory.createWatcherView(this);
	}

	@Override
	public IView getView() {
		return watcherView;
	}

	public void setEnvironment(ISynaptixEnvironment environment, IEnvironmentInstance instance) {
		this.environment = environment;
		this.instance = instance;

		watcherView.setTitle(environment.getName(), instance.getName());

		if (watcher != null) {
			watcher.stop();
			watcher = null;
		}
		watcherView.setRunning(false);
		watcherView.reveal();
	}

	public void switchState() {
		viewFactory.waitSilentViewWorker(new AbstractWorkInProgressViewWorker<Void>() {

			@Override
			protected Void doLoading() throws Exception {
				SynaptixDeployer synaptixDeployer = synaptixDeployerController.getShellDeployer(environment);
				if (synaptixDeployer != null) {
					synaptixDeployer.addReaderListener(new ReaderListener() {

						@Override
						public void write(String line) {
							watcherView.addConsoleLine(line);
						}
					});

					if (watcher == null) {
						watcher = synaptixDeployer.watch(instance);
					} else {
						watcher.stop();
						watcher = null;
					}
				}
				return null;
			}

			@Override
			public void success(Void result) {
				watcherView.setRunning(watcher != null);
			}

			@Override
			public void fail(Throwable t) {
				viewFactory.showErrorMessageDialog(getView(), t);
			}
		});
	}

	public void download() {
		final File file = viewFactory.chooseSaveFile(getView(), "log.txt", new TxtFileFilter());
		viewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<String>() {

			@Override
			protected String doLoading() throws Exception {
				SynaptixDeployer synaptixDeployer = synaptixDeployerController.getShellDeployer(environment);
				if (synaptixDeployer != null) {
					return synaptixDeployer.downloadLog(instance);
				}
				return null;
			}

			@Override
			public void success(String result) {
				try {
					FileWriter writer = new FileWriter(file);
					writer.write(result);
					writer.close();
				} catch (IOException e) {
					viewFactory.showErrorMessageDialog(getView(), e);
				}
			}

			@Override
			public void fail(Throwable t) {
				viewFactory.showErrorMessageDialog(getView(), t);
			}
		});
	}

	@Override
	public boolean stop() {
		super.stop();

		if (watcher != null) {
			watcher.stop();
			watcher = null;
		}
		watcherView.setRunning(false);
		return true;
	}
}
