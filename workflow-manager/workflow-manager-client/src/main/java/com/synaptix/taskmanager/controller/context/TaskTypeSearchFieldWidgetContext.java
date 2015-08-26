package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTaskTypeDialogController;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;

public class TaskTypeSearchFieldWidgetContext extends AbstractWorkflowComponentSearchFieldWidgetContext<ITaskManagerViewFactory, ITaskType> {

	public TaskTypeSearchFieldWidgetContext(ITaskManagerViewFactory viewFactory, IView parent, Map<String, Object> filters) {
		super(viewFactory, parent, ITaskType.class, filters, TaskTypeFields.code().name());
	}

	@Override
	protected AbstractSearchComponentDialogController<ITaskManagerViewFactory, ITaskType, ITaskType> createDefaultSearchComponentDialogController(ITaskManagerViewFactory viewFactory,
			Map<String, Object> initialValueFilterMap) {
		return new SearchTaskTypeDialogController(viewFactory, initialValueFilterMap);
	}
}
