package com.synaptix.taskmanager.controller.dialog.search;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.synaptix.client.common.controller.dialog.AbstractPscSimpleSearchEntityDialogController;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;

public class SearchTaskChainDialogController extends AbstractPscSimpleSearchEntityDialogController<ITaskManagerViewFactory, ITaskChain> {

	public interface Factory {

		SearchTaskChainDialogController create(@Assisted @Nullable Map<String, Object> filters);

	}

	private final Map<String, Object> initialValueFilterMap;

	@Inject
	private ISearchComponentsDialogViewDescriptor<ITaskChain> taskChainDialogViewDescriptor;

	@Inject
	public SearchTaskChainDialogController(ITaskManagerViewFactory viewFactory, @Assisted @Nullable Map<String, Object> initialValueFilterMap) {
		super(viewFactory, ITaskChain.class, StaticHelper.getTaskManagerConstantsBundle().taskChain());

		this.initialValueFilterMap = initialValueFilterMap;
	}

	@Override
	protected ISearchComponentsDialogViewDescriptor<ITaskChain> createSearchComponentsDialogViewDescriptor() {
		return taskChainDialogViewDescriptor;
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		if (initialValueFilterMap != null) {
			filters.putAll(initialValueFilterMap);
		}
	}
}
