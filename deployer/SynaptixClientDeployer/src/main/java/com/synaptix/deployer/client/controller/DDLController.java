package com.synaptix.deployer.client.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IDDLView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.service.IDeployerService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.filefilter.view.AbstractFileFilter;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class DDLController extends AbstractController {

	private ISynaptixDeployerViewFactory viewFactory;

	private SynaptixDeployerController synaptixDeployerController;

	private IDDLView ddlView;

	public DDLController(ISynaptixDeployerViewFactory viewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.viewFactory = viewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		initialize();
	}

	private void initialize() {
		ddlView = viewFactory.createDDLView(this);
	}

	@Override
	public IView getView() {
		return ddlView;
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbs() {
		return synaptixDeployerController.getSupportedDbs();
	}

	public void downloadDDL(final ISynaptixDatabaseSchema database, final String login, final char[] password) {
		final File file = viewFactory.chooseSaveFile(getView(), "ddl.sql", new SqlFileFilter());
		if (file != null) {
			viewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<Void>() {

				@Override
				protected Void doLoading() throws Exception {
					List<String> ddlList = getDeployerService().getDDL(database, login, password);
					if (ddlList != null) {
						FileWriter fileWriter = new FileWriter(file);
						for (String ddl : ddlList) {
							fileWriter.append(ddl).append("\r\n");
						}
						fileWriter.close();
					}
					return null;
				}

				@Override
				public void success(Void e) {
					try {
						Desktop desktop = Desktop.getDesktop();
						desktop.open(file);
					} catch (Exception e2) {
						viewFactory.showErrorMessageDialog(getView(), e2);
					}
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(getView(), t);
				}
			});
		}
	}

	private IServiceFactory getDeployerServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("deployer");
	}

	private IDeployerService getDeployerService() {
		return getDeployerServiceFactory().getService(IDeployerService.class);
	}

	public class SqlFileFilter extends AbstractFileFilter {

		public static final String SQL_EXTENSION = ".sql";

		public SqlFileFilter() {
			super(SQL_EXTENSION);
		}

		@Override
		public String getDescription() {
			return StaticHelper.getDDLConstantsBundle().sqlFile();
		}
	}
}
