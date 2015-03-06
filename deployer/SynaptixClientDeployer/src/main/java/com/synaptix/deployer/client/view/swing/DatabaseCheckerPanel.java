package com.synaptix.deployer.client.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXHeader;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.deployer.client.controller.DatabaseCheckerController;
import com.synaptix.deployer.client.message.DatabaseCheckerConstantsBundle;
import com.synaptix.deployer.client.model.IDatabaseQuery;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IDatabaseCheckerView;
import com.synaptix.deployer.client.view.swing.helper.DBHelper;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.actions.view.swing.AbstractRefreshAction;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;

/* package protected*/class DatabaseCheckerPanel extends WaitComponentFeedbackPanel implements IDatabaseCheckerView, IDockingContextView, IDockable, IRibbonContextView {

	private static final long serialVersionUID = -4150349797159089736L;

	private final Icon validIcon = ImageWrapperResizableIcon.getIcon(DatabaseCheckerPanel.class.getResource("/images/deployer/checkGreen.png"), new Dimension(12, 12));

	private final Icon errorIcon = ImageWrapperResizableIcon.getIcon(DatabaseCheckerPanel.class.getResource("/images/deployer/errorRed.png"), new Dimension(12, 12));

	private DatabaseCheckerController databaseCheckerController;

	private JList databaseQueryList;

	private MyListModel listModel;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private Action testAllAction;

	private Action testAction;

	private JXHeader header;

	private JCheckBox validCheckbox;

	private JTextArea descriptionArea;

	private JTextArea resultArea;

	private JComboBox databaseCombo;

	public DatabaseCheckerPanel(DatabaseCheckerController databaseCheckerController) {
		super();

		this.databaseCheckerController = databaseCheckerController;

		this.initComponents();

		this.addContent(buildContent());
	}

	private void initComponents() {
		initActions();

		databaseCombo = DBHelper.buildDBComboBox(databaseCheckerController.getSupportedDbs());
		databaseCombo.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = 1422730796201469580L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO : null pour valid
			}
		});

		header = new JXHeader(getBundle().chooseACheck(), null);
		validCheckbox = new JCheckBox(getBundle().valid());
		validCheckbox.setEnabled(false);
		descriptionArea = new JTextArea(4, 60);
		resultArea = new JTextArea(4, 60);

		listModel = new MyListModel();

		databaseQueryList = new JList(listModel);
		databaseQueryList.setCellRenderer(new SubstanceDefaultListCellRenderer() {

			private static final long serialVersionUID = 1393622303363450108L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (IDatabaseQuery.class.isAssignableFrom(value.getClass())) {
					IDatabaseQuery databaseTest = (IDatabaseQuery) value;
					JLabel label = (JLabel) component;
					if (databaseTest.isValid() != null) {
						if (databaseTest.isValid()) {
							label.setIcon(validIcon);
						} else {
							label.setIcon(errorIcon);
						}
					} else {
						label.setIcon(null);
					}
					label.setText(databaseTest.getName());
				}
				return component;
			}
		});
		databaseQueryList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (databaseQueryList.getSelectedIndex() > -1) {
						IDatabaseQuery databaseQuery = listModel.getElementAt(databaseQueryList.getSelectedIndex());
						databaseCheckerController.setSelectedDatabaseQuery(databaseQuery);
						// } else {
						// databaseQueryList.setSelectedValue(testFileController.getSelectedTestFile(), true);
					}
				}
			}
		});
	}

	private void initActions() {
		testAllAction = new AbstractRefreshAction(getBundle().testAll()) {

			private static final long serialVersionUID = 2064968758921761846L;

			@Override
			public void actionPerformed(ActionEvent e) {
				databaseCheckerController.testAll((ISynaptixDatabaseSchema) databaseCombo.getSelectedItem());
			}
		};
		testAction = new AbstractRefreshAction(getBundle().test()) {

			private static final long serialVersionUID = -7291507900455418070L;

			@Override
			public void actionPerformed(ActionEvent e) {
				databaseCheckerController.testCurrentDatabaseQuery((ISynaptixDatabaseSchema) databaseCombo.getSelectedItem());
			}
		};
		testAction.setEnabled(false);
	}

	private Component buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(databaseCombo, cc.xy(1, 1));
		builder.add(ToolBarFactory.buildToolBar(testAllAction), cc.xy(1, 3));
		builder.add(new JSyScrollPane(databaseQueryList), cc.xy(1, 5));
		builder.add(buildRightPanel(), cc.xywh(3, 1, 1, 5));
		return builder.getPanel();
	}

	private final Component buildRightPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(header, cc.xy(1, 1));
		builder.add(buildQueryPanel(), cc.xy(1, 3));
		return builder.getPanel();
	}

	private final Component buildQueryPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTop(), buildBottom()), cc.xy(1, 1));

		return builder.getPanel();
	}

	private final Component buildTop() {
		FormLayout layout = new FormLayout("5DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(ToolBarFactory.buildToolBar(testAction), cc.xyw(1, 1, 2));
		builder.addSeparator(getBundle().description(), cc.xyw(1, 3, 2));
		builder.add(new JSyScrollPane(descriptionArea), cc.xy(2, 5));

		return builder.getPanel();
	}

	private final Component buildBottom() {
		FormLayout layout = new FormLayout("5DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.addSeparator(getBundle().result(), cc.xyw(1, 1, 4));
		builder.add(validCheckbox, cc.xy(2, 3));
		builder.add(new JSyScrollPane(resultArea), cc.xyw(2, 5, 3));

		return builder.getPanel();
	}

	private DatabaseCheckerConstantsBundle getBundle() {
		return StaticHelper.getDatabaseCheckerConstantsBundle();
	}

	@Override
	public void setDatabaseQueryList(List<IDatabaseQuery> databaseTestList) {
		databaseQueryList.setValueIsAdjusting(true);
		listModel.setDatabaseTestList(databaseTestList);
		databaseQueryList.getSelectionModel().clearSelection();
		databaseQueryList.setValueIsAdjusting(false);
	}

	@Override
	public void showDatabaseQuery(IDatabaseQuery databaseQuery) {
		header.setTitle(databaseQuery != null ? databaseQuery.getName() : null);
		descriptionArea.setText(databaseQuery != null ? databaseQuery.getMeaning() : null);
		descriptionArea.setCaretPosition(0);
		testAction.setEnabled(databaseQuery != null);
		resultArea.setText(null);
	}

	@Override
	public void showDatabaseQueryResult(boolean valid, String databaseQueryResult) {
		resultArea.setText(databaseQueryResult);
		resultArea.setCaretPosition(0);
		validCheckbox.setSelected(valid);
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;

		dockKey = new DockKey(getIdDockable(), StaticHelper.getDatabaseCheckerConstantsBundle().databaseChecker());
		dockKey.setFloatEnabled(true);
		dockKey.setAutoHideEnabled(false);

		dockingContext.registerDockable(this);
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(DatabaseCheckerPanel.class.getResource("/images/deployer/checkmark.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getDatabaseCheckerConstantsBundle().databaseChecker(), icon);
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dockingContext.showDockable(getIdDockable());
			}
		});

		String ribbonTaskTitle = StaticHelper.getDeployerConstantsBundle().exploitation();
		String ribbonBandTitle = StaticHelper.getDeployerConstantsBundle().database();

		RibbonTaskBuilder ribbonTaskBuilder = ribbonContext.addRibbonTask(ribbonTaskTitle, 0);
		RibbonBandBuilder ribbonBand = ribbonTaskBuilder.addRibbonBand(ribbonBandTitle, 2);
		ribbonBand.addCommandeButton(cb, RibbonElementPriority.TOP);
	}

	@Override
	public DockKey getDockKey() {
		return dockKey;
	}

	public SyDockingContext getDockingContext() {
		return dockingContext;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getCategory() {
		return "DEPLOYER";
	}

	public String getIdDockable() {
		return this.getClass().getName() + "_" + IDatabaseQuery.class.getName();
	}

	public void setDockKey(DockKey dockKey) {
		this.dockKey = dockKey;
	}

	public void setDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;
	}

	@Override
	public void updateTree() {
		databaseQueryList.repaint();
	}

	private final class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = 4075920994431099889L;

		private List<IDatabaseQuery> databaseTestList;

		public void setDatabaseTestList(List<IDatabaseQuery> databaseTestList) {
			int max = Math.max(getSize(), CollectionHelper.size(databaseTestList));
			this.databaseTestList = databaseTestList;
			fireContentsChanged(databaseTestList, 0, max);
		}

		@Override
		public IDatabaseQuery getElementAt(int index) {
			if ((index >= 0) && (index < getSize())) {
				return databaseTestList.get(index);
			}
			return null;
		}

		@Override
		public int getSize() {
			return CollectionHelper.size(databaseTestList);
		}
	}
}
