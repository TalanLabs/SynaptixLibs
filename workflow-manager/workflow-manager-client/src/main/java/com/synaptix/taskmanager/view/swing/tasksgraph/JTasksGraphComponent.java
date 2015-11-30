package com.synaptix.taskmanager.view.swing.tasksgraph;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IId;
import com.synaptix.taskmanager.controller.TasksGraphController;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.model.AssoTaskPreviousTaskFields;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskFields;
import com.synaptix.taskmanager.model.TaskTypeBuilder;
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.view.swing.graph.AbstractGraphWithDetailsComponent;
import com.synaptix.taskmanager.view.swing.graph.JRingNode;

public class JTasksGraphComponent extends AbstractGraphWithDetailsComponent {

	public static final String DATE_TIME_PATTERN_WITH_SECONDS = "dd/MM/yyyy HH:mm:ss";
	private static final long serialVersionUID = 120081943081039182L;
	private static final String ERROR_MESSAGE_LABEL = "ERROR_MESSAGE_LABEL";
	private static final String ERRORS_BUTTON = "ERRORS_BUTTON";
	private JLabel meaningLabel;
	private JLabel codeLabel;
	private JLabel descriptionLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	private JLabel errorMessageLabel;
	private JButton errorsButton;
	private JPanel cardPanel;
	private TasksGraphController tasksGraphController;
	private ITask task;
	private JLabel durationLabel;

	public JTasksGraphComponent(TasksGraphController tasksGraphController) {
		super();
		this.tasksGraphController = tasksGraphController;
	}

	@Override
	protected void clearDetails() {
		meaningLabel.setText(null);
		codeLabel.setText(null);
		descriptionLabel.setText(null);
		startDateLabel.setText(null);
		endDateLabel.setText(null);
		errorMessageLabel.setText(null);
		showErrorButton(false);
		durationLabel.setText(null);
	}

	@Override
	protected void initalizeDetailsPanel() {
		meaningLabel = new JLabel();
		codeLabel = new JLabel();
		descriptionLabel = new JLabel();
		startDateLabel = new JLabel();
		endDateLabel = new JLabel();
		errorMessageLabel = new JLabel();
		errorsButton = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tasksGraphController.showErrors(task.getId());
			}
		});
		errorsButton.setText(StaticCommonHelper.getCommonConstantsBundle().showErrors());

		cardPanel = new JPanel(new CardLayout());
		cardPanel.add(errorMessageLabel, ERROR_MESSAGE_LABEL);
		cardPanel.add(buildErrorButtonPanel(), ERRORS_BUTTON);
		durationLabel = new JLabel();
	}

	private JPanel buildErrorButtonPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,5DLU,FILL:DEFAULT:GROW(1.0)");
		DefaultFormBuilder defaultFormBuilder = new DefaultFormBuilder(layout);
		defaultFormBuilder.append(errorsButton);
		return defaultFormBuilder.getPanel();
	}

	@Override
	protected void fillDetails(Object value) {
		if (value instanceof ITask) {
			task = (ITask) value;

			ITaskType taskType = task.getTaskType();
			if (taskType == null) {
				taskType = new TaskTypeBuilder().build();
			}
			meaningLabel.setText(taskType.getMeaning());
			codeLabel.setText(taskType.getServiceCode());

			if (taskType.getDescription() != null) {
				descriptionLabel.setText("<html>" + taskType.getDescription() + "</html>");
			} else {
				descriptionLabel.setText(null);
			}

			if (task.getStartDate() != null) {
				String startDate = new LocalDateTime(task.getStartDate()).toString(DATE_TIME_PATTERN_WITH_SECONDS);
				startDateLabel.setText(startDate);
			} else {
				startDateLabel.setText(null);
			}

			if (task.getEndDate() != null) {
				String endDate = new LocalDateTime(task.getEndDate()).toString(DATE_TIME_PATTERN_WITH_SECONDS);
				endDateLabel.setText(endDate);
			} else {
				endDateLabel.setText(null);
			}

			if ((task.getStartDate() != null && task.getEndDate() != null) && (!task.getStartDate().after(task.getEndDate()))) {

				try {

					DateTime startDate = new DateTime(task.getStartDate());
					DateTime endDate = new DateTime(task.getEndDate());

					durationLabel.setText(Days.daysBetween(startDate, endDate).getDays() + " " + StaticCommonHelper.getCommonConstantsBundle().calculDurationDays() + " "
							+ Hours.hoursBetween(startDate, endDate).getHours() % 24 + ":" + Minutes.minutesBetween(startDate, endDate).getMinutes() % 60 + ":"
							+ Seconds.secondsBetween(startDate, endDate).getSeconds() % 60 + "");

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				durationLabel.setText(null);
			}

			errorMessageLabel.setText(task.getErrorMessage());
			if (StringUtils.isNotEmpty(task.getErrorMessage())) {
				showErrorButton(task.getErrorMessage().equals(EnumErrorMessages.DEFAULT_ERROR_MESSAGE_LIST.getMessage()));
			} else {
				showErrorButton(false);
			}
		}
	}

	private void showErrorButton(boolean b) {
		CardLayout layout = (CardLayout) cardPanel.getLayout();
		if (b) {
			layout.show(cardPanel, ERRORS_BUTTON);
		} else {
			layout.show(cardPanel, ERROR_MESSAGE_LABEL);
		}
	}

	protected JPanel createDetailsPanel(/* mxCell cell */) {
		FormLayout layout = new FormLayout(
				"RIGHT:MAX(40DLU;PREF):NONE,3DLU,FILL:100DLU:NONE,3DLU,RIGHT:MAX(30DLU;PREF):NONE,3DLU,FILL:100DLU:NONE,3DLU,RIGHT:MAX(40DLU;PREF):NONE,3DLU,FILL:80DLU:NONE,3DLU,RIGHT:MAX(40DLU;PREF):NONE,3DLU,FILL:80DLU:NONE,3DLU,RIGHT:MAX(30DLU;PREF):NONE,3DLU,FILL:60DLU:NONE,FILL:DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().meaning()), meaningLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().serviceCode()), codeLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().startDate()), startDateLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().endDate()), endDateLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().duration()), durationLabel);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().description()));
		builder.append(descriptionLabel, 18);
		builder.append(buildTitleLabel(StaticCommonHelper.getCommonConstantsBundle().errorMessage()));
		builder.append(cardPanel, 18);

		return builder.getPanel();
	}

	@Override
	protected VertexGraphCanvas createGraphCanvas() {
		return new MyGraphCanvas();
	}

	@Override
	protected mxGraph createGraph() {
		mxGraph mxGraph = new mxGraph() {
			@Override
			public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
				if (getModel().isVertex(state.getCell()) && canvas instanceof mxImageCanvas && ((mxImageCanvas) canvas).getGraphicsCanvas() instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state);
				} else if (getModel().isVertex(state.getCell()) && canvas instanceof VertexGraphCanvas) {
					((VertexGraphCanvas) canvas).drawVertex(state);
				} else {
					super.drawState(canvas, state, drawLabel);
				}
			}

			@Override
			public String convertValueToString(Object cell) {
				if (cell instanceof mxCell) {
					Object value = ((mxCell) cell).getValue();
					if (value instanceof ITask) {
						ITask task = (ITask) value;
						return getText(task);
					} else if (value instanceof MyGroup) {
						return "";
					} else if (value instanceof MyInterrogation) {
						return "";
					}
				}
				return super.convertValueToString(cell);
			}

			private String getText(ITask task) {
				if (task.getTaskType() != null) {
					return task.getTaskType().getMeaning() != null ? task.getTaskType().getMeaning() : task.getTaskType().getCode();
				} else {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(task.getObjectType());
					if (objectType != null) {
						return objectType.getShortName();
					} else {
						return task.getObjectType().getName();
					}
				}
			}

			// @Override public boolean isCellSelectable(Object o) {
			// if (o instanceof mxCell) {
			// mxCell cell = (mxCell) o;
			//
			// if (cell.isEdge()) {
			// return false;
			// }
			// }
			// return super.isCellSelectable(o);
			// }
		};

		mxGraph.setEnabled(false);
		mxGraph.setCellsEditable(false);
		mxGraph.setCellsLocked(true);
		mxGraph.setVertexLabelsMovable(false);
		mxGraph.setLabelsClipped(false);
		mxGraph.setCellsSelectable(false);
		return mxGraph;
	}

	public ITask findTaskInList(List<ITask> tasks, IId id) {
		ITask task = null;
		for (ITask iTask : tasks) {
			if (iTask.getId().equals(id)) {
				task = iTask;
			}
		}
		return task;
	}

	/**
	 * Create graph from task list (new version).
	 *
	 * @param tasks                 Tasks list
	 * @param assoTaskPreviousTasks Association task/previous tasks
	 */
	public void setTasks2(List<ITask> tasks, List<IAssoTaskPreviousTask> assoTaskPreviousTasks) {
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));

			if (tasks != null && !tasks.isEmpty()) {
				Map<String, Object> cellMap = new HashMap<String, Object>();

				// Create vertexes
				for (ITask task : tasks) {
					if (task.isCheckGroup()) {
						List<ITask> children = findTaskChildren(tasks, task.getId());
						if (children.isEmpty()) {
							if (TaskStatus.TODO.equals(task.getTaskStatus())) {
								Object v = graph.insertVertex(parent, null, new MyInterrogation(), 0, 0, CELL_SIZE, CELL_SIZE + LABEL_SIZE);
								cellMap.put(task.getId().toString(), v);
							}
						}
					} else {
						Object v = graph.insertVertex(parent, null, task, 0, 0, CELL_SIZE, CELL_SIZE + LABEL_SIZE);
						cellMap.put(task.getId().toString(), v);
					}
				}

				// Create edges
				for (ITask task : tasks) {
					Object v1 = cellMap.get(task.getId().toString());

					List<IId> idsOfPreviousTasks = getIdsOfPreviousTasks(tasks, assoTaskPreviousTasks, task);
					for (IId idPreviousTasks : idsOfPreviousTasks) {
						graph.insertEdge(parent, null, "", cellMap.get(idPreviousTasks.toString()), v1);
					}
				}
			}

			graphLayout.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	private List<IId> getIdsOfPreviousTasks(List<ITask> tasks, List<IAssoTaskPreviousTask> assoTaskPreviousTasks, ITask task) {
		List<IId> resultList = new ArrayList<IId>();
		List<IAssoTaskPreviousTask> previousTasks = ComponentHelper.findComponentsBy(assoTaskPreviousTasks, AssoTaskPreviousTaskFields.idTask().name(), task.getId());

		if (previousTasks.isEmpty()) {
			if (task.getIdParentTask() == null) {
				// Case first group, no predecessor
				return resultList;
			} else {
				// Case first task of group: get predecessor(s) of parent (= group) task.
				ITask parentTask = findTaskInList(tasks, task.getIdParentTask());
				return getIdsOfPreviousTasks(tasks, assoTaskPreviousTasks, parentTask);
			}
		}
		for (IAssoTaskPreviousTask iAssoTaskPreviousTask : previousTasks) {
			IId idPreviousTask = iAssoTaskPreviousTask.getIdPreviousTask();
			ITask previousTask = findTaskInList(tasks, idPreviousTask);
			if (previousTask.isCheckGroup()) {
				if (TaskStatus.TODO.equals(previousTask.getTaskStatus())) {
					// Case predecessor is a group that is not evaluated yet ( = question mark vertex)
					resultList.add(previousTask.getId());
				} else {
					// Find last childrens of task group
					List<ITask> children = findTaskChildren(tasks, previousTask.getId());
					if (children.isEmpty()) {
						resultList.addAll(getIdsOfPreviousTasks(tasks, assoTaskPreviousTasks, previousTask));
					}
					for (ITask child : children) {
						List<IAssoTaskPreviousTask> followers = ComponentHelper.findComponentsBy(assoTaskPreviousTasks, AssoTaskPreviousTaskFields.idPreviousTask().name(), child.getId());
						if (CollectionUtils.isEmpty(followers)) {
							resultList.add(child.getId());
						}
					}
				}
			} else {
				resultList.add(previousTask.getId());
			}
		}
		return resultList;
	}

	private List<ITask> findTaskChildren(List<ITask> tasks, IId idParentTask) {
		List<ITask> children = ComponentHelper.findComponentsBy(tasks, TaskFields.idParentTask().name(), idParentTask);
		return children != null ? children : Collections.<ITask>emptyList();
	}

	private class MyGroup {

		final ITask task;

		final boolean start;

		public MyGroup(ITask task, boolean start) {
			super();
			this.task = task;
			this.start = start;
		}
	}

	private class MyInterrogation {

	}

	private class MyGraphCanvas extends VertexGraphCanvas {

		private CellRendererPane cellRendererPane;

		private JLabel vertexRenderer;

		private JRingNode startCellNode;

		private JRingNode endCellNode;

		private JRingNode cellNode;

		public MyGraphCanvas() {
			super();

			startCellNode = new JRingNode(START_CELL_SIZE);
			startCellNode.setColorNode(JRingNode.ColorNode.blue);

			endCellNode = new JRingNode(END_CELL_SIZE);
			endCellNode.setColorNode(JRingNode.ColorNode.blue);

			cellNode = new JRingNode(CELL_SIZE);

			vertexRenderer = new JLabel();
			vertexRenderer.setHorizontalAlignment(JLabel.CENTER);
			vertexRenderer.setOpaque(false);
			vertexRenderer.setFont(new Font("Dialog", Font.PLAIN, 9));

			cellRendererPane = new CellRendererPane();
		}

		@Override
		public void drawVertex(mxCellState state) {
			Component c;
			String text;
			mxCell cell = (mxCell) state.getCell();
			if ("start".equals(cell.getId())) {
				c = startCellNode;
				text = state.getLabel();
			} else if ("end".equals(cell.getId())) {
				c = endCellNode;
				text = state.getLabel();
			} else if (cell.getValue() instanceof ITask) {
				ITask task = (ITask) cell.getValue();

				c = cellNode;
				if (task.getTaskStatus() != null) {
					switch (task.getTaskStatus()) {
					case TODO:
						cellNode.setColorNode(JRingNode.ColorNode.white);
						break;
					case DONE:
						cellNode.setColorNode(JRingNode.ColorNode.green);
						break;
					case CURRENT:
						cellNode.setColorNode(JRingNode.ColorNode.red);
						break;
					case CANCELED:
						cellNode.setColorNode(JRingNode.ColorNode.black);
						break;
					case SKIPPED:
						cellNode.setColorNode(JRingNode.ColorNode.yellow);
						break;
					default:
						break;
					}
				} else {
					cellNode.setColorNode(JRingNode.ColorNode.white);
				}

				if (task.getNature() != null) {
					switch (task.getNature()) {
					case DATA_CHECK:
						cellNode.setTypeNode(JRingNode.TypeNode.dataCheck);
						break;
					case ENRICHMENT:
						cellNode.setTypeNode(JRingNode.TypeNode.enrichment);
						break;
					case EXTERNAL_PROCESS:
						cellNode.setTypeNode(JRingNode.TypeNode.externalProcess);
						break;
					case MANUAL_ENRICHMENT:
						cellNode.setTypeNode(JRingNode.TypeNode.manualEnrichment);
						break;
					case UPDATE_STATUS:
						cellNode.setTypeNode(JRingNode.TypeNode.updateStatus);
						break;
					}
				} else {
					cellNode.setTypeNode(null);
				}

				text = state.getLabel();
			} else if (cell.getValue() instanceof MyGroup) {
				MyGroup group = (MyGroup) cell.getValue();
				ITask task = group.task;
				c = cellNode;
				if (task.getTaskStatus() != null) {
					switch (task.getTaskStatus()) {
					case TODO:
						cellNode.setColorNode(JRingNode.ColorNode.white);
						break;
					case DONE:
						cellNode.setColorNode(JRingNode.ColorNode.green);
						break;
					case CURRENT:
						cellNode.setColorNode(JRingNode.ColorNode.blue);
						break;
					case CANCELED:
						cellNode.setColorNode(JRingNode.ColorNode.black);
						break;
					case SKIPPED:
						cellNode.setColorNode(JRingNode.ColorNode.yellow);
						break;
					default:
						break;
					}
				} else {
					cellNode.setColorNode(JRingNode.ColorNode.white);
				}
				if (group.start) {
					cellNode.setTypeNode(JRingNode.TypeNode.startGroup);
				} else {
					cellNode.setTypeNode(JRingNode.TypeNode.endGroup);
				}
				text = state.getLabel();
			} else if (cell.getValue() instanceof MyInterrogation) {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(JRingNode.TypeNode.interrogation);
				text = state.getLabel();
			} else {
				c = cellNode;
				cellNode.setColorNode(JRingNode.ColorNode.white);
				cellNode.setTypeNode(null);
				text = state.getLabel();
			}
			cellRendererPane.paintComponent(g, c, JTasksGraphComponent.this, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(),
					(int) state.getWidth(), true);

			vertexRenderer.setText(text);
			Dimension pref = vertexRenderer.getPreferredSize();
			int width = (int) Math.min(state.getWidth() * 2, pref.width);
			int x = (int) (state.getX() + translate.x + state.getWidth() / 2) - width / 2;
			cellRendererPane.paintComponent(g, vertexRenderer, JTasksGraphComponent.this, x, (int) state.getY() + translate.y + (int) state.getWidth(), width, pref.height, true);
		}
	}
}
