package com.synaptix.taskmanager.view.swing;

import com.synaptix.client.common.swing.view.SwingCommonViewFactory;
import com.synaptix.taskmanager.controller.ErrorsManagementController;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.controller.SimulationTasksGraphController;
import com.synaptix.taskmanager.controller.StatusGraphsManagementController;
import com.synaptix.taskmanager.controller.TaskChainsManagementController;
import com.synaptix.taskmanager.controller.TaskServiceDescriptorsManagementController;
import com.synaptix.taskmanager.controller.TaskTypesManagementController;
import com.synaptix.taskmanager.controller.TasksGraphController;
import com.synaptix.taskmanager.controller.TasksManagementController;
import com.synaptix.taskmanager.controller.TodoFoldersManagementController;
import com.synaptix.taskmanager.controller.AbstractTodosManagementController;
import com.synaptix.taskmanager.controller.dialog.edit.TaskTypeDialogController;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.view.IStatusGraphsManagementView;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.ITasksGraphSimulationView;
import com.synaptix.taskmanager.view.ITasksGraphView;
import com.synaptix.taskmanager.view.ITodoManagementView;
import com.synaptix.taskmanager.view.descriptor.IErrorsManagementViewDescriptor;
import com.synaptix.taskmanager.view.descriptor.ITaskChainsManagementViewDescriptor;
import com.synaptix.taskmanager.view.descriptor.ITasksManagementViewDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.ErrorsManagementPanelDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.TaskChainPanelDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.TaskPanelDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.TaskServiceDescriptorPanelDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.TaskTypePanelDescriptor;
import com.synaptix.taskmanager.view.swing.descriptor.TodoFolderViewDescriptor;
import com.synaptix.taskmanager.view.swing.dialog.GeneralStatusGraphBeanExtensionDialog;
import com.synaptix.taskmanager.view.swing.dialog.GeneralTaskChainBeanExtensionDialog;
import com.synaptix.taskmanager.view.swing.dialog.GeneralTaskTypeBeanExtensionDialog;
import com.synaptix.taskmanager.view.swing.dialog.GeneralTodoFolderBeanExtensionDialog;
import com.synaptix.taskmanager.view.swing.dialog.descriptor.SearchTaskChainDialogPanelDescriptor;
import com.synaptix.taskmanager.view.swing.dialog.descriptor.SearchTaskServiceDescriptorDialogPanelDescriptor;
import com.synaptix.taskmanager.view.swing.dialog.descriptor.SearchTaskTypeDialogPanelDescriptor;
import com.synaptix.taskmanager.view.swing.dialog.descriptor.SearchTodoFolderDialogPanelDescriptor;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;

public class SwingTaskManagerViewFactory extends SwingCommonViewFactory implements ITaskManagerViewFactory {

	@Override
	public ISearchComponentsDialogViewDescriptor<ITaskServiceDescriptor> createSearchTaskServiceDescriptorDialogViewDescriptor() {
		return new SearchTaskServiceDescriptorDialogPanelDescriptor();
	}

	@Override
	public IComponentsManagementViewDescriptor<ITaskServiceDescriptor> createTaskServiceDescriptorViewDescriptor(TaskServiceDescriptorsManagementController taskServicesDescriptorsManagementController) {
		return new TaskServiceDescriptorPanelDescriptor(taskServicesDescriptorsManagementController);
	}

	@Override
	public ICRUDManagementViewDescriptor<ITaskType> createTaskTypeViewDescriptor(TaskTypesManagementController taskTypesManagementController) {
		return new TaskTypePanelDescriptor(taskTypesManagementController);
	}

	@Override
	public IBeanExtensionDialogView<ITaskType> newGeneralTaskTypeBeanExtensionDialogView(TaskTypeDialogController taskTypeDialogController, ITaskManagerController taskManagerController) {
		return new GeneralTaskTypeBeanExtensionDialog(taskTypeDialogController, taskManagerController);
	}

	@Override
	public ITaskChainsManagementViewDescriptor createTaskChainViewDescriptor(TaskChainsManagementController taskChainsManagementController) {
		return new TaskChainPanelDescriptor(taskChainsManagementController);
	}

	@Override
	public ISearchComponentsDialogViewDescriptor<ITaskType> createSearchTaskTypeDialogViewDescriptor() {
		return new SearchTaskTypeDialogPanelDescriptor();
	}

	@Override
	public IBeanExtensionDialogView<ITaskChain> newGeneralTaskChainBeanExtensionDialogView(ITaskManagerController taskManagerController) {
		return new GeneralTaskChainBeanExtensionDialog(taskManagerController);
	}

	@Override
	public IBeanExtensionDialogView<IStatusGraph> newGeneralStatusGraphBeanExtensionDialogView(ITaskManagerController taskManagerController, Class<? extends ITaskObject<?>> taskObjectClass) {
		return new GeneralStatusGraphBeanExtensionDialog(taskManagerController, taskObjectClass);
	}

	@Override
	public ISearchComponentsDialogViewDescriptor<ITaskChain> createSearchTaskChainDialogViewDescriptor() {
		return new SearchTaskChainDialogPanelDescriptor();
	}

	@Override
	public ITasksManagementViewDescriptor createTaskViewDescriptor(TasksManagementController tasksManagementController) {
		return new TaskPanelDescriptor(tasksManagementController);
	}

	@Override
	public IErrorsManagementViewDescriptor createErrorsViewDescriptor(ErrorsManagementController errorsManagementController) {
		return new ErrorsManagementPanelDescriptor(errorsManagementController);
	}

	@Override
	public IStatusGraphsManagementView newStatusGraphsManagementView(StatusGraphsManagementController statusGraphsManagementController) {
		return new StatusGraphsManagementPanel(statusGraphsManagementController);
	}

	@Override
	public ITasksGraphView newTaskGraphView(TasksGraphController tasksGraphController) {
		return new TasksGraphPanel(tasksGraphController);
	}

	@Override
	public ICRUDManagementViewDescriptor<ITodoFolder> createTodoFolderViewDescriptor(TodoFoldersManagementController todoFoldersManagementController) {
		return new TodoFolderViewDescriptor(todoFoldersManagementController);
	}

	@Override
	public IBeanExtensionDialogView<ITodoFolder> newGeneralTodoFolderExtensionBeanDialogView() {
		return new GeneralTodoFolderBeanExtensionDialog();
	}

	@Override
	public ISearchComponentsDialogViewDescriptor<ITodoFolder> createSearchTodoFolderDialogViewDescriptor() {
		return new SearchTodoFolderDialogPanelDescriptor();
	}

	@Override
	public ITodoManagementView createTodoPanel(AbstractTodosManagementController abstractTodosManagementController) {
		return new TodoManagementPanel(abstractTodosManagementController);
	}

	@Override
	public ITasksGraphSimulationView newTaskGraphSimulationView(SimulationTasksGraphController tasksGraphSimulationController) {
		return new TasksGraphSimulationPanel(tasksGraphSimulationController);
	}

}
