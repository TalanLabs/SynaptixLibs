package com.synaptix.deployer.client.view.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.BuildNode;
import com.synaptix.deployer.client.deployerManagement.EnvironmentNode;
import com.synaptix.deployer.client.deployerManagement.InstructionsNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode.NodeType;
import com.synaptix.deployer.client.deployerManagement.PlayScriptsNode;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IDeployerManagementView;
import com.synaptix.deployer.client.view.swing.deployerManagement.AbstractManagementPanel;
import com.synaptix.deployer.client.view.swing.deployerManagement.BuildPanel;
import com.synaptix.deployer.client.view.swing.deployerManagement.EnvironmentPanel;
import com.synaptix.deployer.client.view.swing.deployerManagement.InstructionsPanel;
import com.synaptix.deployer.client.view.swing.deployerManagement.ManagementTitle;
import com.synaptix.deployer.client.view.swing.deployerManagement.PlayScriptsPanel;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.instructions.SynaptixInstructions;
import com.synaptix.deployer.model.StepEnum;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.view.swing.JSyCarouselPanel;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

/* package protected */class DeployerManagementPanel extends WaitComponentFeedbackPanel implements IRibbonContextView, IDockable, IDockingContextView, IDeployerManagementView {

	private static final long serialVersionUID = 5949581171612491402L;

	private static final Icon waitIcon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/wait.png"), new Dimension(18, 18));

	private static final Icon skipIcon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/skip.png"), new Dimension(18, 18));

	private static final Icon doneIcon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/done.png"), new Dimension(18, 18));

	private static final Icon cancelIcon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/cancel.png"), new Dimension(18, 18));

	private static final Icon playIcon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/play.png"), new Dimension(18, 18));

	private final DeployerManagementController deployerManagementController;

	// private final Map<ManagementNode, AbstractManagementPanel<?>> managementMap;

	private final List<AbstractManagementPanel<?>> managementPanelList;

	private final List<ManagementTitle> managementTitleList;

	private final Map<String, MyTrackingLabel> trackingLabelMap;

	private final StringBuilder log;

	private RibbonContext ribbonContext;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private JSyCarouselPanel carousel;

	private JPanel swingTitle;

	private JButton showLogButton;

	private JPanel resultPanel;

	public DeployerManagementPanel(DeployerManagementController deployerManagementController) {
		super();

		this.deployerManagementController = deployerManagementController;

		// this.managementMap = new HashMap<ManagementNode, AbstractManagementPanel<?>>();
		this.managementPanelList = new ArrayList<AbstractManagementPanel<?>>();
		this.managementTitleList = new ArrayList<ManagementTitle>();
		this.trackingLabelMap = new HashMap<String, MyTrackingLabel>();
		this.log = new StringBuilder();

		initComponents();

		this.addContent(buildContent());
	}

	private void initComponents() {
		carousel = new JSyCarouselPanel(null, true);
		carousel.setShowDotIfAlone(false);
		swingTitle = new JPanel();
		showLogButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().showLog());
		showLogButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = 7262218368511613082L;

			@Override
			public void actionPerformed(ActionEvent e) {
				deployerManagementController.showLog(log.toString());
			}
		});

		carousel.addPropertyChangeListener(JSyCarouselPanel.PAGE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				pageSelected((Integer) evt.getNewValue());
			}
		});

		resultPanel = new JPanel();
	}

	private Component buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildInputPanel(), buildResultPanel());
		split.setResizeWeight(0.75);
		builder.add(split, cc.xy(1, 1));
		return builder.getPanel();
	}

	private Component buildInputPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(swingTitle, cc.xy(1, 1));
		builder.add(carousel, cc.xy(1, 3));

		return builder.getPanel();
	}

	private Component buildResultPanel() {
		initResultPanel(null, null);
		return resultPanel;
		// FormLayout layout = new FormLayout(
		// "FILL:MAX(15DLU;DEFAULT):GROW(0.25),FILL:DEFAULT:NONE,3DLU,FILL:MAX(15DLU;DEFAULT):GROW(0.25),FILL:DEFAULT:NONE,3DLU,FILL:MAX(15DLU;DEFAULT):GROW(0.25),FILL:DEFAULT:NONE,3DLU,FILL:MAX(15DLU;DEFAULT):GROW(0.25),FILL:DEFAULT:NONE");
		// DefaultFormBuilder builder = new DefaultFormBuilder(layout, resultPanel);
		// builder.setDefaultDialogBorder();
		//
		// return builder.getPanel();
	}

	@Override
	public void initResultPanel(ISynaptixEnvironment environment, SynaptixInstructions instructions) {
		resultPanel.removeAll();
		log.delete(0, log.length());
		showLogButton.setVisible(false);
		trackingLabelMap.clear();

		FormLayout layout = new FormLayout(
				"FILL:MAX(15DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.20),3DLU,FILL:MAX(15DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.20),3DLU,FILL:MAX(15DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.20),3DLU,FILL:MAX(15DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.20),3DLU,FILL:MAX(15DLU;DEFAULT):NONE,FILL:DEFAULT:GROW(0.20)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, resultPanel);
		builder.setDefaultDialogBorder();

		if (environment != null) {
			String[] titles = new String[] { StaticHelper.getDeployerManagementConstantsBundle().stopTitle(), StaticHelper.getDeployerManagementConstantsBundle().renameTitle(),
					StaticHelper.getDeployerManagementConstantsBundle().downloadTitle(), StaticHelper.getDeployerManagementConstantsBundle().scripts(),
					StaticHelper.getDeployerManagementConstantsBundle().launchTitle() };
			builder.appendRow("FILL:DEFAULT:NONE");
			for (int i = 0; i < 5; i++) {
				builder.add(new JLabel("<html><b>" + titles[i] + "</b></html>"), new CellConstraints(i * 3 + 1, 1, 2, 1));
				if (i == 3) {
					MyTrackingLabel trackingLabel = new MyTrackingLabel(titles[i], i, instructions.isPlayScripts());
					String key = "null_" + Integer.toString(i + 1);
					trackingLabelMap.put(key, trackingLabel);
					builder.add(trackingLabel, new CellConstraints((i + 1) * 3 - 1, 1 * 2 + 1));
				} else {
					for (int j = 0; j < environment.getInstances().size(); j++) {
						IEnvironmentInstance instance = environment.getInstances().get(j);
						Set<IEnvironmentInstance> concernedInstanceSet = null;
						if (i == 0) {
							concernedInstanceSet = instructions.getStopInstanceSet();
							builder.appendRow("3DLU");
							builder.appendRow("FILL:DEFAULT:NONE");
						} else if (i == 1) {
							concernedInstanceSet = instructions.getRenameInstanceSet();
						} else if (i == 2) {
							concernedInstanceSet = instructions.getDownloadInstanceSet();
						} else if (i == 4) {
							concernedInstanceSet = instructions.getLaunchInstanceSet();
						}
						MyTrackingLabel trackingLabel = new MyTrackingLabel(instance.getName(), i, concernedInstanceSet.contains(instance));
						String key = instance.getName() + "_" + Integer.toString(i + 1);
						trackingLabelMap.put(key, trackingLabel);
						builder.add(trackingLabel, new CellConstraints((i + 1) * 3 - 1, (j + 1) * 2 + 1));
					}
				}
			}
			builder.setRow(environment.getInstances().size() * 2 + 2);
			builder.appendRelatedComponentsGapRow();
			builder.appendRow("FILL:DEFAULT:NONE");
			builder.nextLine();
			builder.nextColumn(6);
			builder.append(showLogButton, 2);
		}
		resultPanel.doLayout();
		resultPanel.repaint();
	}

	// private JPanel createEnvironmentPage() {
	// FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
	// DefaultFormBuilder builder = new DefaultFormBuilder(layout);
	//
	// deployerManagementController.getSupportedEnvironments();
	//
	// return builder.getPanel();
	// }

	public String getIdDockable() {
		return this.getClass().getName();
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
	public String getCategory() {
		return "DEPLOYER";
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;

		dockKey = new DockKey(getIdDockable(), StaticHelper.getDeployerManagementConstantsBundle().deployer());
		dockKey.setFloatEnabled(true);
		dockKey.setAutoHideEnabled(false);

		dockingContext.registerDockable(this);

		dockingContext.addDockableStateChangeListener(this, new DockableStateChangeListener() {
			@Override
			public void dockableStateChanged(DockableStateChangeEvent event) {
			}
		});
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(DeployerManagementPanel.class.getResource("/images/deployer/upload.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getDeployerManagementConstantsBundle().deployer(), icon);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockingContext.showDockable(getIdDockable());
			}
		});

		String ribbonTaskTitle = StaticHelper.getDeployerConstantsBundle().exploitation();
		String ribbonBandTitle = StaticHelper.getDeployerConstantsBundle().exploitation();

		RibbonTaskBuilder ribbonTaskBuilder = ribbonContext.addRibbonTask(ribbonTaskTitle, 0);
		RibbonBandBuilder ribbonBand = ribbonTaskBuilder.addRibbonBand(ribbonBandTitle, 2);
		ribbonBand.addCommandeButton(cb, RibbonElementPriority.TOP);
	}

	@Override
	public void exploreNode(ManagementNode node) {
		int level = node.getLevel();
		if (level < managementPanelList.size()) {
			if (!sameHierarchy(node)) {
				Iterator<AbstractManagementPanel<?>> ite = managementPanelList.iterator();
				int i = 0;
				while (ite.hasNext()) {
					ite.next();
					if (i >= level) {
						ite.remove();
						managementTitleList.remove(level);
					}
					i++;
				}
				addComponentPanel(node);
				// } else {
				// carousel.setPage(level);
			}
		} else {
			addComponentPanel(node);
		}
		carousel.goToPage(level + 1);
		pageSelected(level + 1);
	}

	private boolean sameHierarchy(ManagementNode node) {
		if (node == null) {
			return true;
		}
		return (node.getKey() != null) && (node.getKey().equals(managementPanelList.get(node.getLevel()).getNode().getKey())) && sameHierarchy(node.getParentNode());
	}

	private void pageSelected(int page) {
		for (ManagementTitle explorerTitle : managementTitleList) {
			explorerTitle.setActive(explorerTitle.getRank() == page);
			explorerTitle.setExplored(explorerTitle.getRank() <= page);
		}
		int i = 0;
		int m = managementPanelList.size();
		while (i < m) {
			AbstractManagementPanel<?> managementPanel = managementPanelList.get(i);
			managementPanel.setActive(i == page - 1);
			i++;
		}
		updateTitle();
	}

	private void updateCarousel() {
		JComponent[] components = new JComponent[managementPanelList.size()];
		for (int i = 0; i < managementPanelList.size(); i++) {
			components[i] = managementPanelList.get(i);
		}
		carousel.setComponents(components);
	}

	@SuppressWarnings("unchecked")
	private <N extends ManagementNode, M extends AbstractManagementPanel<N>> void addComponentPanel(N node) {
		M managementPanel = null;
		if (EnvironmentNode.class.isAssignableFrom(node.getClass())) {
			managementPanel = (M) new EnvironmentPanel((EnvironmentNode) node, deployerManagementController);
		} else if (InstructionsNode.class.isAssignableFrom(node.getClass())) {
			managementPanel = (M) new InstructionsPanel((InstructionsNode) node, deployerManagementController);
		} else if (PlayScriptsNode.class.isAssignableFrom(node.getClass())) {
			managementPanel = (M) new PlayScriptsPanel((PlayScriptsNode) node, deployerManagementController);
		} else if (BuildNode.class.isAssignableFrom(node.getClass())) {
			managementPanel = (M) new BuildPanel((BuildNode) node, deployerManagementController);
		}
		if (managementPanel != null) {
			managementPanelList.add(managementPanel);
			managementTitleList.add(new ManagementTitle(node, deployerManagementController));
			updateCarousel();
		}
	}

	@Override
	public boolean hasError() {
		boolean isOK = true;
		for (AbstractManagementPanel<?> managementPanel : managementPanelList) {
			isOK = isOK && managementPanel.hasError();
		}
		return isOK;
	}

	private void updateTitle() {
		swingTitle.removeAll();
		int size = managementTitleList.size() * 2 - 1;
		StringBuilder columnSpec = new StringBuilder();
		for (int i = 0; i < size; i++) {
			columnSpec.append("FILL:DEFAULT:NONE");
			if (i != size - 1) {
				columnSpec.append(",3DLU,");
			}
		}
		FormLayout layout = new FormLayout(columnSpec.toString(), "FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout, swingTitle);
		CellConstraints cc = new CellConstraints();
		int i = 1;
		int j = 1;
		for (ManagementTitle title : managementTitleList) {
			title.updateName();
			builder.add(title, cc.xy(j, 1));
			j = j + 2;
			i++;
			if (i < size) {
				builder.addLabel("»", cc.xy(j, 1));
				j = j + 2;
				i++;
			}
		}
		swingTitle.validate();
		swingTitle.repaint();
	}

	@Override
	public void checkFinalStep(NodeType nodeToCheck) { // TODO
		if (managementPanelList.size() > 2) {
			Integer level = null;
			for (AbstractManagementPanel<?> managementPanel : managementPanelList) {
				ManagementNode node = (managementPanel.getNode());
				if (nodeToCheck.equals(node.getType())) {
					level = ((ManagementNode) managementPanel.getNode()).getLevel();
				}
			}
			// if ((build) && (!NodeType.BUILD.equals(managementPanelList.get(2).getNode().getType()))) {
			// proceed = true;
			// }
			// if ((!build) && (NodeType.BUILD.equals(managementPanelList.get(2).getNode().getType()))) {
			// proceed = true;
			// }
			if (level != null) {
				int rank = level + 1;
				Iterator<AbstractManagementPanel<?>> ite = managementPanelList.iterator();
				int i = 1;
				boolean removed = false;
				while (ite.hasNext()) {
					ite.next();
					if (i >= rank) {
						ite.remove();
						removed = true;
					}
					i++;
				}
				Iterator<ManagementTitle> ite2 = managementTitleList.iterator();
				i = 1;
				while (ite2.hasNext()) {
					ite2.next();
					if (i >= rank) {
						ite2.remove();
						removed = true;
					}
					i++;
				}
				if (removed) {
					// carousel.goToPage(rank - 1);
					updateCarousel();
					updateTitle();
				}
			}
		}
	}

	@Override
	public void setRunning(boolean running) {
		for (AbstractManagementPanel<?> panel : managementPanelList) {
			panel.setRunning(running);
		}
	}

	@Override
	public void markDone(IEnvironmentInstance instance, StepEnum stepEnum) {
		MyTrackingLabel myTrackingLabel = getTrackingLabel(instance, stepEnum);
		if (myTrackingLabel != null) {
			myTrackingLabel.markDone();
		}
	}

	@Override
	public void markRejected(IEnvironmentInstance instance, StepEnum stepEnum, String cause) {
		MyTrackingLabel myTrackingLabel = getTrackingLabel(instance, stepEnum);
		if (myTrackingLabel != null) {
			myTrackingLabel.markRejected(cause);
		}
	}

	@Override
	public void markPlay(IEnvironmentInstance instance, StepEnum stepEnum) {
		MyTrackingLabel myTrackingLabel = getTrackingLabel(instance, stepEnum);
		if (myTrackingLabel != null) {
			myTrackingLabel.markPlay();
		}
	}

	private MyTrackingLabel getTrackingLabel(IEnvironmentInstance instance, StepEnum stepEnum) {
		String key = (instance != null ? instance.getName() : "null") + "_" + Integer.toString(stepEnum.getStep());

		MyTrackingLabel myTrackingLabel = trackingLabelMap.get(key);
		return myTrackingLabel;
	}

	private final class MyTrackingLabel extends JLabel {

		private static final long serialVersionUID = 2130446662560229566L;

		public MyTrackingLabel(String name, int step, boolean concerned) {
			super();

			if (concerned) {
				this.setIcon(waitIcon);
				this.setText(name);
			} else {
				this.setIcon(skipIcon);
				this.setText("<html><i>" + name + "</i></html>");
			}
		}

		public void markDone() {
			this.setIcon(doneIcon);
		}

		public void markRejected(String cause) {
			this.setToolTipText(cause);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.setIcon(cancelIcon);
		}

		public void markPlay() {
			this.setIcon(playIcon);
		}
	}

	@Override
	public void updateMailCheckbox() {
		if (managementPanelList.size() > 2) {
			for (AbstractManagementPanel<?> managementPanel : managementPanelList) {
				if (NodeType.BUILD.equals(managementPanel.getNode().getType())) {
					BuildPanel buildPanel = (BuildPanel) managementPanel;
					buildPanel.updateMailCheckbox();
				}
			}
		}
	}

	@Override
	public void log(String log) {
		showLogButton.setVisible(true);
		this.log.append(log);
	}
}
