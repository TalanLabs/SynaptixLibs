package com.synaptix.taskmanager.controller.dialog.search;

import java.util.Map;

import com.synaptix.taskmanager.controller.dialog.AbstractWorkflowSimpleSearchEntityDialogController;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;


public class SearchTaskTypeDialogController extends AbstractWorkflowSimpleSearchEntityDialogController<ITaskManagerViewFactory, ITaskType> {

	private final Map<String, Object> initialValueFilterMap;

	public SearchTaskTypeDialogController(ITaskManagerViewFactory viewFactory, Map<String, Object> initialValueFilterMap) {
		super(viewFactory, ITaskType.class, StaticHelper.getTaskManagerConstantsBundle().taskType());

		this.initialValueFilterMap = initialValueFilterMap;
	}

	@Override
	protected ISearchComponentsDialogViewDescriptor<ITaskType> createSearchComponentsDialogViewDescriptor() {
		return getViewFactory().createSearchTaskTypeDialogViewDescriptor();
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		if (initialValueFilterMap != null) {
			filters.putAll(initialValueFilterMap);
		}
	}
}
