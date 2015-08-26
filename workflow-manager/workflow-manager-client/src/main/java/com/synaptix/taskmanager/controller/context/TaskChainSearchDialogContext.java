package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTaskChainDialogController;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;
import com.synaptix.widget.component.controller.dialog.context.AbstractComponentSearchDialogContext;

public class TaskChainSearchDialogContext extends AbstractComponentSearchDialogContext<ITaskManagerViewFactory, ITaskChain> {

	public interface Factory {

		public TaskChainSearchDialogContext create(@Assisted IView view, @Assisted @Nullable Map<String, Object> filters);

	}

	@Inject
	private SearchTaskChainDialogController.Factory searchTaskChainDialogControllerFactory;

	@Inject
	public TaskChainSearchDialogContext(ITaskManagerViewFactory viewFactory, @Assisted IView parent, @Assisted @Nullable Map<String, Object> filters) {
		super(viewFactory, parent, ITaskChain.class, filters);
	}

	@Override
	protected AbstractSearchComponentDialogController<ITaskManagerViewFactory, ITaskChain, ITaskChain> createDefaultSearchComponentDialogController(ITaskManagerViewFactory viewFactory,
			Map<String, Object> initialValueFilterMap) {
		return searchTaskChainDialogControllerFactory.create(initialValueFilterMap);
	}
}
