package com.synaptix.deployer.client.view.swing.deployerManagement;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.InstructionsNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode.NodeType;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.instructions.SynaptixInstructions;
import com.synaptix.deployer.model.IEnvironmentState;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public class InstructionsPanel extends AbstractManagementPanel<InstructionsNode> {

	private static final long serialVersionUID = 7756188383728758058L;

	private static final ResizableIcon refreshIcon = ImageWrapperResizableIcon.getIcon(InstructionsPanel.class.getResource("/images/deployer/refresh.png"), new Dimension(18, 18));

	private JLabel stateLabel;

	private JLabel checkLabel;

	private MyCheckboxGroup stopCheck;

	private MyCheckboxGroup renameCheck;

	private MyCheckboxGroup downloadCheck;

	private JCheckBox playScriptsCheck;

	private MyCheckboxGroup launchCheck;

	private JButton nextStepButton;

	private List<MyCheckboxGroup> checkboxGroupList;

	private JLabel stopLabel;

	private JLabel renameLabel;

	private JLabel downloadLabel;

	private JLabel playScriptsLabel;

	private JLabel launchLabel;

	public InstructionsPanel(InstructionsNode node, DeployerManagementController managementController) {
		super(node, managementController);

		updateInstructions();
	}

	@Override
	protected void initComponents() {
		stateLabel = new JLabel(StaticHelper.getDeployerManagementConstantsBundle().state("?"));
		checkLabel = new JLabel(refreshIcon);
		checkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		checkLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				getManagementController().checkState(new IResultCallback<IEnvironmentState>() {

					@Override
					public void setResult(IEnvironmentState environmentState) {
						updateDependingOnState(environmentState);
					}
				}, getManagementController().getEnvironment());
			}
		});

		stopLabel = new JLabel(bold(StaticHelper.getDeployerManagementConstantsBundle().stopInstances()));
		renameLabel = new JLabel(bold(StaticHelper.getDeployerManagementConstantsBundle().renameOld()));
		downloadLabel = new JLabel(bold(StaticHelper.getDeployerManagementConstantsBundle().downloadNew()));
		playScriptsLabel = new JLabel(bold(StaticHelper.getDeployerManagementConstantsBundle().playScripts()));
		launchLabel = new JLabel(bold(StaticHelper.getDeployerManagementConstantsBundle().launch()));

		stopCheck = new MyCheckboxGroup(StaticHelper.getDeployerManagementConstantsBundle().stopAll());
		renameCheck = new MyCheckboxGroup(StaticHelper.getDeployerManagementConstantsBundle().renameAll());
		downloadCheck = new MyCheckboxGroup(StaticHelper.getDeployerManagementConstantsBundle().downloadAll());
		playScriptsCheck = new JCheckBox(StaticHelper.getDeployerManagementConstantsBundle().playScripts());
		launchCheck = new MyCheckboxGroup(StaticHelper.getDeployerManagementConstantsBundle().launchAll());

		checkboxGroupList = new ArrayList<MyCheckboxGroup>();
		checkboxGroupList.add(stopCheck);
		checkboxGroupList.add(renameCheck);
		checkboxGroupList.add(downloadCheck);
		checkboxGroupList.add(launchCheck);

		ISynaptixEnvironment environment = getManagementController().getEnvironment();
		List<ISynaptixDatabaseSchema> dbList = getManagementController().getSupportedDbsForEnvironment(environment);
		playScriptsCheck.setEnabled(environment != null && environment.getSqlPlusPath() != null && dbList != null && !dbList.isEmpty());

		nextStepButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().nextStep());
		nextStepButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -6679798746079480745L;

			@Override
			public void actionPerformed(ActionEvent e) {
				getManagementController().selectInstructions(getNode(), buildInstructions());
			}
		});
		playScriptsCheck.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -8957365221339084814L;

			@Override
			public void actionPerformed(ActionEvent e) {
				updateInstructions();
				updateNextButton(NodeType.SCRIPTS);
			}
		});
	}

	private SynaptixInstructions buildInstructions() {
		SynaptixInstructions synaptixInstructions = new SynaptixInstructions();
		synaptixInstructions.addStopInstances(checkboxGroupList.get(0).getSelectedInstanceList());
		synaptixInstructions.addRenameInstances(checkboxGroupList.get(1).getSelectedInstanceList());
		synaptixInstructions.addDownloadInstances(checkboxGroupList.get(2).getSelectedInstanceList());
		synaptixInstructions.playScripts(playScriptsCheck.isSelected());
		synaptixInstructions.addLaunchInstances(checkboxGroupList.get(3).getSelectedInstanceList());
		return synaptixInstructions;
	}

	private String bold(String text) {
		return "<html><b>" + text + "</b></html>";
	}

	private void updateDependingOnState(IEnvironmentState environmentState) {
		String stateText = StaticHelper.getDeployerManagementConstantsBundle().state("?");
		if (environmentState != null) {
			if ((environmentState.getRunningInstanceSet() == null) || (environmentState.getRunningInstanceSet().size() == 0)) {
				stateText = StaticHelper.getDeployerManagementConstantsBundle().state(StaticHelper.getDeployerManagementConstantsBundle().stopped());
			} else {
				StringBuilder builder = new StringBuilder(StaticHelper.getDeployerManagementConstantsBundle().state(
						StaticHelper.getDeployerManagementConstantsBundle().launched(environmentState.getRunningInstanceSet().size())));
				boolean first = true;
				builder.append("        ");
				for (IEnvironmentInstance instance : environmentState.getRunningInstanceSet()) {
					if (!first) {
						builder.append(", ");
					}
					builder.append(instance.getName());
					first = false;
				}
				stateText = builder.toString();
			}
		}
		stateLabel.setText(stateText);
	}

	@Override
	protected JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,15DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(checkLabel, cc.xy(1, 1));
		builder.add(stateLabel, cc.xy(3, 1));
		builder.add(buildInstructionsPanel(), cc.xyw(1, 3, 3));

		return builder.getPanel();
	}

	private Component buildInstructionsPanel() {
		FormLayout layout = new FormLayout(
				"FILL:20DLU:NONE,FILL:DEFAULT:GROW(0.2),3DLU,FILL:20DLU:NONE,FILL:DEFAULT:GROW(0.2),3DLU,FILL:20DLU:NONE,FILL:DEFAULT:GROW(0.2),3DLU,FILL:20DLU:NONE,FILL:DEFAULT:GROW(0.2),3DLU,FILL:20DLU:NONE,FILL:DEFAULT:GROW(0.2)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.append(stopLabel, 2);
		builder.append(renameLabel, 2);
		builder.append(downloadLabel, 2);
		builder.append(playScriptsLabel, 2);
		builder.append(launchLabel, 2);

		builder.append(stopCheck, 2);
		builder.append(renameCheck, 2);
		builder.append(downloadCheck, 2);
		builder.append(playScriptsCheck, 2);
		builder.append(launchCheck, 2);
		ISynaptixEnvironment environment = getManagementController().getEnvironment();

		int r = 0;
		for (IEnvironmentInstance instance : environment.getInstances()) {
			// builder.appendRow("3DLU");
			builder.appendRow("FILL:DEFAULT:NONE");
			builder.nextRow();
			builder.setColumn(2);
			for (int i = 1; i <= 4; i++) {
				builder.append(new MyLittleCheckbox(checkboxGroupList.get(i - 1), r, instance, (i == 3)));
				if (i == 3) {
					builder.nextColumn(3);
				}
				builder.nextColumn();
			}
			r++;
		}

		builder.appendRow("15DLU");
		builder.appendRow("CENTER:DEFAULT:NONE");
		builder.nextRow(2);
		builder.setColumn(4);
		builder.append(nextStepButton, 8);

		return builder.getPanel();
	}

	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);

		nextStepButton.setEnabled(!running); // TODO improve (enable if not last step)
	}

	private void updateNextButton(NodeType node) {
		boolean oneSelected = !checkboxGroupList.get(2).getSelectedInstanceList().isEmpty();
		if ((oneSelected) || (playScriptsCheck.isSelected())) {
			nextStepButton.setEnabled(true);
			nextStepButton.setText(StaticHelper.getDeployerManagementConstantsBundle().nextStep());
		} else {
			nextStepButton.setText(StaticHelper.getDeployerManagementConstantsBundle().run());
			nextStepButton.setEnabled(!getManagementController().isRunning());
		}
		getManagementController().checkFinalStep(node);
	}

	private void updateInstructions() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				getManagementController().setInstructions(buildInstructions());
			}
		});
	}

	@Override
	public void updateValidation() {
	}

	private final class MyCheckboxGroup extends JCheckBox {

		private static final long serialVersionUID = -8333985322408606308L;

		private List<MyLittleCheckbox> littleCheckboxList;

		public MyCheckboxGroup(String title) {
			super(title, true);

			this.littleCheckboxList = new ArrayList<MyLittleCheckbox>();

			addActionListener(new AbstractAction() {

				private static final long serialVersionUID = 876634375311254803L;

				@Override
				public void actionPerformed(ActionEvent e) {
					for (MyLittleCheckbox littleCheckbox : littleCheckboxList) {
						littleCheckbox.setSelected(isSelected());
					}
					for (MyLittleCheckbox littleCheckbox : littleCheckboxList) {
						littleCheckbox.updateSelection();
					}
					updateInstructions();
				}
			});
		}

		public void addLittleCheckbox(MyLittleCheckbox littleCheckbox) {
			littleCheckboxList.add(littleCheckbox);
		}

		public List<MyLittleCheckbox> getLittleCheckboxList() {
			return littleCheckboxList;
		}

		public Set<IEnvironmentInstance> getSelectedInstanceList() {
			Set<IEnvironmentInstance> selectedInstanceList = new HashSet<IEnvironmentInstance>();
			for (MyLittleCheckbox littleCheckbox : littleCheckboxList) {
				if (littleCheckbox.isSelected()) {
					selectedInstanceList.add(littleCheckbox.getInstance());
				}
			}
			return selectedInstanceList;
		}
	}

	private final class MyLittleCheckbox extends JCheckBox {

		private static final long serialVersionUID = 1180535772931416947L;

		private MyCheckboxGroup checkboxGroup;

		private IEnvironmentInstance instance;

		private boolean extra;

		// public MyLittleCheckbox(MyCheckboxGroup checkboxGroup, final int rank, IEnvironmentInstance instance) {
		// this(checkboxGroup, rank, instance, false);
		// }

		public MyLittleCheckbox(MyCheckboxGroup checkboxGroup, final int rank, IEnvironmentInstance instance, boolean extra) {
			super(instance.getName(), true);

			this.checkboxGroup = checkboxGroup;
			this.instance = instance;
			this.extra = extra;

			checkboxGroup.addLittleCheckbox(this);

			addActionListener(new AbstractAction() {

				private static final long serialVersionUID = 4556042737553561617L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
						updateSelection();
					} else {
						for (MyCheckboxGroup checkboxGroup : checkboxGroupList) {
							MyLittleCheckbox littleCheckbox = checkboxGroup.getLittleCheckboxList().get(rank);
							littleCheckbox.setSelected(isSelected());
							littleCheckbox.updateSelection();
						}
					}
					updateInstructions();
				}
			});
		}

		public IEnvironmentInstance getInstance() {
			return instance;
		}

		public void updateSelection() {
			boolean allSelected = true;
			for (MyLittleCheckbox littleCheckbox : checkboxGroup.getLittleCheckboxList()) {
				if (!littleCheckbox.isSelected()) {
					allSelected = false;
				}
			}
			if (extra) {
				updateNextButton(NodeType.BUILD);
			}
			checkboxGroup.setSelected(allSelected);
		}
	}
}
