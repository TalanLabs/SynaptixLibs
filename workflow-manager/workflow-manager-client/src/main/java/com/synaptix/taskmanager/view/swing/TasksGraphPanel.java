package com.synaptix.taskmanager.view.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.taskmanager.controller.TasksGraphController;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITasksGraphView;
import com.synaptix.taskmanager.view.swing.tasksgraph.JTasksGraphComponent;
import com.synaptix.widget.actions.view.swing.AbstractRefreshAction;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;

public class TasksGraphPanel extends WaitComponentFeedbackPanel implements ITasksGraphView, IDockable, IDockingContextView {

	private static final long serialVersionUID = 4302904570832535212L;

	public static final String ID_DOCKABLE = TasksGraphPanel.class.getName();

	private final TasksGraphController tasksGraphController;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private JTasksGraphComponent graphComponent;

	private Action refreshAction;

	private Action showTaskDetailsAction;

	private Action startTaskManagerAction;

	private Serializable idTask;

	private Serializable idCluster;

	public TasksGraphPanel(TasksGraphController tasksGraphController) {
		super();

		this.tasksGraphController = tasksGraphController;

		initialize();

		this.addContent(buildContents());
	}

	private void initialize() {
		graphComponent = new JTasksGraphComponent(tasksGraphController);
		refreshAction = new AbstractRefreshAction(StaticCommonHelper.getCommonConstantsBundle().refresh()) {

			private static final long serialVersionUID = 725957630431205432L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (idTask != null) {
					tasksGraphController.loadTasksGraphWithIdTask(idTask);
				}
			}
		};

		showTaskDetailsAction = new AbstractAction(StaticCommonHelper.getCommonConstantsBundle().showTasks()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				tasksGraphController.showTasks(idCluster);
			}
		};

		startTaskManagerAction = new AbstractAction(StaticCommonHelper.getCommonConstantsBundle().startTaskManger()) {

			private static final long serialVersionUID = -7319287011671601354L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tasksGraphController.startTaskManager(idCluster);

			}
		};
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(ToolBarFactory.buildToolBar(refreshAction, showTaskDetailsAction, startTaskManagerAction), cc.xy(1, 1));
		builder.add(graphComponent, cc.xy(1, 3));
		return builder.getPanel();
	}

	@Override
	public DockKey getDockKey() {
		return dockKey;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;

		dockKey = new DockKey(ID_DOCKABLE, StaticHelper.getTaskManagerConstantsBundle().tasksGraphManagement());
		dockingContext.registerDockable(this);
	}

	@Override
	public String getCategory() {
		return "taskManager";
	}

	@Override
	public void setTasks(List<ITask> tasks, List<IAssoTaskPreviousTask> assoTaskPreviousTasks) {
		this.idTask = tasks.get(0).getId();
		this.idCluster = tasks.get(0).getIdCluster();
		graphComponent.setTasks2(tasks, assoTaskPreviousTasks);
	}

	@Override
	public void showView() {
		if (dockingContext != null) {
			dockingContext.showDockable(ID_DOCKABLE);
		}
	}

}
