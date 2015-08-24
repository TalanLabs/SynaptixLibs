package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.search.SearchTodoFolderDialogController;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.model.TodoFolderFields;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;

public class TodoFolderSearchFieldWidgetContext extends AbstractWorkflowComponentSearchFieldWidgetContext<ITaskManagerViewFactory, ITodoFolder> {

	public TodoFolderSearchFieldWidgetContext(ITaskManagerViewFactory viewFactory, IView parent, Map<String, Object> filters) {
		super(viewFactory, parent, ITodoFolder.class, filters, TodoFolderFields.meaning().name());
	}

	@Override
	protected AbstractSearchComponentDialogController<ITaskManagerViewFactory, ITodoFolder, ITodoFolder> createDefaultSearchComponentDialogController(ITaskManagerViewFactory viewFactory,
	  Map<String, Object> initialValueFilterMap) {
		return new SearchTodoFolderDialogController(viewFactory, initialValueFilterMap);
	}
}
