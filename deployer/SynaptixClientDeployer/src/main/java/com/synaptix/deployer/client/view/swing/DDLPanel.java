package com.synaptix.deployer.client.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.deployer.client.controller.DDLController;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IDDLView;
import com.synaptix.deployer.client.view.swing.helper.DBHelper;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

/* package protected */class DDLPanel extends WaitComponentFeedbackPanel implements IRibbonContextView, IDockable, IDockingContextView, IDDLView {

	private static final long serialVersionUID = 2092280860564693479L;

	private RibbonContext ribbonContext;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private DDLController ddlController;

	private JTextField userTextField;

	private JPasswordField passwordTextField;

	private JButton downloadButton;

	private JComboBox envCombo;

	public DDLPanel(DDLController ddlController) {
		super();

		this.ddlController = ddlController;

		initComponents();

		this.addContent(buildContent());
	}

	private void initComponents() {
		userTextField = new JTextField();
		passwordTextField = new JPasswordField();

		envCombo = DBHelper.buildDBComboBox(ddlController.getSupportedDbs());

		downloadButton = new JButton(new AbstractAction(StaticHelper.getDDLConstantsBundle().generateDDL()) {

			private static final long serialVersionUID = -5309039114570512448L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ddlController.downloadDDL((ISynaptixDatabaseSchema) envCombo.getSelectedItem(), userTextField.getText(), passwordTextField.getPassword());
			}
		});
	}

	private Component buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:MAX(DEFAULT;100DLU):NONE,15DLU,FILL:DEFAULT:NONE,3DLU,FILL:MAX(DEFAULT;100DLU):NONE",
				"FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,6DLU,FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addTitle("<html><b>" + StaticHelper.getDDLConstantsBundle().databaseInformation() + "</b></html>", cc.xyw(1, 1, 7));
		builder.addLabel(StaticHelper.getDDLConstantsBundle().user(), cc.xy(1, 3));
		builder.add(userTextField, cc.xy(3, 3));
		builder.addLabel(StaticHelper.getDDLConstantsBundle().password(), cc.xy(5, 3));
		builder.add(passwordTextField, cc.xy(7, 3));
		builder.addLabel(StaticHelper.getDDLConstantsBundle().tablename(), cc.xy(1, 5));
		builder.add(envCombo, cc.xy(3, 5));
		builder.add(downloadButton, cc.xy(1, 7));
		return builder.getPanel();
	}

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

		dockKey = new DockKey(getIdDockable(), StaticHelper.getDDLConstantsBundle().ddl());
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
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(DDLPanel.class.getResource("/images/deployer/ddl.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getDDLConstantsBundle().ddl(), icon);
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
}
