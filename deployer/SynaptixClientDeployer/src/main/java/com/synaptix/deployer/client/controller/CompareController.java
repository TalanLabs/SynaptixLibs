package com.synaptix.deployer.client.controller;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.common.model.Binome;
import com.synaptix.deployer.client.compare.CompareNode;
import com.synaptix.deployer.client.view.ICompareView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.service.IDeployerService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class CompareController extends AbstractController {

	private ISynaptixDeployerViewFactory synaptixDeployerViewFactory;

	private ICompareView compareView;

	private SynaptixDeployerController synaptixDeployerController;

	public CompareController(ISynaptixDeployerViewFactory synaptixDeployerViewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.synaptixDeployerViewFactory = synaptixDeployerViewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		initialize();
	}

	private void initialize() {
		this.compareView = synaptixDeployerViewFactory.createCompareView(this);
	}

	@Override
	public IView getView() {
		return compareView;
	}

	public void refresh(final ISynaptixDatabaseSchema db1, final ISynaptixDatabaseSchema db2) {

		synaptixDeployerViewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<Binome<ICompareStructureResult, ICompareConstraintResult>>() {

			@Override
			protected Binome<ICompareStructureResult, ICompareConstraintResult> doLoading() throws Exception {
				List<String> ignoreTableList = new ArrayList<String>();
				ignoreTableList.add("PATCH");
				ignoreTableList.add("TOAD_PLAN_TABLE");

				Binome<ICompareStructureResult, ICompareConstraintResult> binome = new Binome<ICompareStructureResult, ICompareConstraintResult>();
				binome.setValue1(getDeployerService().compareStructure(db1, db2, ignoreTableList));
				binome.setValue2(getDeployerService().compareConstraint(db1, db2, ignoreTableList));
				return binome;
			}

			@Override
			public void success(Binome<ICompareStructureResult, ICompareConstraintResult> result) {
				compareView.setCompareResult(db1, db2, result);
			}

			@Override
			public void fail(Throwable e) {
				synaptixDeployerViewFactory.showErrorMessageDialog(getView(), e);
			}
		});
	}

	private IServiceFactory getDeployerServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("deployer");
	}

	private IDeployerService getDeployerService() {
		return getDeployerServiceFactory().getService(IDeployerService.class);
	}

	public void exploreNode(CompareNode node) {
		compareView.exploreNode(node);
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbs() {
		return synaptixDeployerController.getSupportedDbs();
	}
}
