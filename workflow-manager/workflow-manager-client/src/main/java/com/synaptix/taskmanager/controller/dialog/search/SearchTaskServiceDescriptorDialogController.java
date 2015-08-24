package com.synaptix.taskmanager.controller.dialog.search;

import java.util.Map;

import com.synaptix.taskmanager.controller.dialog.AbstractWorkflowSimpleSearchComponentDialogController;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.service.ITaskServiceDescriptorService;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;

public class SearchTaskServiceDescriptorDialogController extends AbstractWorkflowSimpleSearchComponentDialogController<ITaskManagerViewFactory, ITaskServiceDescriptor> {

	private final Map<String, Object> initialValueFilterMap;

	public SearchTaskServiceDescriptorDialogController(ITaskManagerViewFactory viewFactory, Map<String, Object> initialValueFilterMap) {
		super(viewFactory, ITaskServiceDescriptor.class, ITaskServiceDescriptorService.class, StaticHelper.getTaskManagerConstantsBundle().taskService());

		this.initialValueFilterMap = initialValueFilterMap;
	}

	@Override
	protected ISearchComponentsDialogViewDescriptor<ITaskServiceDescriptor> createSearchComponentsDialogViewDescriptor() {
		return getViewFactory().createSearchTaskServiceDescriptorDialogViewDescriptor();
	}

	@Override
	protected ITaskServiceDescriptor findFullComponent(ITaskServiceDescriptor paginationEntity) {
		return paginationEntity;
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		if (initialValueFilterMap != null) {
			filters.putAll(initialValueFilterMap);
		}
	}
}
