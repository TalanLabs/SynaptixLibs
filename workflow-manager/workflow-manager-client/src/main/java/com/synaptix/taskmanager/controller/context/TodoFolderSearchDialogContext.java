package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTodoFolderDialogController;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;
import com.synaptix.widget.component.controller.dialog.context.AbstractComponentSearchDialogContext;

public class TodoFolderSearchDialogContext extends AbstractComponentSearchDialogContext<ITaskManagerViewFactory, ITodoFolder> {

	public TodoFolderSearchDialogContext(ITaskManagerViewFactory viewFactory, IView parent, Map<String, Object> filters) {
		super(viewFactory, parent, ITodoFolder.class, filters);
	}

	@Override
	protected AbstractSearchComponentDialogController<ITaskManagerViewFactory, ITodoFolder, ITodoFolder> createDefaultSearchComponentDialogController(ITaskManagerViewFactory viewFactory,
			Map<String, Object> initialValueFilterMap) {
		return new SearchTodoFolderDialogController(viewFactory, initialValueFilterMap);
	}
}
