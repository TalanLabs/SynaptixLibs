package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.synaptix.client.common.controller.dialog.AbstractPscSimpleSearchEntityDialogController;
import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTaskChainDialogController;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.TaskChainFields;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;

public class TaskChainSearchFieldWidgetContext extends AbstractWorkflowComponentSearchFieldWidgetContext<ITaskManagerViewFactory, ITaskChain> {

	public interface Factory {

		TaskChainSearchFieldWidgetContext create(@Assisted IView view, @Assisted @Nullable Map<String, Object> filters);

	}

	@Inject
	private SearchTaskChainDialogController.Factory searchTaskChainDialogControllerFactory;

	@Inject
	public TaskChainSearchFieldWidgetContext(ITaskManagerViewFactory viewFactory, @Assisted IView parent, @Assisted @Nullable Map<String, Object> initialValueFilterMap) {
		super(viewFactory, parent, ITaskChain.class, initialValueFilterMap, TaskChainFields.code().name());
	}

	@Override
	protected AbstractPscSimpleSearchEntityDialogController<ITaskManagerViewFactory, ITaskChain> createDefaultSearchComponentDialogController(ITaskManagerViewFactory viewFactory,
			Map<String, Object> initialValueFilterMap) {
		return searchTaskChainDialogControllerFactory.create(initialValueFilterMap);
	}

}
