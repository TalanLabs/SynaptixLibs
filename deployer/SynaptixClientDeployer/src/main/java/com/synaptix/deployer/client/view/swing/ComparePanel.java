package com.synaptix.deployer.client.view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JScrollablePanel;
import org.pushingpixels.flamingo.api.common.JScrollablePanel.ScrollType;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.model.Binome;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper.PropertyArrayBuilder;
import com.synaptix.deployer.client.compare.CompareNode;
import com.synaptix.deployer.client.compare.CompareNode.CompareNodeType;
import com.synaptix.deployer.client.controller.CompareController;
import com.synaptix.deployer.client.model.AnalyzedTableColumnFields;
import com.synaptix.deployer.client.model.IAnalyzedTableColumn;
import com.synaptix.deployer.client.model.IAnalyzedTableColumn.MovementType;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.ICompareView;
import com.synaptix.deployer.client.view.swing.compare.CompareTitle;
import com.synaptix.deployer.client.view.swing.helper.DBHelper;
import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.ITableColumn;
import com.synaptix.deployer.model.TableColumnFields;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.table.TableRowRenderer;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonBandBuilder;
import com.synaptix.widget.core.view.swing.RibbonContext.RibbonTaskBuilder;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.view.swing.JSyCarouselPanel;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

/* package protected */class ComparePanel extends WaitComponentFeedbackPanel implements IRibbonContextView, IDockable, IDockingContextView, ICompareView {

	private static final long serialVersionUID = -4892619854243311752L;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private CompareController compareController;

	private JComboBox env1Combo;

	private JComboBox env2Combo;

	private JButton refreshButton;

	private JTabbedPane tabbedPane;

	private JSyCarouselPanel missingTableCarousel;

	private JSyCarouselPanel structureCarousel;

	private JSyCarouselPanel constraintCarousel;

	private List<CompareTitle> missingTableTitles;

	private List<CompareTitle> structureTitles;

	private List<CompareTitle> constraintTitles;

	private JPanel missingTableSwingTitle;

	private JPanel structureSwingTitle;

	private JPanel constraintSwingTitle;

	private MyRowRenderer differenceTableRowRenderer;

	public ComparePanel(CompareController compareController) {
		super();

		this.compareController = compareController;

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		missingTableTitles = new ArrayList<CompareTitle>();
		structureTitles = new ArrayList<CompareTitle>();
		constraintTitles = new ArrayList<CompareTitle>();

		differenceTableRowRenderer = new MyRowRenderer();

		missingTableSwingTitle = new JPanel();
		structureSwingTitle = new JPanel();
		constraintSwingTitle = new JPanel();

		List<ISynaptixDatabaseSchema> dbList = compareController.getSupportedDbs();
		env1Combo = DBHelper.buildDBComboBox(dbList);
		env2Combo = DBHelper.buildDBComboBox(dbList);
		int i = 1;
		int max = dbList.size();
		ISynaptixDatabaseSchema db1 = (max > 0 ? dbList.get(0) : null);
		boolean ok = (db1 != null ? true : false);
		while ((ok) && (i < max)) {
			if (!db1.getEnvironment().equals(dbList.get(i).getEnvironment())) {
				env2Combo.setSelectedItem(dbList.get(i));
				ok = false;
			}
			i++;
		}

		refreshButton = new JButton(StaticHelper.getCompareConstantsBundle().refresh());
		refreshButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = 2875942987826528223L;

			@Override
			public void actionPerformed(ActionEvent e) {
				compareController.refresh((ISynaptixDatabaseSchema) env1Combo.getSelectedItem(), (ISynaptixDatabaseSchema) env2Combo.getSelectedItem());
			}
		});

		tabbedPane = new JTabbedPane();

		missingTableCarousel = new JSyCarouselPanel(null, true);
		missingTableCarousel.setShowDotIfAlone(false);
		structureCarousel = new JSyCarouselPanel(null, true);
		structureCarousel.setShowDotIfAlone(false);
		constraintCarousel = new JSyCarouselPanel(null, true);
		constraintCarousel.setShowDotIfAlone(false);

		missingTableCarousel.addPropertyChangeListener(JSyCarouselPanel.PAGE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				pageSelected(missingTableTitles, missingTableSwingTitle, (Integer) evt.getNewValue());
			}
		});

		structureCarousel.addPropertyChangeListener(JSyCarouselPanel.PAGE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				pageSelected(structureTitles, structureSwingTitle, (Integer) evt.getNewValue());
			}
		});

		constraintCarousel.addPropertyChangeListener(JSyCarouselPanel.PAGE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				pageSelected(constraintTitles, constraintSwingTitle, (Integer) evt.getNewValue());
			}
		});

		tabbedPane.addTab(StaticHelper.getCompareConstantsBundle().missingTable(), buildCarouselPanel(missingTableTitles, missingTableSwingTitle, missingTableCarousel));
		tabbedPane.addTab(StaticHelper.getCompareConstantsBundle().structuralDifference(), buildCarouselPanel(structureTitles, structureSwingTitle, structureCarousel));
		tabbedPane.addTab(StaticHelper.getCompareConstantsBundle().constraintDifferences(), buildCarouselPanel(constraintTitles, constraintSwingTitle, constraintCarousel));
		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedIndex() == 0) {
					missingTableCarousel.requestFocusInWindow();
				} else if (tabbedPane.getSelectedIndex() == 1) {
					structureCarousel.requestFocusInWindow();
				} else if (tabbedPane.getSelectedIndex() == 2) {
					constraintCarousel.requestFocusInWindow();
				}
			}
		});
	}

	private Component buildCarouselPanel(final List<CompareTitle> missingTableTitles, final JPanel swingTitle, JSyCarouselPanel carousel) {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		final JScrollablePanel<JPanel> scroll = new JScrollablePanel<JPanel>(swingTitle, ScrollType.HORIZONTALLY);
		carousel.addPropertyChangeListener(JSyCarouselPanel.PAGE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Integer page = (Integer) evt.getNewValue();
				if (page != null) {
					CompareTitle compareTitle = missingTableTitles.get(page - 1);
					for (Component component : swingTitle.getComponents()) {
						if (component.equals(compareTitle)) {
							scroll.getUI().scrollToIfNecessary((int) component.getBounds().getMinX(), component.getWidth());
						}
					}
				}
			}
		});
		// scroll.getUI().scrollToIfNecessary(startPosition, span);
		builder.add(scroll, cc.xy(1, 1));
		builder.add(carousel, cc.xy(1, 3));
		return builder.getPanel();
	}

	private Component buildContents() {
		FormLayout layout = new FormLayout("LEFT:200DLU:NONE,3DLU,FILL:DEFAULT:GROW(1.0),3DLU,RIGHT:200DLU:NONE", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(env1Combo, cc.xy(1, 1));
		builder.add(refreshButton, cc.xy(3, 1));
		builder.add(env2Combo, cc.xy(5, 1));
		builder.add(tabbedPane, cc.xyw(1, 3, 5));
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

		dockKey = new DockKey(getIdDockable(), StaticHelper.getCompareConstantsBundle().comparer());
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
		ResizableIcon icon = ImageWrapperResizableIcon.getIcon(WatcherPanel.class.getResource("/images/deployer/compare.png"), new Dimension(30, 30));
		JCommandButton cb = new JCommandButton(StaticHelper.getCompareConstantsBundle().comparer(), icon);
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
	public void setCompareResult(ISynaptixDatabaseSchema db1, ISynaptixDatabaseSchema db2, Binome<ICompareStructureResult, ICompareConstraintResult> result) {
		missingTableTitles.clear();
		structureTitles.clear();
		constraintTitles.clear();
		if (result != null) {
			buildMissingTables(db1, db2, result.getValue1());
			buildStructuralDifferences(db1, db2, result.getValue1());
		} else {
			structureCarousel.setComponents(null);
			constraintCarousel.setComponents(null);
		}
	}

	private void buildMissingTables(ISynaptixDatabaseSchema db1, ISynaptixDatabaseSchema db2, ICompareStructureResult compareStructureResult) {
		String[] fields = PropertyArrayBuilder.build(TableColumnFields.columnName(), TableColumnFields.type(), TableColumnFields.columnId(), TableColumnFields.precision(),
				TableColumnFields.dataLength(), TableColumnFields.dataScale(), TableColumnFields.dataDefault(), TableColumnFields.nullable());
		List<JPanel> missingTablePanelList = new ArrayList<JPanel>();
		int level = 0;
		for (Binome<ISynaptixDatabaseSchema, String> binomeTable : compareStructureResult.getMissingTableList()) {
			if (db2.equals(binomeTable.getValue1())) {
				List<ITableColumn> tableColumnList = new ArrayList<ITableColumn>();
				for (ITableColumn tableColumn : compareStructureResult.getTableColumnDb1()) {
					if (binomeTable.getValue2().equals(tableColumn.getTableName())) {
						tableColumnList.add(tableColumn);
					}
				}
				DefaultComponentTableModel<ITableColumn> tableModel = new DefaultComponentTableModel<ITableColumn>(ITableColumn.class, StaticHelper.getCompareConstantsBundle(), fields);
				tableModel.setComponents(tableColumnList);

				missingTablePanelList.add(buildMissingTablePanel(binomeTable.getValue2(), tableModel));
				CompareNode compareNode = new CompareNode(level++, binomeTable.getValue2(), CompareNodeType.TABLE);
				missingTableTitles.add(new CompareTitle(compareNode, compareController));
			}
		}
		missingTableCarousel.setComponents(missingTablePanelList.toArray(new JPanel[missingTablePanelList.size()]));
		tabbedPane.setTabComponentAt(0, new JLabel(StaticHelper.getCompareConstantsBundle().missingTables(missingTablePanelList.size())));

		pageSelected(missingTableTitles, missingTableSwingTitle, missingTableCarousel.getPage());
	}

	private void buildStructuralDifferences(ISynaptixDatabaseSchema db1, ISynaptixDatabaseSchema db2, ICompareStructureResult compareStructureResult) {
		String[] fields = PropertyArrayBuilder.build(AnalyzedTableColumnFields.columnName(), AnalyzedTableColumnFields.type(), AnalyzedTableColumnFields.columnId(),
				AnalyzedTableColumnFields.precision(), AnalyzedTableColumnFields.dataLength(), AnalyzedTableColumnFields.dataScale(), AnalyzedTableColumnFields.dataDefault(),
				AnalyzedTableColumnFields.nullable(), AnalyzedTableColumnFields.movementType());

		Set<String> tableInErrorSet = new HashSet<String>();
		tableInErrorSet.addAll(compareStructureResult.getDifferenceMapDb1().keySet());
		tableInErrorSet.addAll(compareStructureResult.getDifferenceMapDb2().keySet());
		// if (CollectionHelper.isNotEmpty(compareStructureResult.getDifferenceList())) {
		// for (IDifference difference : compareStructureResult.getDifferenceList()) {
		// tableInErrorSet.add(difference.getTableName());
		// }
		// }
		List<String> tableInErrorList = new ArrayList<String>(tableInErrorSet);
		Collections.sort(tableInErrorList);

		for (Binome<ISynaptixDatabaseSchema, String> binome : compareStructureResult.getMissingTableList()) {
			tableInErrorList.remove(binome.getValue2());
		}

		int level = 0;
		List<JPanel> tablePanelList = new ArrayList<JPanel>();
		for (String table : tableInErrorList) {
			// if (db2.equals(differentTable.getValue1())) {
			// List<ITableColumn> tableColumnList = new ArrayList<ITableColumn>();
			// for (Binome<IDatabase, ITableColumn> binomeTableColumn : compareStructureResult.getMissingTableColumnList()) {
			// if ((db2.equals(binomeTableColumn.getValue1())) && (binomeTableColumn.getValue2().equals(binomeTableColumn.getValue2().getTableName()))) {
			// tableColumnList.add(binomeTableColumn.getValue2());
			// }
			// }

			List<ITableColumn> tableColumnDb1List = getColumnsForTable(table, compareStructureResult.getTableColumnDb1());
			List<ITableColumn> tableColumnDb2List = getColumnsForTable(table, compareStructureResult.getTableColumnDb2());

			List<IAnalyzedTableColumn> analyzedTableColumnDb1List = new ArrayList<IAnalyzedTableColumn>();
			List<IAnalyzedTableColumn> analyzedTableColumnDb2List = new ArrayList<IAnalyzedTableColumn>();
			for (ITableColumn tableColumn : tableColumnDb1List) {
				IAnalyzedTableColumn analyzed = ComponentFactory.getInstance().createInstance(IAnalyzedTableColumn.class);
				analyzed.straightSetProperties(tableColumn.straightGetProperties());

				MovementType movementType = null;
				if (compareStructureResult.getTableColumnDb2().contains(tableColumn)) {
					List<ITableColumn> list = compareStructureResult.getDifferenceMapDb1().get(tableColumn.getTableName());
					if ((list != null) && (list.contains(tableColumn))) {
						movementType = MovementType.EDIT;
					}
				} else {
					movementType = MovementType.CREATION;
				}
				analyzed.setMovementType(movementType);
				analyzedTableColumnDb1List.add(analyzed);
			}
			for (ITableColumn tableColumn : tableColumnDb2List) {
				IAnalyzedTableColumn analyzed = ComponentFactory.getInstance().createInstance(IAnalyzedTableColumn.class);
				analyzed.straightSetProperties(tableColumn.straightGetProperties());

				MovementType movementType = null;
				if (compareStructureResult.getTableColumnDb1().contains(tableColumn)) {
					List<ITableColumn> list = compareStructureResult.getDifferenceMapDb2().get(tableColumn.getTableName());
					if ((list != null) && (list.contains(tableColumn))) {
						movementType = MovementType.EDIT;
					}
				} else {
					movementType = MovementType.SUPPRESSION;
				}
				analyzed.setMovementType(movementType);
				analyzedTableColumnDb2List.add(analyzed);
			}

			DefaultComponentTableModel<IAnalyzedTableColumn> tableModelDb1 = new DefaultComponentTableModel<IAnalyzedTableColumn>(IAnalyzedTableColumn.class, StaticHelper.getCompareConstantsBundle(),
					fields);
			tableModelDb1.setComponents(analyzedTableColumnDb1List);

			DefaultComponentTableModel<IAnalyzedTableColumn> tableModelDb2 = new DefaultComponentTableModel<IAnalyzedTableColumn>(IAnalyzedTableColumn.class, StaticHelper.getCompareConstantsBundle(),
					fields);
			tableModelDb2.setComponents(analyzedTableColumnDb2List);

			tablePanelList.add(buildDifferentTablePanel(table, db1, db2, tableModelDb1, tableModelDb2));
			// }

			CompareNode compareNode = new CompareNode(level++, table, CompareNodeType.COLUMN);
			structureTitles.add(new CompareTitle(compareNode, compareController));
			structureTitles.get(0).setActive(true);
		}
		structureCarousel.setComponents(tablePanelList.toArray(new JPanel[tablePanelList.size()]));
		tabbedPane.setTabComponentAt(1, new JLabel(StaticHelper.getCompareConstantsBundle().structuralDifferences(tablePanelList.size())));

		pageSelected(structureTitles, structureSwingTitle, structureCarousel.getPage());
	}

	private List<ITableColumn> getColumnsForTable(String table, List<ITableColumn> tableColumnList) {
		List<ITableColumn> subTableColumnList = new ArrayList<ITableColumn>();
		for (ITableColumn tableColumn : tableColumnList) {
			if (table.equals(tableColumn.getTableName())) {
				subTableColumnList.add(tableColumn);
			}
		}
		return subTableColumnList;
	}

	private JPanel buildMissingTablePanel(String table, DefaultComponentTableModel<ITableColumn> tableModel) {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0),15DLU");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(String.format("<html><b>%s</b></html>", table)), cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.CENTER));
		JSyTable table1 = new JSyTable(tableModel);
		table1.setShowTableLines(true);
		int column = tableModel.findColumnIndexById(TableColumnFields.columnId().name());
		if (column >= 0) {
			SortKey sortKey = new SortKey(column, SortOrder.ASCENDING);
			List<SortKey> sortKeys = new ArrayList<SortKey>();
			sortKeys.add(sortKey);
			table1.getRowSorter().setSortKeys(sortKeys);
		}
		builder.add(new JSyScrollPane(table1), cc.xy(1, 3));
		return builder.getPanel();
	}

	private JPanel buildDifferentTablePanel(String table, ISynaptixDatabaseSchema db1, ISynaptixDatabaseSchema db2, DefaultComponentTableModel<IAnalyzedTableColumn> tableModelDb1,
			DefaultComponentTableModel<IAnalyzedTableColumn> tableModelDb2) {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(0.5),3DLU,FILL:DEFAULT:GROW(0.5)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0),15DLU");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(new JLabel(String.format("<html><b>%s</b></html>", table)), cc.xyw(1, 1, 3, CellConstraints.CENTER, CellConstraints.CENTER));
		builder.add(new JLabel(db1.getEnvironment()), cc.xy(1, 3, CellConstraints.CENTER, CellConstraints.CENTER));
		builder.add(new JLabel(db2.getEnvironment()), cc.xy(3, 3, CellConstraints.CENTER, CellConstraints.CENTER));
		JSyTable table1 = new JSyTable(tableModelDb1);
		table1.setShowTableLines(true);
		table1.setTableRowRenderer(differenceTableRowRenderer);
		int column = tableModelDb1.findColumnIndexById(TableColumnFields.columnId().name());
		if (column >= 0) {
			SortKey sortKey = new SortKey(column, SortOrder.ASCENDING);
			List<SortKey> sortKeys = new ArrayList<SortKey>();
			sortKeys.add(sortKey);
			table1.getRowSorter().setSortKeys(sortKeys);
		}
		builder.add(new JSyScrollPane(table1), cc.xy(1, 5));
		JSyTable table2 = new JSyTable(tableModelDb2);
		table2.setShowTableLines(true);
		table2.setTableRowRenderer(differenceTableRowRenderer);
		column = tableModelDb2.findColumnIndexById(TableColumnFields.columnId().name());
		if (column >= 0) {
			SortKey sortKey = new SortKey(column, SortOrder.ASCENDING);
			List<SortKey> sortKeys = new ArrayList<SortKey>();
			sortKeys.add(sortKey);
			table2.getRowSorter().setSortKeys(sortKeys);
		}
		builder.add(new JSyScrollPane(table2), cc.xy(3, 5));
		return builder.getPanel();
	}

	private final class MyRowRenderer implements TableRowRenderer {

		@Override
		public Component getTableRowRenderer(Component c, JTable table, boolean isSelected, boolean hasFocus, int row, int col) {

			@SuppressWarnings("unchecked")
			DefaultComponentTableModel<IAnalyzedTableColumn> componentTableModel = (DefaultComponentTableModel<IAnalyzedTableColumn>) table.getModel();
			IAnalyzedTableColumn a = componentTableModel.getComponent(table.convertRowIndexToModel(row));
			if ((a != null) && (a.getMovementType() != null)) {
				switch (a.getMovementType()) {
				case CREATION:
					c.setBackground(Color.green);
					break;
				case EDIT:
					c.setBackground(Color.blue);
					break;
				case SUPPRESSION:
					c.setBackground(Color.red);
					break;
				default:
					break;
				}
			}
			return c;
		}
	}

	@Override
	public void exploreNode(CompareNode node) {

		JSyCarouselPanel carousel = null;
		List<CompareTitle> compareTitleList = null;
		JPanel swingTitle = null;
		switch (node.getCompareNodeType()) {
		case TABLE:
			carousel = missingTableCarousel;
			compareTitleList = missingTableTitles;
			swingTitle = missingTableSwingTitle;
			break;
		case COLUMN:
			carousel = structureCarousel;
			compareTitleList = structureTitles;
			swingTitle = structureSwingTitle;
			break;
		case CONSTRAINT:
			carousel = constraintCarousel;
			compareTitleList = constraintTitles;
			swingTitle = constraintSwingTitle;
			break;
		}
		if (carousel != null) {
			int level = node.getLevel();
			carousel.goToPage(level + 1);
			pageSelected(compareTitleList, swingTitle, level + 1);
		}
	}

	private void pageSelected(List<CompareTitle> compareTitleList, JPanel swingTitle, int page) {
		for (CompareTitle compareTitle : compareTitleList) {
			compareTitle.setActive(compareTitle.getRank() == page);
			compareTitle.setExplored(compareTitle.getRank() <= page);
		}
		updateTitle(compareTitleList, swingTitle);
	}

	private void updateTitle(List<CompareTitle> compareTitleList, JPanel swingTitle) {
		swingTitle.removeAll();
		int size = compareTitleList.size() * 2 - 1;
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
		for (CompareTitle title : compareTitleList) {
			builder.add(title, cc.xy(j, 1));
			j = j + 2;
			i++;
			if (i < size) {
				builder.addLabel("|", cc.xy(j, 1));
				j = j + 2;
				i++;
			}
		}
		swingTitle.validate();
		swingTitle.repaint();
	}
}
