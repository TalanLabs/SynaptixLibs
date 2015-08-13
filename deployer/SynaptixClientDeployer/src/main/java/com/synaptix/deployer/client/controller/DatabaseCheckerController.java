package com.synaptix.deployer.client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.client.view.IView;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.client.model.IDatabaseQuery;
import com.synaptix.deployer.client.view.IDatabaseCheckerView;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.viewworker.view.AbstractWorkInProgressViewWorker;

public class DatabaseCheckerController extends AbstractController {

	private static final Log LOG = LogFactory.getLog(DatabaseCheckerController.class);

	private ISynaptixDeployerViewFactory synaptixDeployerViewFactory;

	private SynaptixDeployerController synaptixDeployerController;

	@Inject(optional = true)
	private Set<IDatabaseQueryContext<?>> databaseQueryContextSet;

	private IDatabaseCheckerView databaseCheckerView;

	private final Map<IDatabaseQuery, IDatabaseQueryContext<?>> databaseQueryMap;

	private IDatabaseQuery selectedDatabaseQuery;

	@Inject
	public DatabaseCheckerController(ISynaptixDeployerViewFactory synaptixDeployerViewFactory, SynaptixDeployerController synaptixDeployerController) {
		super();

		this.synaptixDeployerViewFactory = synaptixDeployerViewFactory;
		this.synaptixDeployerController = synaptixDeployerController;

		this.databaseQueryMap = new HashMap<IDatabaseQuery, IDatabaseQueryContext<?>>();
	}

	public void initialize() {
		databaseCheckerView = synaptixDeployerViewFactory.createDatabaseCheckerView(this);

		List<IDatabaseQuery> databaseQueryList = new ArrayList<IDatabaseQuery>();
		if (databaseQueryContextSet != null) {
			for (IDatabaseQueryContext<?> databaseQueryContext : databaseQueryContextSet) {
				IDatabaseQuery databaseQuery = ComponentFactory.getInstance().createInstance(IDatabaseQuery.class);
				databaseQuery.setName(databaseQueryContext.getName());
				databaseQuery.setMeaning(databaseQueryContext.getMeaning());
				databaseQueryMap.put(databaseQuery, databaseQueryContext);
				databaseQueryList.add(databaseQuery);
			}
		}
		databaseCheckerView.setDatabaseQueryList(databaseQueryList);
	}

	@Override
	public IView getView() {
		return databaseCheckerView;
	}

	public <T> void testAll(final ISynaptixDatabaseSchema database) {
		synaptixDeployerViewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<String>() {

			@Override
			protected String doLoading() throws Exception {
				String result = null;
				for (Entry<IDatabaseQuery, IDatabaseQueryContext<?>> entry : databaseQueryMap.entrySet()) {
					@SuppressWarnings("unchecked")
					IDatabaseQueryContext<T> context = (IDatabaseQueryContext<T>) entry.getValue();
					try {
						if (selectedDatabaseQuery == entry.getKey()) {
							T res = context.launchTest(database);
							entry.getKey().setValid(context.isValid(res));
							result = context.getHTMLResult(res);
						} else {
							entry.getKey().setValid(context.isValid(context.launchTest(database)));
						}
					} catch (Exception e) {
						LOG.error("", e);
						if (selectedDatabaseQuery == entry.getKey()) {
							if (StringUtils.isNotBlank(e.getMessage())) {
								result = e.getMessage();
							} else {
								result = e.toString();
							}
						}
						entry.getKey().setValid(false);
					}
				}
				return result;
			}

			@Override
			public void success(String result) {
				boolean valid = false;
				if (selectedDatabaseQuery != null) {
					valid = selectedDatabaseQuery.isValid() != null ? selectedDatabaseQuery.isValid() : false;
				}
				databaseCheckerView.showDatabaseQueryResult(valid, result);
				databaseCheckerView.updateTree();
			}

			@Override
			public void fail(Throwable t) {
				LOG.error("", t);
				databaseCheckerView.updateTree();
			}
		});
	}

	public void setSelectedDatabaseQuery(IDatabaseQuery databaseQuery) {
		if (this.selectedDatabaseQuery != databaseQuery) {
			this.selectedDatabaseQuery = databaseQuery;
			databaseCheckerView.showDatabaseQuery(databaseQuery);
		}
	}

	public <T> void testCurrentDatabaseQuery(final ISynaptixDatabaseSchema database) {
		@SuppressWarnings("unchecked")
		final IDatabaseQueryContext<T> context = (IDatabaseQueryContext<T>) databaseQueryMap.get(selectedDatabaseQuery);
		if (context != null) {
			synaptixDeployerViewFactory.waitFullComponentViewWorker(getView(), new AbstractWorkInProgressViewWorker<T>() {

				@Override
				protected T doLoading() throws Exception {
					return context.launchTest(database);
				}

				@Override
				public void success(T result) {
					boolean valid = context.isValid(result);
					selectedDatabaseQuery.setValid(valid);
					databaseCheckerView.showDatabaseQueryResult(valid, context.getHTMLResult(result));
					databaseCheckerView.updateTree();
				}

				@Override
				public void fail(Throwable t) {
					LOG.error("", t);
					selectedDatabaseQuery.setValid(false);
					databaseCheckerView.showDatabaseQueryResult(false, t.getMessage());
					databaseCheckerView.updateTree();
				}
			});
		}
	}

	public List<ISynaptixDatabaseSchema> getSupportedDbs() {
		return synaptixDeployerController.getSupportedDbs();
	}
}
