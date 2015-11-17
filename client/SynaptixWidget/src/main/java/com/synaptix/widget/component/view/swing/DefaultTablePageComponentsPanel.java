package com.synaptix.widget.component.view.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.IEntity;
import com.synaptix.service.model.ISortOrder;
import com.synaptix.swing.JCompositionPanel;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.table.SyTableColumnModel;
import com.synaptix.swing.utils.PopupMenuMouseListener;
import com.synaptix.widget.component.controller.context.ITablePageComponentsContext;
import com.synaptix.widget.component.view.ITablePageComponentsView;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.descriptor.AbstractTablePageViewDescriptor;
import com.synaptix.widget.view.swing.helper.IconHelper;
import com.synaptix.widget.view.swing.helper.PopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.DefaultColumnTableFactory;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;
import com.synaptix.widget.view.swing.tablemodel.field.CompositeField;

public class DefaultTablePageComponentsPanel<E extends IComponent> extends WaitComponentFeedbackPanel implements ITablePageComponentsView<E> {

	private static final long serialVersionUID = -2722048820947978541L;

	private static final Color MORE_PAGES_COLOR = new Color(80, 255, 80);

	private final ITablePageComponentsContext tablePageComponentContext;

	private final Class<E> componentClass;

	private final IColumnTableFactory<E> columnTableFactory;

	private final String[] tableColumns;

	private final JComponent toolBarComponents;

	private final JPopupMenu popupMenu;

	private final IExcelTableFileWriter<E> excelTableFileWriter;

	private DefaultComponentTableModel<E> componentTableModel;

	private JSyTable table;

	private JLabel currentPageLabel;

	private JCommandButton configureButton;

	private MyPopupCallback popupCallback;

	private Action previousPageAction;

	private Action nextPageAction;

	private Action firstPageAction;

	private Action lastPageAction;

	private Action countAction;

	private JLabel countLabel;

	private ActionListener configureSizeListener;

	private ActionListener selectAllListener;

	private ActionListener unselectAllListener;

	private ActionListener reverseListener;

	private Set<String> columnSet;

	private MyRowSorter tableRowSorter;

	private JCommandButton exportCommandButton;

	private JCommandMenuButton exportPageCommandMenuButton;

	private JCommandMenuButton exportAllCommandMenuButton;

	private CardLayout countCardLayout;

	private JPanel countPanel;

	private JButton previousPageButton;

	private JButton nextPageButton;

	private JButton countButton;

	private NoResultPanel noResultPanel;

	private boolean lineNumberEditable = false;

	private boolean useAllColumns = true;

	public DefaultTablePageComponentsPanel(ITablePageComponentsContext tablePageComponentContext, Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		this(tablePageComponentContext, componentClass, constantsWithLookingBundle, tableColumns, null, null);
	}

	public DefaultTablePageComponentsPanel(ITablePageComponentsContext tablePageComponentContext, Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle,
			String[] tableColumns, JComponent toolBarComponents, JPopupMenu popupMenu) {
		this(tablePageComponentContext, componentClass, new DefaultColumnTableFactory<E>(componentClass, constantsWithLookingBundle), tableColumns, toolBarComponents, popupMenu);
	}

	public DefaultTablePageComponentsPanel(ITablePageComponentsContext tablePageComponentContext, Class<E> componentClass, IColumnTableFactory<E> columnTableFactory, String[] tableColumns) {
		this(tablePageComponentContext, componentClass, columnTableFactory, tableColumns, null, null);
	}

	public DefaultTablePageComponentsPanel(ITablePageComponentsContext tablePageComponentContext, Class<E> componentClass, IColumnTableFactory<E> columnTableFactory, String[] tableColumns,
			JComponent toolBarComponents, JPopupMenu popupMenu) {
		super();

		this.tablePageComponentContext = tablePageComponentContext;
		this.componentClass = componentClass;
		this.columnTableFactory = columnTableFactory;
		this.tableColumns = tableColumns;
		this.toolBarComponents = toolBarComponents;
		this.popupMenu = popupMenu;

		initComponents();

		if (toolBarComponents != null) {
			this.addContent(buildContentsWithComponents());
		} else {
			this.addContent(buildContents());
		}

		this.excelTableFileWriter = createExcelTableFileWriter();
	}

	public DefaultTablePageComponentsPanel(ITablePageComponentsContext tablePageComponentContext, Class<E> componentClass, AbstractTablePageViewDescriptor<E> viewDescriptor) {
		super();

		this.tablePageComponentContext = tablePageComponentContext;
		this.componentClass = componentClass;

		IColumnTableFactory<E> columnTableFactory = viewDescriptor.getColumnTableFactory();
		if (columnTableFactory == null) {
			columnTableFactory = new DefaultColumnTableFactory<E>(componentClass, viewDescriptor.getDescriptorBundle(), viewDescriptor.getDefaultHideTableColumns());
		}

		this.columnTableFactory = columnTableFactory;
		this.tableColumns = viewDescriptor.getTableColumns();

		ToolBarActionsBuilder toolBarActionBuilder = new ToolBarActionsBuilder();
		viewDescriptor.installToolBar(toolBarActionBuilder);
		this.toolBarComponents = toolBarActionBuilder.build();

		PopupMenuActionsBuilder popupMenuActionsBuilder = new PopupMenuActionsBuilder();
		viewDescriptor.installPopupMenu(popupMenuActionsBuilder);
		this.popupMenu = popupMenuActionsBuilder.build();

		this.lineNumberEditable = viewDescriptor.isLineEditable();
		this.useAllColumns = viewDescriptor.useAllColumns();

		initComponents();

		viewDescriptor.install(this);
		viewDescriptor.installTable(table);

		if (this.toolBarComponents != null) {
			this.addContent(buildContentsWithComponents());
		} else {
			this.addContent(buildContents());
		}

		this.excelTableFileWriter = createExcelTableFileWriter();
	}

	/*
	 * Create component table model, current is DefaultComponentTableModel
	 */
	protected DefaultComponentTableModel<E> createComponentTableModel() {
		return new DefaultComponentTableModel<E>(componentClass, columnTableFactory, tableColumns) {

			private static final long serialVersionUID = 7839424444421975680L;

			@Override
			public boolean isSearchable(int columnIndex) {
				return false;
			}
		};
	}

	/*
	 * Create excel table file writer, current is DefaultExcelTableFileWriter
	 */
	protected IExcelTableFileWriter<E> createExcelTableFileWriter() {
		return new DefaultExcelTableFileWriter<E>(componentClass, table, componentTableModel);
	}

	private void initComponents() {
		configureSizeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectSizePage();
			}
		};
		selectAllListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getRowCount() > 0) {
					table.selectAll();
				}
			}
		};
		unselectAllListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getRowCount() > 0) {
					table.clearSelection();
				}
			}
		};
		reverseListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowCount = table.getRowCount();
				if (rowCount > 0) {
					int[] selectedIndices = table.getSelectedRows();
					ListSelectionModel selectionModel = table.getSelectionModel();
					selectionModel.setValueIsAdjusting(true);
					table.getSelectionModel().setSelectionInterval(0, rowCount - 1);
					for (int prevSel : selectedIndices) {
						table.getSelectionModel().removeSelectionInterval(prevSel, prevSel);
					}
					selectionModel.setValueIsAdjusting(false);
				}
			}
		};

		componentTableModel = createComponentTableModel();
		table = new JSyTable(componentTableModel, buildTableName());
		tableRowSorter = new MyRowSorter();
		table.setRowSorter(tableRowSorter);
		JodaSwingUtils.decorateTable(table);
		table.setShowTableLines(true);

		configureButton = new JCommandButton(ImageWrapperResizableIcon.getIcon(DefaultTablePageComponentsPanel.class.getResource("configure.png"), new Dimension(18, 18)));
		configureButton.setCommandButtonKind(CommandButtonKind.POPUP_ONLY);
		configureButton.setDisplayState(CommandButtonDisplayState.MEDIUM);
		popupCallback = new MyPopupCallback();
		configureButton.setPopupCallback(popupCallback);

		// table.setRowSorter(new MyRowSorter());
		table.getRowSorter().addRowSorterListener(new MyRowSorterListener());

		currentPageLabel = new JLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().numOfPage(0));
		currentPageLabel.setFont(currentPageLabel.getFont().deriveFont(Font.BOLD));

		previousPageAction = new PreviousPageAction();
		previousPageAction.setEnabled(false);
		previousPageButton = new JButton(previousPageAction);

		nextPageAction = new NextPageAction();
		nextPageAction.setEnabled(false);
		nextPageButton = new JButton(nextPageAction);

		firstPageAction = new FirstPageAction();
		firstPageAction.setEnabled(false);

		lastPageAction = new LastPageAction();
		lastPageAction.setEnabled(false);

		countAction = new CountAction();
		countAction.setEnabled(false);

		countLabel = new JLabel("Lignes : ");
		countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD));

		countCardLayout = new CardLayout();
		countPanel = new JPanel(countCardLayout);
		countButton = new JButton(countAction);
		countPanel.add(countButton, "countButton");
		countPanel.add(countLabel, "countLabel");
		countPanel.add(new JPanel(), "blank");

		countCardLayout.show(countPanel, "blank");

		noResultPanel = new NoResultPanel();

		if (popupMenu != null) {
			table.addMouseListener(new PopupMenuMouseListener(popupMenu));
		}

		exportPageCommandMenuButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportCurrentPageExcelEllipsis(), null);
		exportPageCommandMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tablePageComponentContext.exportExcel(false);
			}
		});
		exportAllCommandMenuButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportAllPagesExcelEllipsis(), null);
		exportAllCommandMenuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tablePageComponentContext.exportExcel(true);
			}
		});

		exportCommandButton = new JCommandButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportEllipsis(), ImageWrapperResizableIcon.getIcon(IconHelper.Icons.EXPORT_EXCEL.getFileURL(),
				new Dimension(16, 16)));
		exportCommandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tablePageComponentContext.exportExcel(false);
			}
		});
		exportCommandButton.setEnabled(false);
		exportCommandButton.setCommandButtonKind(CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
		exportCommandButton.setDisplayState(CommandButtonDisplayState.MEDIUM);
		exportCommandButton.setPopupCallback(new PopupPanelCallback() {
			@Override
			public JPopupPanel getPopupPanel(JCommandButton commandButton) {
				JCommandPopupMenu pp = new JCommandPopupMenu();
				pp.addMenuButton(exportPageCommandMenuButton);
				pp.addMenuButton(exportAllCommandMenuButton);
				return pp;
			}
		});
	}

	protected String buildTableName() {
		return new StringBuilder().append(this.getClass().getName()).append("_").append(tablePageComponentContext.getClass().getName()).append("_").append(componentClass.getName()).append("_")
				.append(Arrays.toString(tableColumns)).toString();
	}

	private void selectSizePage() {
		tablePageComponentContext.selectSizePage(this);
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:60DLU:GROW,3DLU,FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		CellConstraints cc = new CellConstraints();
		JCompositionPanel compositionPanel = new JCompositionPanel();
		compositionPanel.add(new JSyScrollPane(table), new Integer(0));
		compositionPanel.add(noResultPanel, new Integer(1));
		builder.add(compositionPanel, cc.xy(1, 1));
		builder.add(buildPaginationBar(), cc.xy(1, 3));
		return builder.getPanel();
	}

	private JComponent buildContentsWithComponents() {
		FormLayout layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:100DLU:GROW(1.0),3DLU,FILL:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		CellConstraints cc = new CellConstraints();
		builder.add(toolBarComponents, cc.xy(1, 1));
		JCompositionPanel compositionPanel = new JCompositionPanel();
		compositionPanel.add(new JSyScrollPane(table), new Integer(0));
		compositionPanel.add(noResultPanel, new Integer(1));
		builder.add(compositionPanel, cc.xy(1, 3));
		builder.add(buildPaginationBar(), cc.xy(1, 5));
		return builder.getPanel();
	}

	private JComponent buildPaginationBar() {
		FormLayout layout = new FormLayout("DEFAULT,4DLU,DEFAULT,4DLU,DEFAULT,4DLU,FILL:30DLU:NONE,4DLU,DEFAULT,4DLU,DEFAULT,4DLU,DEFAULT,4DLU:GROW(1.0),DEFAULT", "CENTER:DEFAULT:NONE");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(configureButton, cc.xy(1, 1));
		builder.add(new JButton(firstPageAction), cc.xy(3, 1));
		builder.add(previousPageButton, cc.xy(5, 1));
		builder.add(currentPageLabel, cc.xy(7, 1));
		builder.add(nextPageButton, cc.xy(9, 1));
		builder.add(new JButton(lastPageAction), cc.xy(11, 1));
		builder.add(countPanel, cc.xy(13, 1));
		builder.add(exportCommandButton, cc.xy(15, 1));
		return builder.getPanel();
	}

	public JSyTable getTable() {
		return table;
	}

	public List<E> getSelectedComponents() {
		if (table.getSelectedRowCount() < 1) {
			return null;
		}
		List<E> selectedComponents = new ArrayList<E>();

		for (int row : table.getSelectedRows()) {
			if (hasOnlyOnePage()) {
				row = table.convertRowIndexToModel(row);
			}
			// when more than page, works without the converter because the view has a defined row sorter
			selectedComponents.add(componentTableModel.getComponent(row));
		}

		return selectedComponents;
	}

	@Override
	public void setComponents(List<E> components) {
		componentTableModel.setComponents(components);
		if (hasOnlyOnePage()) {
			tableRowSorter.doSort();
		} else {
			// do something here to update table row sorter
		}

		if (CollectionHelper.isEmpty(components)) {
			noResultPanel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().noResult());
			noResultPanel.showGhost();
		} else {
			noResultPanel.hideGhost();
		}
	}

	public DefaultComponentTableModel<E> getComponentTableModel() {
		return componentTableModel;
	}

	@Override
	public void setPaginationView(int currentPage, boolean first, boolean previous, boolean next, boolean last) {
		if (currentPage >= 1) {
			currentPageLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().numOfPage(currentPage));
		} else {
			currentPageLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().numOfPage(0));
		}

		firstPageAction.setEnabled(first);
		previousPageAction.setEnabled(previous);
		if (previous) {
			previousPageButton.setBackground(MORE_PAGES_COLOR);
		} else {
			previousPageButton.setBackground(null);
		}
		nextPageAction.setEnabled(next);
		if (next) {
			nextPageButton.setBackground(MORE_PAGES_COLOR);
		} else {
			nextPageButton.setBackground(null);
		}
		lastPageAction.setEnabled(last);

		exportCommandButton.setEnabled(currentPage > 0);

		Rectangle visibleRect = table.getVisibleRect();
		table.scrollRectToVisible(new Rectangle(0, 0, visibleRect.width, visibleRect.height));

		validate();
	}

	@Override
	public void setCountLine(Integer count) {
		if (count == null || count < 0) {
			countAction.setEnabled(count != null);
			countCardLayout.show(countPanel, count != null ? "countButton" : "blank");
		} else {
			countAction.setEnabled(false);
			countCardLayout.show(countPanel, "countLabel");

			int p = count / tablePageComponentContext.getCurrentPageSize();
			if (count % tablePageComponentContext.getCurrentPageSize() > 0) {
				p++;
			}

			countLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().pagesNumberTotalRow(p, count));
		}
	}

	private class FirstPageAction extends AbstractAction {

		private static final long serialVersionUID = 5046184196838751509L;

		public FirstPageAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().firstPage());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tablePageComponentContext.firstPage();
		}
	}

	private class LastPageAction extends AbstractAction {

		private static final long serialVersionUID = -2896383125429449537L;

		public LastPageAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().lastPage());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tablePageComponentContext.lastPage();
		}
	}

	private class CountAction extends AbstractAction {

		private static final long serialVersionUID = -2896383125429449537L;

		public CountAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().numberOfLines());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tablePageComponentContext.countLines();
		}
	}

	private class PreviousPageAction extends AbstractAction {

		private static final long serialVersionUID = -3631994162438740456L;

		public PreviousPageAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().previousPage());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tablePageComponentContext.previousPage();
		}
	}

	private class NextPageAction extends AbstractAction {

		private static final long serialVersionUID = 9151886045748843457L;

		public NextPageAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().nextPage());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tablePageComponentContext.nextPage();
		}
	}

	private final class MyRowSorterListener implements RowSorterListener {

		@Override
		@SuppressWarnings("unchecked")
		public void sorterChanged(RowSorterEvent e) {
			if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
				// if there is only one page, we stop here, there is no need to refresh
				if (hasOnlyOnePage()) {
					return;
				}
				List<ISortOrder> orderList = new ArrayList<ISortOrder>();

				List<SortKey> keyList = e.getSource().getSortKeys();
				for (SortKey key : keyList) {
					String pn = tableColumns[key.getColumn()];

					if (CompositeField.is(pn)) {
						CompositeField cf = CompositeField.of(pn);
						for (String s : cf.originalNames()) {
							ISortOrder so = ComponentFactory.getInstance().createInstance(ISortOrder.class);
							so.setPropertyName(s);
							so.setAscending((SortOrder.ASCENDING.equals(key.getSortOrder()) ? true : false));
							orderList.add(so);
						}
					} else {
						ISortOrder so = ComponentFactory.getInstance().createInstance(ISortOrder.class);
						so.setPropertyName(pn);
						so.setAscending((SortOrder.ASCENDING.equals(key.getSortOrder()) ? true : false));
						orderList.add(so);
					}
				}
				tablePageComponentContext.sortPage(orderList, !noResultPanel.isGhost());
			}
		}
	}

	private boolean hasOnlyOnePage() {
		if ((!firstPageAction.isEnabled()) && (!lastPageAction.isEnabled())) {
			return false; // true
		}
		return false;
	}

	private final class MyPopupCallback implements PopupPanelCallback {

		@Override
		public JPopupPanel getPopupPanel(JCommandButton commandButton) {
			JCommandPopupMenu cpm = new JCommandPopupMenu();

			int rowCount = table.getRowCount();
			int selectedRowCount = table.getSelectedRowCount();

			String sizePagelabel = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().displayingLines(tablePageComponentContext.getCurrentPageSize());
			String selectionlabel = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectedItems(rowCount, selectedRowCount);

			JCommandMenuButton changeNbButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().changeTheNumberOfDisplayedLines(), null);
			JCommandMenuButton infoButton = new JCommandMenuButton(sizePagelabel, null);
			JCommandMenuButton selectionInfoButton = new JCommandMenuButton(selectionlabel, null);
			JCommandMenuButton selectAllButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectAll(), null);
			JCommandMenuButton unselectAllButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().unselectAll(), null);
			JCommandMenuButton reverseSelectionButton = new JCommandMenuButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().reverseSelection(), null);

			changeNbButton.addActionListener(configureSizeListener);
			selectAllButton.addActionListener(selectAllListener);
			unselectAllButton.addActionListener(unselectAllListener);
			reverseSelectionButton.addActionListener(reverseListener);

			cpm.addMenuButton(infoButton);
			if (isLineNumberEditable()) {
				cpm.addMenuButton(changeNbButton);
			}
			cpm.addMenuSeparator();
			cpm.addMenuButton(selectionInfoButton);
			if (table.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
				cpm.addMenuButton(selectAllButton);
				if (table.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
					cpm.addMenuButton(reverseSelectionButton);
				}
			}
			cpm.addMenuButton(unselectAllButton);

			infoButton.setEnabled(false);
			selectionInfoButton.setEnabled(false);
			if (selectedRowCount == rowCount) {
				selectAllButton.setEnabled(false);
			}
			if (selectedRowCount <= 0) {
				unselectAllButton.setEnabled(false);
			}
			if (rowCount <= 0) {
				reverseSelectionButton.setEnabled(false);
			}
			return cpm;
		}
	}

	protected boolean isLineNumberEditable() {
		return lineNumberEditable;
	}

	@Override
	public Set<String> getColumns() {
		Set<String> columns = null;
		if (useAllColumns()) {
			if (columnSet == null && tableColumns != null) {
				columnSet = getColumnList(tableColumns);
				if (IEntity.class.isAssignableFrom(componentClass)) {
					columnSet.add(EntityFields.version().name());
				}
			}
			columns = columnSet;
		} else {
			columns = new HashSet<String>();
			SyTableColumnModel columnModel = table.getYColumnModel();
			int max = columnModel.getColumnCount(false);
			for (int i = 0; i < max; i++) {
				TableColumn column = columnModel.getColumn(i, false);
				CollectionHelper.addAll(columns, column.getIdentifier().toString().replaceAll("\\[|\\]", "").split(","));
			}
		}
		return columns;
	}

	/**
	 * Use all columns for the pagination<br/>
	 * If false, uses only visible columns
	 */
	public boolean useAllColumns() {
		return useAllColumns;
	}

	private Set<String> getColumnList(String[] propertyNames) {
		Set<String> res = new HashSet<String>();
		for (String propertyName : propertyNames) {
			if (CompositeField.is(propertyName)) {
				CompositeField cf = CompositeField.of(propertyName);
				res.addAll(getColumnList(cf.originalNames()));
			} else {
				res.add(propertyName);
			}
		}
		return res;
	}

	/**
	 * Export components in excel file
	 *
	 * @param file
	 * @param components
	 */
	public void exportExcel(File file, List<E> components) throws Exception {
		excelTableFileWriter.createExcelFromTable(file, components);
	}

	public final IExcelTableFileWriter<E> getExcelTableFileWriter() {
		return excelTableFileWriter;
	}

	public final NoResultPanel getNoResultPanel() {
		return noResultPanel;
	}

	private final class MyRowSorter extends TableRowSorter<DefaultComponentTableModel<E>> {

		public MyRowSorter() {
			super(componentTableModel);
		}

		/**
		 * Do nothing... yet!
		 */
		@Override
		public void sort() {
			// if there is only one page, we sort without refreshing
			if (hasOnlyOnePage()) {
				doSort();
			}
		}

		private void doSort() {
			super.sort();
		}
	}
}
