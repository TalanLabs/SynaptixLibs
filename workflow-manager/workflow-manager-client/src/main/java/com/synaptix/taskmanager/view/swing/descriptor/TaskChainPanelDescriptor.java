package com.synaptix.taskmanager.view.swing.descriptor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.inject.Inject;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.taskmanager.controller.TaskChainsManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.TaskChainFields;
import com.synaptix.taskmanager.view.swing.graph.JTaskChainGraphComponent;
import com.synaptix.widget.crud.view.swing.descriptor.DefaultCRUDPanelDescriptor;
import com.synaptix.widget.model.RibbonData;

public class TaskChainPanelDescriptor extends DefaultCRUDPanelDescriptor<ITaskChain> implements com.synaptix.taskmanager.view.descriptor.ITaskChainsManagementViewDescriptor {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskChainFields.code(), TaskChainFields.objectType(), TaskChainFields.taskTypes());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskChainFields.code(), TaskChainFields.objectType(), TaskChainFields.graphRuleReadable(),
			TaskChainFields.description());

	private final TaskChainsManagementController taskChainsManagementController;

	private JTaskChainGraphComponent graphComponent;

	@Inject
	public TaskChainPanelDescriptor(TaskChainsManagementController taskChainsManagementController) {
		super(new RibbonData(com.synaptix.taskmanager.util.StaticHelper.getTaskManagerConstantsBundle().taskChainsManagement(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 2,
				StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().configuration(), 1, "taskManager"), com.synaptix.taskmanager.util.StaticHelper.getTaskChainTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
		this.taskChainsManagementController = taskChainsManagementController;
	}

	@Override
	public List<Filter> getSpecificFilters() {
		List<Filter> specificFilters = new ArrayList<Filter>();
		specificFilters.add(TaskObjectClassViewHelper.createTaskObjectClassDefaultSuperComboBoxFilter(TaskChainFields.objectType().name(),
				com.synaptix.taskmanager.util.StaticHelper.getTaskChainTableConstantsBundle().objectType(), true));
		/***************** Loupe ***************/
		specificFilters.add(
				TaskManagerViewHelper.createTaskTypeDefaultSearchFieldFilter(getDefaultComponentsManagementPanel(), TaskChainFields.taskTypes().name(), com.synaptix.taskmanager.util.StaticHelper
						.getTaskChainTableConstantsBundle().taskTypes(), null));
		/******************/
		return specificFilters;
	}

	@Override
	public void installTable(final JSyTable table) {
		super.installTable(table);
		getTableColumn(table, TaskChainFields.objectType().name()).setCellRenderer(TaskObjectClassViewHelper.createTaskObjectClassTableCellRenderer());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					taskChainsManagementController.loadTaskChain(getSelectedComponent());
				}
			}
		});
	}

	@Override
	protected JComponent createDetailComponent() {
		graphComponent = new JTaskChainGraphComponent();
		return buildChildContents();
	}

	private JComponent buildChildContents() {
		return graphComponent;
	}

	@Override
	public void setTaskChain(ITaskChain taskChain) {
		if (taskChain != null) {
			graphComponent.setGraphRule(taskChain.getGraphRuleReadable(), taskChain.getTaskTypes());
		} else {
			graphComponent.setGraphRule(null);
		}
	}
}
