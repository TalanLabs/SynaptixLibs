package com.synaptix.deployer.client.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.component.helper.ComponentHelper.PropertyArrayBuilder;
import com.synaptix.deployer.client.controller.FileSystemSpaceController;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.IFileSystemSpaceView;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.model.FileSystemSpaceFields;
import com.synaptix.deployer.model.IFileSystemSpace;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.SyTableColumnModel;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.widget.TypeGenericCellRenderer;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.renderer.view.swing.IValueComponent;
import com.synaptix.widget.renderer.view.swing.ValueComponentSubstanceTableCellRenderer;
import com.synaptix.widget.view.swing.JTauxLabel;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

/* package protected */class FileSystemSpacePanel extends WaitComponentFeedbackPanel implements IRibbonContextView, IDockable, IDockingContextView, IFileSystemSpaceView {

	private static final long serialVersionUID = 2092280860564693479L;

	private RibbonContext ribbonContext;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private FileSystemSpaceController fileSystemSpaceController;

	private JButton refreshButton;

	private JComboBox envCombo;

	private JSyTable table;

	private DefaultComponentTableModel<IFileSystemSpace> tableModel;

	public FileSystemSpacePanel(FileSystemSpaceController fileSystemSpaceController) {
		super();

		this.fileSystemSpaceController = fileSystemSpaceController;

		initComponents();

		this.addContent(buildContent());
	}

	private void initComponents() {

		String[] tableColumns = PropertyArrayBuilder.build(FileSystemSpaceFields.name(), FileSystemSpaceFields.totalSpace(), FileSystemSpaceFields.usedSpace(), FileSystemSpaceFields.availableSpace(),
				FileSystemSpaceFields.percentage(), FileSystemSpaceFields.mountedOn());
		tableModel = new DefaultComponentTableModel<IFileSystemSpace>(IFileSystemSpace.class, StaticHelper.getFileSystemSpaceConstantsBundle(), tableColumns);

		table = new JSyTable(tableModel);
		int column = tableModel.findColumnIndexById(FileSystemSpaceFields.percentage().name());
		if (column >= 0) {
			SortKey sortKey = new SortKey(column, SortOrder.DESCENDING);
			List<SortKey> sortKeys = new ArrayList<SortKey>();
			sortKeys.add(sortKey);
			table.getRowSorter().setSortKeys(sortKeys);
		}
		int column2 = tableModel.findColumnIndexById(FileSystemSpaceFields.percentage().name());
		if (column2 >= 0) {
			final SyTableColumn column3 = (SyTableColumn) ((SyTableColumnModel) table.getColumnModel()).getColumn(column2, true);
			column3.setCellRenderer(new ValueComponentSubstanceTableCellRenderer<Integer>(new IValueComponent<Integer>() {

				JTauxLabel tauxLabel = new JTauxLabel();

				@Override
				public Component getComponent() {
					return tauxLabel;
				}

				@Override
				public void setValue(JLabel label, Integer value) {
					tauxLabel.setTaux(value / 100.0);
				}

			}));
		}

		GenericObjectToString<ISynaptixEnvironment> os = new GenericObjectToString<ISynaptixEnvironment>() {

			@Override
			public String getString(ISynaptixEnvironment t) {
				return String.format("%s", t.getName());
			}
		};
		TypeGenericCellRenderer<ISynaptixEnvironment> renderer = new TypeGenericCellRenderer<ISynaptixEnvironment>(os);

		List<ISynaptixEnvironment> environmentList = fileSystemSpaceController.getSupportedEnvironments();
		ISynaptixEnvironment[] environments = environmentList.toArray(new ISynaptixEnvironment[environmentList.size()]);
		envCombo = new JComboBox(environments);
		envCombo.setRenderer(renderer);

		refreshButton = new JButton(new AbstractAction(StaticHelper.getFileSystemSpaceConstantsBundle().refresh()) {

			private static final long serialVersionUID = -5309039114570512448L;

			@Override
			public void actionPerformed(ActionEvent e) {
				fileSystemSpaceController.refresh((ISynaptixEnvironment) envCombo.getSelectedItem());
			}
		});
	}

	private Component buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:MAX(DEFAULT;100DLU):NONE,15DLU,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticHelper.getFileSystemSpaceConstantsBundle().environment(), cc.xy(1, 1));
		builder.add(envCombo, cc.xy(3, 1));
		builder.add(refreshButton, cc.xy(5, 1));
		builder.add(new JSyScrollPane(table), cc.xyw(1, 3, 6));
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

		dockKey = new DockKey(getIdDockable(), StaticHelper.getFileSystemSpaceConstantsBundle().fileSystemSpace());
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
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(WatcherPanel.class.getResource("/images/deployer/harddisk.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getFileSystemSpaceConstantsBundle().fileSystemSpace(), icon);
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
	public void setComponents(List<IFileSystemSpace> fileSystemSpaceList) {
		tableModel.setComponents(fileSystemSpaceList);
	}

	private final class PercentagePanel extends JPanel {

		private static final long serialVersionUID = 4120472574827058386L;

		private final int percentage;

		public PercentagePanel(int percentage) {
			super();

			this.percentage = percentage;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			g.fillRect(0, 0, (int) (percentage * getWidth() / 100.0), getHeight());
		}
	}

	private final class PercentageLabel extends JLabel {

		private static final long serialVersionUID = 3627609655438975113L;

		private final JLabel label;

		private final int percentage;

		public PercentageLabel(JLabel label, int percentage) {
			super();

			this.label = label;
			this.percentage = percentage;
		}

		@Override
		public void paint(Graphics g) {
			g.fillRect(0, 0, (int) (percentage * getWidth() / 100.0), getHeight());
			label.paint(g);
		}
	}

}
