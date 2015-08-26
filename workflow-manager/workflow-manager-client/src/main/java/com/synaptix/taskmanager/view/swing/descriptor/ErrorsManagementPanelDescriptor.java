package com.synaptix.taskmanager.view.swing.descriptor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.ErrorEntityFields;
import com.synaptix.entity.IErrorEntity;
import com.synaptix.entity.IdRaw;
import com.synaptix.swing.JSyTable;
import com.synaptix.taskmanager.controller.ErrorsManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.descriptor.IErrorsManagementViewDescriptor;
import com.synaptix.taskmanager.view.swing.renderer.SerializableTableCellRenderer;
import com.synaptix.widget.component.view.swing.descriptor.DefaultComponentsManagementPanelDescriptor;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;


public class ErrorsManagementPanelDescriptor extends DefaultComponentsManagementPanelDescriptor<IErrorEntity> implements IErrorsManagementViewDescriptor {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(ErrorEntityFields.code(), ErrorEntityFields.idObject(), ErrorEntityFields.idTask());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(ErrorEntityFields.code(), ErrorEntityFields.attribute(), ErrorEntityFields.label(),
			ErrorEntityFields.value(), ErrorEntityFields.type());

	// private static final String[] TASK_HISTORY_TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskHistoryFields.taskStatus(), TaskHistoryFields.comments(),
	// TaskHistoryFields.createdBy(),
	// TaskHistoryFields.createdDate(), TaskHistoryFields.updatedBy(), TaskHistoryFields.updatedDate());

	private final ErrorsManagementController errorsManagementController;

	private Action showTasksAction;

	public ErrorsManagementPanelDescriptor(ErrorsManagementController errorsManagementController) {
		super(new RibbonData(StaticHelper.getTaskManagerConstantsBundle().errorsManagement(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 1, StaticTaskManagerHelper
				.getTaskManagerModuleConstantsBundle().tasks(), 1, "taskManager", ImageWrapperResizableIcon.getIcon(ErrorsManagementPanelDescriptor.class.getResource("/taskManager/images/error.png"),
				new Dimension(30, 30))), StaticHelper.getErrorsTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
		this.errorsManagementController = errorsManagementController;
		initActions();
	}

	private void initActions() {

		showTasksAction = new AbstractAction(StaticHelper.getTaskManagerConstantsBundle().tasksManagement()) {
			private static final long serialVersionUID = 6550320970292728334L;

			@Override
			public void actionPerformed(ActionEvent e) {
				errorsManagementController.searchTasks(getSelectedComponent().getIdObject(), getSelectedComponent().getObjectType());
			}
		};
		showTasksAction.setEnabled(false);

	}

	@Override
	public Set<String> getColumns() {
		Set<String> columns = super.getColumns();
		columns.add(ErrorEntityFields.idObject().name());
		columns.add(ErrorEntityFields.objectType().name());
		columns.add(ErrorEntityFields.idTask().name());
		return columns;
	}

	@Override
	public <E extends ITaskObject<?>> void searchByObject(E taskObject) {
		showDockable();

		getSearchHeader().defaultFilters();
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if (taskObject != null) {
			filterMap.put(ErrorEntityFields.objectType().name(), ComponentFactory.getInstance().getComponentClass(taskObject));
			filterMap.put(ErrorEntityFields.idObject().name(), taskObject.getId() != null ? ((IdRaw) taskObject.getId()).getHex() : null);
		}

		getDefaultSearchComponentsPanel().setValueFilters(filterMap);

		search();
	}

	@Override
	public void searchByTask(Serializable idTask) {
		showDockable();

		getSearchHeader().defaultFilters();
		Map<String, Object> filterMap = new HashMap<String, Object>();
		filterMap.put(ErrorEntityFields.idTask().name(), idTask != null ? ((IdRaw) idTask).getHex() : null);

		getDefaultSearchComponentsPanel().setValueFilters(filterMap);

		search();
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);
		builder.addAction(showTasksAction);
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		table.setDefaultRenderer(Serializable.class, new SerializableTableCellRenderer());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					IErrorEntity errorEntity = getSelectedComponent();
					if (errorEntity != null) {
						showTasksAction.setEnabled(errorEntity.getIdTask() != null);
						showTasksAction.setEnabled(true);
					}
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
					showTasksAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});
	}
}
