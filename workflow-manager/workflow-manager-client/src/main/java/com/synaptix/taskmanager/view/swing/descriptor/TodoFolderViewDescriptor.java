package com.synaptix.taskmanager.view.swing.descriptor;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.synaptix.client.common.swing.view.DefaultNlsCRUDPanelDescriptor;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyTable;
import com.synaptix.taskmanager.controller.TodoFoldersManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.model.TodoFolderFields;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;

public class TodoFolderViewDescriptor extends DefaultNlsCRUDPanelDescriptor<ITodoFolder> {

	private static final RibbonData RIBBON_DATA = new RibbonData(StaticHelper.getTaskManagerConstantsBundle().todosFolders(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle()
			.taskManager(), 1, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().configuration(), 1, "taskManager");

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TodoFolderFields.meaning(), TodoFolderFields.comments());

	private static final String[] TABLE_COLUMNS = new ComponentHelper.PropertyArrayBuilder().addPropertyNames(TodoFolderFields.meaning(), TodoFolderFields.restricted(), TodoFolderFields.comments())
			.build();

	private final TodoFoldersManagementController todoFoldersManagementController;

	private Action modifyRolesAction;

	public TodoFolderViewDescriptor(TodoFoldersManagementController todoFoldersManagementController) {
		super(RIBBON_DATA, StaticHelper.getTodoFoldersTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);

		this.todoFoldersManagementController = todoFoldersManagementController;
	}

	@Override
	public void create() {
		super.create();

		this.modifyRolesAction = new AbstractAction(StaticHelper.getTodoFoldersTableConstantsBundle().modifyRoles()) {

			private static final long serialVersionUID = 8502398527754877583L;

			@Override
			public void actionPerformed(ActionEvent e) {
//				todoFoldersManagementController.editRoles(getDefaultSearchComponentsPanel().getTablePageComponentsPanel().getSelectedComponents().get(0).getId());
			}
		};
		this.modifyRolesAction.setEnabled(false);
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					List<ITodoFolder> folderList = getDefaultSearchComponentsPanel().getTablePageComponentsPanel().getSelectedComponents();
					modifyRolesAction.setEnabled(StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasWriteTodoFoldersManagement() && (CollectionHelper.size(folderList) == 1)
							&& (folderList.get(0).isRestricted()));
				}
			}
		});
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);

		builder.addAction(modifyRolesAction);
	}
}
