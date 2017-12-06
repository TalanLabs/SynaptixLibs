package com.synaptix.widget.component.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.search.JSearchHeader;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.component.view.swing.descriptor.DefaultSearchPanelDescriptor;
import com.synaptix.widget.component.view.swing.searchmodel.DefaultComponentSearchHeaderModel;
import com.synaptix.widget.perimeter.view.swing.PerimetersFilterModel;
import com.synaptix.widget.perimeter.view.swing.PerimetersFilterPanel;
import com.synaptix.widget.perimeter.view.swing.PerimetersFilterPanel.SearchAxisListener;
import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor;
import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor.IPerimeterSearchAxis;
import com.synaptix.widget.view.swing.descriptor.AbstractSearchViewDescriptor;
import com.synaptix.widget.view.swing.helper.PopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.DefaultColumnTableFactory;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

/**
 * Default search panel associated to a component<br>
 * This panel is composed of a search header, filters and a table<br>
 * Use a search view descriptor ({@link AbstractSearchViewDescriptor}) to specify complex filters, define renderers, add action buttons or a detail component
 * 
 * @param <E>
 *            component
 */
/* dockable if view descriptor extends IDockableSearchViewDescriptor */
public class DefaultSearchTablePageComponentsPanel<E extends IComponent> extends WaitComponentFeedbackPanel implements ISearchTablePageComponentsView<E> {

	private static final long serialVersionUID = 4096636078841946108L;

	protected final ConstantsWithLookingBundle constantsWithLookingBundle;

	protected final Class<E> componentClass;

	protected final AbstractSearchViewDescriptor<E> viewDescriptor;

	private ISearchComponentsContext searchComponentsContext;

	private JSearchHeader searchHeader;

	private PerimetersFilterPanel<E> perimetersFilterPanel;

	private DefaultTablePageComponentsPanel<E> tablePageComponentsPanel;

	/**
	 * A default search component panel without any view descriptor (or it can be defined by overwritting the {@link #getViewDescriptor} method)
	 * 
	 * @param componentManagementContext
	 * @param componentClass
	 * @param constantsWithLookingBundle
	 * @param filterColumns
	 * @param tableColumns
	 */
	public DefaultSearchTablePageComponentsPanel(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(componentClass, new DefaultSearchPanelDescriptor<E>(constantsWithLookingBundle, filterColumns, tableColumns));
	}

	public DefaultSearchTablePageComponentsPanel(Class<E> componentClass, AbstractSearchViewDescriptor<E> viewDescriptor) {
		super();

		this.componentClass = componentClass;
		this.constantsWithLookingBundle = viewDescriptor.getDescriptorBundle();
		this.viewDescriptor = viewDescriptor;
	}

	@Override
	public final void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext) {
		this.searchComponentsContext = searchComponentsContext;
	}

	@Override
	public void create() {
		initComponents();

		this.addContent(buildContents());
	}

	/**
	 * Getter for the view descriptor<br>
	 * Can be overwritten
	 * 
	 * @return
	 */
	protected final AbstractSearchViewDescriptor<E> getViewDescriptor() {
		return viewDescriptor;
	}

	protected DefaultTablePageComponentsPanel<E> createDefaultTablePageComponentsPanel() {
		JComponent toolBarComponents = null;
		JPopupMenu popupMenu = null;
		if (hasSearchViewDescriptor()) {
			AbstractSearchViewDescriptor<E> searchViewDescriptor = getViewDescriptor();

			ToolBarActionsBuilder toolBarActionBuilder = new ToolBarActionsBuilder();
			searchViewDescriptor.installToolBar(toolBarActionBuilder);
			toolBarComponents = toolBarActionBuilder.build();

			PopupMenuActionsBuilder popupMenuActionsBuilder = new PopupMenuActionsBuilder();
			searchViewDescriptor.installPopupMenu(popupMenuActionsBuilder);
			popupMenu = popupMenuActionsBuilder.build();
		}

		DefaultTablePageComponentsPanel<E> tablePage;

		IColumnTableFactory<E> columnTableFactory = viewDescriptor.getColumnTableFactory();
		if (columnTableFactory == null) {
			columnTableFactory = new DefaultColumnTableFactory<E>(componentClass, constantsWithLookingBundle, viewDescriptor.getDefaultHideTableColumns());
		}

		tablePage = new DefaultTablePageComponentsPanel<E>(searchComponentsContext, componentClass, columnTableFactory, viewDescriptor.getTableColumns(), toolBarComponents, popupMenu) {

			private static final long serialVersionUID = 2792044787831019763L;

			@Override
			protected boolean isLineNumberEditable() {
				if (hasSearchViewDescriptor()) {
					return getViewDescriptor().isLineEditable();
				}
				return super.isLineNumberEditable();
			}

			@Override
			public boolean useAllColumns() {
				if (hasSearchViewDescriptor()) {
					return getViewDescriptor().useAllColumns();
				}
				return super.useAllColumns();
			}
		};

		return tablePage;
	}

	private void initComponents() {
		IPerimeterDescriptor perimeterDescriptor = null;
		if (hasSearchViewDescriptor()) {
			perimeterDescriptor = getViewDescriptor().createPerimeter();
		}

		DefaultComponentSearchHeaderModel<E> searchHeaderModel = null;
		PerimetersFilterModel<E> perimetersFilterModel = null;

		if ((viewDescriptor.getFilterColumns() != null) && (viewDescriptor.getFilterColumns().length > 0)) {
			if (perimeterDescriptor == null) {
				searchHeaderModel = new DefaultComponentSearchHeaderModel<E>(componentClass, constantsWithLookingBundle, viewDescriptor.getFilterColumns());
			} else {
				perimetersFilterModel = new PerimetersFilterModel<E>(componentClass, constantsWithLookingBundle, viewDescriptor.getFilterColumns());
			}
		}

		tablePageComponentsPanel = createDefaultTablePageComponentsPanel();

		if (hasSearchViewDescriptor()) {
			getViewDescriptor().install(this);
			getViewDescriptor().installTable(getTable());

			if (searchHeaderModel != null) {
				searchHeaderModel.setSpecificFilters(getViewDescriptor().getSpecificFilters());
			} else if (perimetersFilterModel != null) {
				perimetersFilterModel.setSpecificPerimeterFilters(getViewDescriptor().getSpecificPerimeters());
				perimetersFilterModel.initFilterMap(perimeterDescriptor.getDefaultValueFilterMap());
			}
		}

		if (searchHeaderModel != null) {
			searchHeader = new JSearchHeader(searchHeaderModel, this.getClass().getName() + "_" + searchComponentsContext.getClass().getName() + "_" + componentClass.getName());
			searchHeader.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					searchComponentsContext.searchComponents(getValueFilters());
				}
			});
		} else if (perimetersFilterModel != null) {
			perimetersFilterPanel = new PerimetersFilterPanel<E>(perimeterDescriptor, perimetersFilterModel);
			perimetersFilterPanel.addSearchAxisListener(new SearchAxisListener() {

				@Override
				public void searchAxisChanged() {
					searchComponentsContext.setSearchAxis(getCurrentSearchAxis());
				}
			});
			searchComponentsContext.setSearchAxis(getCurrentSearchAxis());
			perimeterDescriptor.installPerimeter(perimetersFilterPanel);
		}

		if (hasSearchViewDescriptor()) {
			getViewDescriptor().initialize();
		}
	}

	public String getCurrentSearchAxis() {
		if (perimetersFilterPanel != null) {
			IPerimeterSearchAxis currentPerimeterSearchAxis = perimetersFilterPanel.getCurrentPerimeterSearchAxis();
			if (currentPerimeterSearchAxis != null) {
				return currentPerimeterSearchAxis.getId();
			}
		}
		return null;
	}

	/**
	 * Get consolidated value filters
	 * 
	 * @return
	 */
	public Map<String, Object> getValueFilters() {
		if (searchHeader != null) {
			return searchHeader.getValueFilters();
		}
		if (perimetersFilterPanel != null) {
			return perimetersFilterPanel.getFinalFiltersMap();
		}
		return null;
	}

	@Override
	public void setValueFilters(Map<String, Object> map) {
		if (searchHeader != null) {
			Set<String> set = new HashSet<String>();
			for (int i = 0; i < searchHeader.getModel().getFilterCount(); i++) {
				set.add(searchHeader.getModel().getFilter(i).getId());
			}
			for (Entry<String, Object> entry : map.entrySet()) {
				if (set.contains(entry.getKey())) {
					searchHeader.setFilterValue(entry.getKey(), entry.getValue());
				}
			}
		} else if (perimetersFilterPanel != null) {
			perimetersFilterPanel.setFiltersMap(map);
		}
	}

	/**
	 * Set a value for a particular filter (search header or perimeter widget)
	 * 
	 * @param filterKey
	 * @param value
	 */
	public final void setFilterValue(String filterKey, Object value) {
		if (searchHeader != null) {
			searchHeader.setFilterValue(filterKey, value);
		} else {
			perimetersFilterPanel.setFilterValue(filterKey, value);
		}
	}

	/**
	 * Put default filters for search header or resets the perimeter fully
	 */
	public void defaultFilters() {
		if (searchHeader != null) {
			searchHeader.defaultFilters();
		} else {
			perimetersFilterPanel.clear(false);
		}
	}

	protected JComponent buildContents() {
		JComponent detailComponent = null;
		if (hasSearchViewDescriptor()) {
			detailComponent = getViewDescriptor().getDetailComponent();
		}
		if (searchHeader != null) {
			return buildContentsWithSearchHeader(detailComponent);
		} else if (perimetersFilterPanel != null) {
			return buildContentsWithPerimeterPanel(detailComponent);
		}
		return buildContentsWithNoSearch(detailComponent);
	}

	private JComponent buildContentsWithNoSearch(JComponent detailComponent) {
		if (detailComponent == null) {
			FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(tablePageComponentsPanel, cc.xy(1, 1));
			return builder.getPanel();
		} else {
			FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePageComponentsPanel, detailComponent);
			splitPane.setOneTouchExpandable(true);
			splitPane.setResizeWeight(0.75);
			builder.add(splitPane, cc.xy(1, 1));
			return builder.getPanel();
		}
	}

	private JComponent buildContentsWithSearchHeader(JComponent detailComponent) {
		if (detailComponent == null) {
			FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:PREF:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(searchHeader, cc.xy(1, 1));
			builder.add(tablePageComponentsPanel, cc.xy(1, 3));
			return builder.getPanel();
		} else {
			FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", "FILL:PREF:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(searchHeader, cc.xy(1, 1));
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePageComponentsPanel, detailComponent);
			splitPane.setOneTouchExpandable(true);
			splitPane.setResizeWeight(0.75);
			builder.add(splitPane, cc.xy(1, 3));
			return builder.getPanel();
		}
	}

	private JComponent buildContentsWithPerimeterPanel(JComponent detailComponent) {
		if (detailComponent == null) {
			FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,4DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$ //$NON-NLS-2$
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(perimetersFilterPanel, cc.xy(1, 1));
			builder.add(tablePageComponentsPanel, cc.xy(3, 1));
			return builder.getPanel();
		} else {
			FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,4DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$ //$NON-NLS-2$
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(perimetersFilterPanel, cc.xy(1, 1)); // maybe the splitPane should include the perimeter in the upper part and let the detail full
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePageComponentsPanel, detailComponent);
			splitPane.setOneTouchExpandable(true);
			splitPane.setResizeWeight(0.75);
			builder.add(splitPane, cc.xy(3, 1));
			return builder.getPanel();
		}
	}

	public final JSearchHeader getSearchHeader() {
		return searchHeader;
	}

	public final PerimetersFilterPanel<E> getPerimetresFilterPanel() {
		return perimetersFilterPanel;
	}

	public final JSyTable getTable() {
		return tablePageComponentsPanel.getTable();
	}

	public final DefaultTablePageComponentsPanel<E> getTablePageComponentsPanel() {
		return tablePageComponentsPanel;
	}

	@Override
	public void setComponents(List<E> components) {
		if (keepSelectionOnChange()) {
			List<E> previouslySelectedComponents = getTablePageComponentsPanel().getSelectedComponents();
			List<Integer> componentsToSelect = new ArrayList<Integer>();
			if ((CollectionHelper.isNotEmpty(previouslySelectedComponents)) && (CollectionHelper.isNotEmpty(components))) {
				for (E component : previouslySelectedComponents) {
					int index = components.indexOf(component);
					if (index >= 0) {
						componentsToSelect.add(index);
					}
				}
			}
			tablePageComponentsPanel.setComponents(components);
			if (!componentsToSelect.isEmpty()) {
				ListSelectionModel selectionModel = getTable().getSelectionModel();
				selectionModel.setValueIsAdjusting(true);
				for (Integer i : componentsToSelect) {
					int row = getTable().convertRowIndexToView(i);
					selectionModel.addSelectionInterval(row, row);
				}
				selectionModel.setValueIsAdjusting(false);
			}
		} else {
			tablePageComponentsPanel.setComponents(components);
		}
	}

	protected boolean keepSelectionOnChange() {
		if (hasSearchViewDescriptor()) {
			return getViewDescriptor().keepSelectionOnChange();
		}
		return false;
	}

	@Override
	public void setPaginationView(int currentPage, boolean first, boolean previous, boolean next, boolean last) {
		tablePageComponentsPanel.setPaginationView(currentPage, first, previous, next, last);
	}

	@Override
	public void setCountLine(Integer count) {
		tablePageComponentsPanel.setCountLine(count);
	}

	private final boolean hasSearchViewDescriptor() {
		if (getViewDescriptor() != null && getViewDescriptor() instanceof AbstractSearchViewDescriptor) {
			return true;
		}
		return false;
	}

	@Override
	public final Set<String> getColumns() {
		if (hasSearchViewDescriptor()) {
			return getViewDescriptor().getColumns();
		}
		return tablePageComponentsPanel.getColumns();
	}

	public ISearchComponentsContext getSearchComponentsContext() {
		return searchComponentsContext;
	}

	@Override
	public void exportExcel(File file, List<E> components) throws Exception {
		tablePageComponentsPanel.exportExcel(file, components);
	}
}
