package com.synaptix.taskmanager.view.swing.descriptor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.joda.time.Duration;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IdRaw;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.taskmanager.controller.TasksManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.TaskFields;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.descriptor.ITasksManagementViewDescriptor;
import com.synaptix.taskmanager.view.swing.renderer.SerializableTableCellRenderer;
import com.synaptix.widget.component.view.swing.descriptor.DefaultComponentsManagementPanelDescriptor;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.renderer.JavaDateTimeTableCellRenderer;

public class TaskPanelDescriptor extends DefaultComponentsManagementPanelDescriptor<ITask> implements ITasksManagementViewDescriptor {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskFields.idCluster(), TaskFields.objectType(), TaskFields.idObject(), TaskFields.serviceCode(),
			TaskFields.nature(), TaskFields.taskStatus());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskFields.idCluster(), TaskFields.objectType(), TaskFields.idObject(), TaskFields.taskStatus(),
			TaskFields.serviceCode(), TaskFields.nature(), TaskFields.checkSkippable(), TaskFields.executantRole(), TaskFields.managerRole(), TaskFields.checkGroup(), TaskFields.nextStatus(),
			TaskFields.checkError(), TaskFields.errorMessage(), TaskFields.todoManagerDuration(), TaskFields.checkTodoExecutantCreated(), TaskFields.checkTodoManagerCreated(), TaskFields.startDate(),
			TaskFields.endDate(), TaskFields.resultStatus(), TaskFields.resultDesc());

	private final TasksManagementController tasksManagementController;

	private Action skipTaskAction;

	private Action showTasksGraphAction;

	private Action showErrorsAction;

	private Action showTaskClusterAction;

	public TaskPanelDescriptor(TasksManagementController tasksManagementController) {
		super(new RibbonData(StaticHelper.getTaskManagerConstantsBundle().tasksManagement(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 2, StaticTaskManagerHelper
				.getTaskManagerModuleConstantsBundle().tasks(), 1, "taskManager", ImageWrapperResizableIcon.getIcon(TaskPanelDescriptor.class.getResource("/taskManager/images/task.png"),
				new Dimension(30, 30))), StaticHelper.getTaskTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);

		this.tasksManagementController = tasksManagementController;

		initActions();
	}

	private void initActions() {
		skipTaskAction = new SkipTaskAction();
		skipTaskAction.setEnabled(false);

		showTasksGraphAction = new ShowGraphAction();
		showTasksGraphAction.setEnabled(false);

		showErrorsAction = new AbstractAction(StaticHelper.getTaskManagerConstantsBundle().errorsManagement()) {
			private static final long serialVersionUID = 6550320970292728334L;

			@Override
			public void actionPerformed(ActionEvent e) {
				tasksManagementController.showErrors(getSelectedComponent());
			}
		};
		showErrorsAction.setEnabled(false);

		showTaskClusterAction = new ShowTaskCluster();
		showTaskClusterAction.setEnabled(false);

	}

	@Override
	public void installPopupMenu(IPopupMenuActionsBuilder builder) {
		super.installPopupMenu(builder);
		builder.addAction(showTasksGraphAction);
		builder.addSeparator();
		builder.addAction(skipTaskAction);
		builder.addSeparator();
		builder.addAction(showErrorsAction);
		builder.addSeparator();
		builder.addAction(showTaskClusterAction);
	}

	@Override
	public List<Filter> getSpecificFilters() {
		List<Filter> specificFilters = new ArrayList<Filter>();
		specificFilters.add(TaskManagerViewHelper.createEnumServiceNatureDefaultSuperComboBoxFilter(TaskFields.nature().name(), StaticHelper.getTaskTableConstantsBundle().nature()));
		specificFilters.add(TaskObjectClassViewHelper.createTaskObjectClassDefaultSuperComboBoxFilter(TaskFields.objectType().name(), StaticHelper.getTaskTableConstantsBundle().objectType(), true));
		specificFilters.add(TaskManagerViewHelper.createEnumTaskStatusDefaultSuperComboBoxFilter(TaskFields.taskStatus().name(), StaticHelper.getTaskTableConstantsBundle().taskStatus()));
		return specificFilters;
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		getTableColumn(table, TaskFields.nature().name()).setCellRenderer(TaskManagerViewHelper.createEnumServiceNatureTableCellRenderer());
		getTableColumn(table, TaskFields.objectType().name()).setCellRenderer(TaskObjectClassViewHelper.createTaskObjectClassTableCellRenderer());
		getTableColumn(table, TaskFields.managerRole().name()).setCellRenderer(new MyManagerRoleSubstanceDefaultTableCellRenderer());
		getTableColumn(table, TaskFields.executantRole().name()).setCellRenderer(new MyExecutantRoleSubstanceDefaultTableCellRenderer());
		getTableColumn(table, TaskFields.taskStatus().name()).setCellRenderer(TaskManagerViewHelper.createEnumTaskStatusTableCellRenderer());
		getTableColumn(table, TaskFields.todoManagerDuration().name()).setCellRenderer(new MyTodoManagerDurationTableCellRenderer());
		getTableColumn(table, TaskFields.resultStatus().name()).setCellRenderer(TaskManagerViewHelper.createEnumServiceResultStatusTableCellRenderer());

		// TODO excel?
		getTableColumn(table, TaskFields.resultStatus().name()).setExcelColumnRenderer(TaskManagerViewHelper.createEnumServiceResultStatusExcelColumnRenderer());

		table.setDefaultRenderer(Serializable.class, new SerializableTableCellRenderer());
		table.setDefaultRenderer(Date.class, new JavaDateTimeTableCellRenderer());

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					ITask task = getSelectedComponent();
					if (task != null) {
						showTasksGraphAction.setEnabled(task != null);
						skipTaskAction.setEnabled(task != null && task.isCheckSkippable() && TaskStatus.CURRENT.equals(task.getTaskStatus()));
						showErrorsAction.setEnabled(task != null && task.isCheckError());
						showTaskClusterAction.setEnabled(task != null);
					} else {
						showTasksGraphAction.setEnabled(false);
						showTaskClusterAction.setEnabled(false);
					}
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
					showTasksGraphAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);
		builder.addAction(showTasksGraphAction);
		builder.addSeparator();
		builder.addAction(skipTaskAction);
		builder.addSeparator();
		builder.addAction(showErrorsAction);
		builder.addSeparator();
		builder.addAction(showTaskClusterAction);
	}

	@Override
	public <E extends ITaskObject<?>> void searchBy(E taskObject) {
		showDockable();

		getSearchHeader().defaultFilters();
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if (taskObject != null) {
			filterMap.put(TaskFields.objectType().name(), ComponentFactory.getInstance().getComponentClass(taskObject));
			filterMap.put(TaskFields.idObject().name(), taskObject.getId() != null ? ((IdRaw) taskObject.getId()).getHex() : null);
		}

		getDefaultSearchComponentsPanel().setValueFilters(filterMap);

		search();
	}

	@Override
	public void searchByCluster(Serializable idCluster) {
		showDockable();

		getSearchHeader().defaultFilters();
		Map<String, Object> filterMap = new HashMap<String, Object>();
		filterMap.put(TaskFields.idCluster().name(), idCluster != null ? ((IdRaw) idCluster).getHex() : null);

		getDefaultSearchComponentsPanel().setValueFilters(filterMap);

		search();

	}

	@Override
	public boolean isSearchAtOpening() {
		return false;
	}

	private final class SkipTaskAction extends AbstractAction {

		private static final long serialVersionUID = 6702118091939215753L;

		public SkipTaskAction() {
			super(StaticHelper.getTaskTableConstantsBundle().skipTaskEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tasksManagementController.skipTask(getSelectedComponent());
		}
	}

	private final class ShowGraphAction extends AbstractAction {

		private static final long serialVersionUID = 4884122392488277164L;

		public ShowGraphAction() {
			super(StaticHelper.getTaskTableConstantsBundle().showGraphEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tasksManagementController.showTasksGraph(getSelectedComponent());
		}
	}

	private final class ShowTaskCluster extends AbstractAction {
		private static final long serialVersionUID = 452132111L;

		public ShowTaskCluster() {
			super(StaticHelper.getTaskTableConstantsBundle().showTaskByCluster());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tasksManagementController.searchTasksByIdCluster(getSelectedComponent().getIdCluster());
		}
	}

	private final class MyManagerRoleSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ITask task = getTablePageComponentsPanel().getComponentTableModel().getComponent(table.convertRowIndexToModel(row));
			if (task != null) {
				if (value != null && task.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(task.getObjectType());
					setText(objectType, (String) value);
				} else {
					setText(StaticCommonHelper.getCommonConstantsBundle().none());
				}
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(objectType.getManagerRoleMeaning(Enum.valueOf(objectType.getManagerRoleClass(), value)));
			} else {
				setText(value);
			}
		}
	}

	private final class MyExecutantRoleSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ITask task = getTablePageComponentsPanel().getComponentTableModel().getComponent(table.convertRowIndexToModel(row));
			if (task != null) {
				if (value != null && task.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(task.getObjectType());
					setText(objectType, (String) value);
				} else {
					setText(StaticCommonHelper.getCommonConstantsBundle().none());
				}
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(objectType.getExecutantRoleMeaning(Enum.valueOf(objectType.getExecutantRoleClass(), value)));
			} else {
				setText(value);
			}
		}
	}

	private final class MyTodoManagerDurationTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<Duration> {

		private static final long serialVersionUID = -2171627954700057007L;

		public MyTodoManagerDurationTableCellRenderer() {
			super(new GenericObjectToString<Duration>() {
				@Override
				public String getString(Duration t) {
					return t != null ? String.valueOf(t.getStandardHours()) : null;
				}
			});

			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}
}
