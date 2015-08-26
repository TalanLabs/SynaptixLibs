package com.synaptix.taskmanager.controller.context;

import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTaskServiceDescriptorDialogController;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.TaskServiceDescriptorFields;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;

public class TaskServiceDescriptorSearchFieldWidgetContext extends AbstractWorkflowComponentSearchFieldWidgetContext<ITaskManagerViewFactory, ITaskServiceDescriptor> {

	public TaskServiceDescriptorSearchFieldWidgetContext(ITaskManagerViewFactory viewFactory, IView parent, Map<String, Object> filters) {
		super(viewFactory, parent, ITaskServiceDescriptor.class, filters, TaskServiceDescriptorFields.code().name());
	}

	@Override
	protected AbstractSearchComponentDialogController<ITaskManagerViewFactory, ITaskServiceDescriptor, ITaskServiceDescriptor> createDefaultSearchComponentDialogController(
			ITaskManagerViewFactory viewFactory, Map<String, Object> initialValueFilterMap) {
		return new SearchTaskServiceDescriptorDialogController(viewFactory, initialValueFilterMap);
	}

	@Override
	protected List<ITaskServiceDescriptor> suggestService(String name) throws Exception {
		return null;
	}
}
