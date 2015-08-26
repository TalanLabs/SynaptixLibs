package com.synaptix.taskmanager.view.swing.dialog.descriptor;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyTable;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.TaskChainFields;
import com.synaptix.widget.component.view.swing.descriptor.DefaultDialogDescriptor;

public class SearchTaskChainDialogPanelDescriptor extends DefaultDialogDescriptor<ITaskChain> {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskChainFields.code());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskChainFields.code(), TaskChainFields.objectType(), TaskChainFields.graphRuleReadable(),
			TaskChainFields.description());

	public SearchTaskChainDialogPanelDescriptor() {
		super(com.synaptix.taskmanager.util.StaticHelper.getTaskChainTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		getTableColumn(table, TaskChainFields.objectType().name()).setCellRenderer(TaskObjectClassViewHelper.createTaskObjectClassTableCellRenderer());
	}
}
