package com.synaptix.taskmanager.controller.dialog.search;

import java.util.Map;

import com.synaptix.taskmanager.controller.dialog.AbstractWorkflowSimpleSearchEntityDialogController;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;

public class SearchTodoFolderDialogController extends AbstractWorkflowSimpleSearchEntityDialogController<ITaskManagerViewFactory, ITodoFolder> {

	private final Map<String, Object> initialValueFilterMap;

	public SearchTodoFolderDialogController(ITaskManagerViewFactory viewFactory, Map<String, Object> initialValueFilterMap) {
		super(viewFactory, ITodoFolder.class, StaticHelper.getTaskManagerConstantsBundle().todoFolder());

		this.initialValueFilterMap = initialValueFilterMap;
	}

	@Override
	protected ISearchComponentsDialogViewDescriptor<ITodoFolder> createSearchComponentsDialogViewDescriptor() {
		return getViewFactory().createSearchTodoFolderDialogViewDescriptor();
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		if (initialValueFilterMap != null) {
			filters.putAll(initialValueFilterMap);
		}
	}
}
