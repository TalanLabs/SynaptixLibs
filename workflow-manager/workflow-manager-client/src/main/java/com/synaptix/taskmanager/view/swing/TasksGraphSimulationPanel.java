package com.synaptix.taskmanager.view.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.taskmanager.controller.SimulationTasksGraphController;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskManagerContext;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITasksGraphSimulationView;
import com.synaptix.taskmanager.view.swing.tasksgraph.JTasksGraphSimulationComponent;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;

public class TasksGraphSimulationPanel extends WaitComponentFeedbackPanel implements ITasksGraphSimulationView, IDockable, IDockingContextView {

	private static final long serialVersionUID = 4302904570832535212L;

	public static final String ID_DOCKABLE = TasksGraphSimulationPanel.class.getName();

	private final SimulationTasksGraphController tasksGraphSimulationController;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private JTasksGraphSimulationComponent graphComponent;

	private LabelsPanel labelsPanel;

	private JComboBox taskObjectClassBox;

	private NewSimulationAction newSimulationAction;

	private JButton editFilters;

	private JLabel contextLabel;

	private Class<? extends ITaskObject<?>> currentObjectType;

	public TasksGraphSimulationPanel(SimulationTasksGraphController tasksGraphSimulationController) {
		super();

		this.tasksGraphSimulationController = tasksGraphSimulationController;

		initialize();

		this.addContent(buildContents());
	}

	private void initialize() {
		graphComponent = new JTasksGraphSimulationComponent();
		labelsPanel = new LabelsPanel();
		taskObjectClassBox = TaskObjectClassViewHelper.createTaskObjectClassComboBox(false);
		newSimulationAction = new NewSimulationAction();
		taskObjectClassBox.addActionListener(newSimulationAction);
		editFilters = new JButton(new AbstractAction(StaticHelper.getTaskManagerConstantsBundle().editContext()) {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				tasksGraphSimulationController.editFilters(labelsPanel.getContext(), currentObjectType);
			}
		});
		contextLabel = new JLabel(StaticHelper.getTaskManagerConstantsBundle().context() + " :");
		contextLabel.setFont(contextLabel.getFont().deriveFont(Font.BOLD));
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:3DLU:NONE,RIGHT:DEFAULT:NONE",
				"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append(StaticHelper.getStatusGraphTableConstantsBundle().objectType(), taskObjectClassBox);
		builder.nextLine(2);
		builder.append(contextLabel);
		builder.append(labelsPanel, 3);
		builder.append(editFilters);
		builder.nextLine(2);
		builder.append(graphComponent, 7);
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

		dockKey = new DockKey(ID_DOCKABLE, StaticHelper.getTaskManagerConstantsBundle().simulationManagement());
		dockingContext.registerDockable(this);
	}

	@Override
	public String getCategory() {
		return "taskManager";
	}

	@Override
	public void showView() {
		if (dockingContext != null) {
			dockingContext.showDockable(ID_DOCKABLE);
		}
	}

	@Override
	public void updateSimulation(List<IStatusGraph> statusGraphs, List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias, ITaskManagerContext context,
			Class<? extends ITaskObject<?>> objectType) {
		graphComponent.setGraphRule(statusGraphs, taskChainCriterias, objectType);
		labelsPanel.setContext(context);
		taskObjectClassBox.removeActionListener(newSimulationAction);
		taskObjectClassBox.setSelectedItem(objectType);
		currentObjectType = objectType;
		taskObjectClassBox.addActionListener(newSimulationAction);
	}

	private class LabelsPanel extends JPanel {

		private static final long serialVersionUID = -602224942302779787L;

		private ITaskManagerContext context;

		private JLabel principalThirdPartyLabel;

		private JLabel communityLabel;

		private JLabel custOfferLabel;

		private JLabel notifiedThirdPartyLabel;

		private JLabel followupTypeLabel;

		private JLabel incidentLevelLabel;

		private JLabel objectTypeLabel;

		public LabelsPanel() {
			super();

			initComponents();

			buildContent();
		}

		private void initComponents() {

			principalThirdPartyLabel = new JLabel();
			communityLabel = new JLabel();
			custOfferLabel = new JLabel();
			notifiedThirdPartyLabel = new JLabel();
			followupTypeLabel = new JLabel();
			incidentLevelLabel = new JLabel();
			objectTypeLabel = new JLabel();
		}

		private void buildContent() {
			FormLayout layout = new FormLayout(
					"FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:5DLU:NONE,FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:GROW(1.0)",
					"FILL:DEFAULT:GROW(1.0)");
			DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
			if (principalThirdPartyLabel.getText() != null && !principalThirdPartyLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getTransportRequestTaskManagerContextTableConstantsBundle().principal_name() + " :", principalThirdPartyLabel);
				builder.nextColumn(2);
			}
			if (custOfferLabel.getText() != null && !custOfferLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getTransportRequestTaskManagerContextTableConstantsBundle().customerOffer_meaning() + " :", custOfferLabel);
				builder.nextColumn(2);
			}
			if (communityLabel.getText() != null && !communityLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getTransportRequestTaskManagerContextTableConstantsBundle().community_label() + " :", communityLabel);
				builder.nextColumn(2);
			}
			if (notifiedThirdPartyLabel.getText() != null && !notifiedThirdPartyLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getEventFollowupTaskManagerContextTableConstantsBundle().notifiedThirdParty_name() + " :", notifiedThirdPartyLabel);
				builder.nextColumn(2);
			}
			if (followupTypeLabel.getText() != null && !followupTypeLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getEventFollowupTaskManagerContextTableConstantsBundle().eventFollowupType() + " :", followupTypeLabel);
				builder.nextColumn(2);
			}
			if (incidentLevelLabel.getText() != null && !incidentLevelLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getEventFollowupTaskManagerContextTableConstantsBundle().incidentLevel() + " :", incidentLevelLabel);
				builder.nextColumn(2);
			}
			if (objectTypeLabel.getText() != null && !objectTypeLabel.getText().isEmpty()) {
				builder.append(StaticHelper.getEventFollowupTaskManagerContextTableConstantsBundle().businessObjectKind() + " :", objectTypeLabel);
				builder.nextColumn(2);
			}
		}

		public void setContext(ITaskManagerContext context) {
			this.context = context;
			refresh();
		}

		private void refresh() {
//			if (context instanceof ITransportRequestTaskManagerContext) {
//				ITransportRequestTaskManagerContext taskManagerContext = (ITransportRequestTaskManagerContext) context;
//				principalThirdPartyLabel.setText(taskManagerContext.getPrincipalThirdParty() != null ? taskManagerContext.getPrincipalThirdParty().getName() : null);
//				communityLabel.setText(taskManagerContext.getCommunity() != null ? taskManagerContext.getCommunity().getCode() : null);
//				custOfferLabel.setText(taskManagerContext.getCustOffer() != null ? taskManagerContext.getCustOffer().getOfferCode() : null);
//				notifiedThirdPartyLabel.setText(null);
//				followupTypeLabel.setText(null);
//				incidentLevelLabel.setText(null);
//				objectTypeLabel.setText(null);
//			} else if (context instanceof ICustomerOrderTaskManagerContext) {
//				ICustomerOrderTaskManagerContext taskManagerContext = (ICustomerOrderTaskManagerContext) context;
//				principalThirdPartyLabel.setText(taskManagerContext.getPrincipalThirdParty() != null ? taskManagerContext.getPrincipalThirdParty().getName() : null);
//				communityLabel.setText(taskManagerContext.getCommunity() != null ? taskManagerContext.getCommunity().getCode() : null);
//				custOfferLabel.setText(taskManagerContext.getCustOffer() != null ? taskManagerContext.getCustOffer().getOfferCode() : null);
//				notifiedThirdPartyLabel.setText(null);
//				followupTypeLabel.setText(null);
//				incidentLevelLabel.setText(null);
//				objectTypeLabel.setText(null);
//			} else if (context instanceof IExecutionOrderTaskManagerContext) {
//				IExecutionOrderTaskManagerContext taskManagerContext = (IExecutionOrderTaskManagerContext) context;
//				principalThirdPartyLabel.setText(taskManagerContext.getPrincipalThirdParty() != null ? taskManagerContext.getPrincipalThirdParty().getName() : null);
//				communityLabel.setText(taskManagerContext.getCommunity() != null ? taskManagerContext.getCommunity().getCode() : null);
//				custOfferLabel.setText(taskManagerContext.getCustOffer() != null ? taskManagerContext.getCustOffer().getOfferCode() : null);
//				notifiedThirdPartyLabel.setText(null);
//				followupTypeLabel.setText(null);
//				incidentLevelLabel.setText(null);
//				objectTypeLabel.setText(null);
//			} else if (context instanceof IEventFollowupTaskManagerContext) {
//				IEventFollowupTaskManagerContext taskManagerContext = (IEventFollowupTaskManagerContext) context;
//				principalThirdPartyLabel.setText(null);
//				communityLabel.setText(null);
//				custOfferLabel.setText(null);
//				notifiedThirdPartyLabel.setText(taskManagerContext.getNotifiedThirdParty() != null ? taskManagerContext.getNotifiedThirdParty().getName() : null);
//				followupTypeLabel.setText(taskManagerContext.getFollowupType() != null ? taskManagerContext.getFollowupType().getMeaning() : null);
//				incidentLevelLabel.setText(taskManagerContext.getIncidentLevel() != null ? taskManagerContext.getIncidentLevel().getMeaning() : null);
//				objectTypeLabel.setText(taskManagerContext.getObjectType() != null ? taskManagerContext.getObjectType().getMeaning() : null);
//			}
			// TODO abstract
			removeAll();
			buildContent();
		}

		public ITaskManagerContext getContext() {
			return context;
		}

	}

	private class NewSimulationAction implements ActionListener {

		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent e) {
			tasksGraphSimulationController.showFilters((Class<? extends ITaskObject<?>>) taskObjectClassBox.getSelectedItem());
		}
	}

}
