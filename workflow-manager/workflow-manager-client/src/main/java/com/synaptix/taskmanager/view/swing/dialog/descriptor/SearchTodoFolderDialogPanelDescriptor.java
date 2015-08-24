package com.synaptix.taskmanager.view.swing.dialog.descriptor;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.model.TodoFolderFields;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.component.view.swing.descriptor.DefaultDialogDescriptor;

public class SearchTodoFolderDialogPanelDescriptor extends DefaultDialogDescriptor<ITodoFolder> {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TodoFolderFields.meaning(), TodoFolderFields.comments());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TodoFolderFields.meaning(), TodoFolderFields.comments());

	public SearchTodoFolderDialogPanelDescriptor() {
		super(StaticHelper.getTodoFoldersTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
	}
}
