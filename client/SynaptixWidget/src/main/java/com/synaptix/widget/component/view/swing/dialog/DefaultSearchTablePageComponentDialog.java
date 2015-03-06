package com.synaptix.widget.component.view.swing.dialog;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.utils.DoubleClickTableMouseListener;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.component.view.ISearchComponentsDialogView;
import com.synaptix.widget.component.view.swing.DefaultSearchTablePageComponentsPanel;
import com.synaptix.widget.component.view.swing.descriptor.DefaultSearchPanelDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.descriptor.AbstractSearchViewDescriptor;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;

/**
 * A dialog that contains search fields and a table to display IComponents.
 * <p>
 * Can be used through the <code>DefaultSearchComponentController</code>.
 * 
 * @author NSE
 * 
 * @param <E>
 *            Type of the elements to display in the dialog's table. E must extend IComponent.
 */
public class DefaultSearchTablePageComponentDialog<E extends IComponent> extends AbstractSimpleDialog2 implements ISearchComponentsDialogView<E> {

	private static final long serialVersionUID = -1694369416612119666L;

	private Class<E> componentClass;

	private List<E> selectedComponents;

	private DefaultSearchTablePageComponentsPanel<E> searchPanel;

	private ISearchComponentsContext searchComponentsContext;

	private AbstractSearchViewDescriptor<E> viewDescriptor;

	private boolean startSearch;

	/**
	 * Creates a new <code>DefaultSearchComponentDialog</code>
	 * 
	 * @param componentClass
	 *            Class of the components to search and display.
	 * @param constantsWithLookingBundle
	 *            A constants bundle where the internationalized names for the columns can be found.
	 * @param filterColumns
	 *            List of search fields to display in the dialog.
	 * @param tableColumns
	 *            List of columns to show in the table.
	 */
	public DefaultSearchTablePageComponentDialog(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(componentClass, new DefaultSearchPanelDescriptor<E>(constantsWithLookingBundle, filterColumns, tableColumns));
	}

	/**
	 * Creates a new <code>DefaultSearchComponentDialog</code> with a search view descriptor
	 * 
	 * @param componentClass
	 * @param constantsWithLookingBundle
	 * @param filterColumns
	 * @param tableColumns
	 * @param viewDescriptor
	 */
	public DefaultSearchTablePageComponentDialog(Class<E> componentClass, AbstractSearchViewDescriptor<E> viewDescriptor) {
		super();

		this.componentClass = componentClass;
		this.viewDescriptor = viewDescriptor;
	}

	@Override
	public final void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext) {
		this.searchComponentsContext = searchComponentsContext;
	}

	@Override
	public void create() {
		initComponents();

		initialize();
	}

	/**
	 * Create search table page components panel
	 * 
	 * @return
	 */
	protected DefaultSearchTablePageComponentsPanel<E> createSearchTablePageComponentsPanel() {
		return new DefaultSearchTablePageComponentsPanel<E>(componentClass, viewDescriptor);
	}

	private void initComponents() {
		searchPanel = createSearchTablePageComponentsPanel();
		searchPanel.setSearchComponentsContext(searchComponentsContext);
		searchPanel.create();
		searchPanel.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updateValidation();
				}
			}
		});

		searchPanel.getTable().addMouseListener(new DoubleClickTableMouseListener(searchPanel.getTable(), new AbstractAction() {

			private static final long serialVersionUID = 8186765148233412961L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (acceptAction.isEnabled() && !readOnly) {
					acceptAction.actionPerformed(e);
				}
			}
		}));

		AbstractSimpleSpecialTableModel model = (AbstractSimpleSpecialTableModel) searchPanel.getTable().getModel();

		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		for (String a : cd.getPropertyNames()) {
			PropertyDescriptor cf = cd.getPropertyDescriptor(a);
			DatabasePropertyExtensionDescriptor d = (DatabasePropertyExtensionDescriptor) cf.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if ((cf == null) || (d == null) || (d.getColumn() == null) || (d.getColumn().getSqlName() == null) || (d.getColumn().getSqlName().isEmpty())) {
				int col = model.findColumnIndexById(a);
				if (col >= 0) {
					SyTableColumn tc = (SyTableColumn) searchPanel.getTable().getYColumnModel().getColumn(col, true);
					tc.setSortable(false);
				}
			}
		}

	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:MAX(PREF;300DLU):GROW(1.0)", "FILL:MAX(200DLU;PREF):GROW(1.0)");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(searchPanel, cc.xy(1, 1));
		return builder.getPanel();
	}

	@Override
	protected String getDialogId() {
		return this.getClass().getName() + "_" + searchComponentsContext.getClass().getName() + "_" + componentClass.getName();
	}

	@Override
	public int showDialog(IView parent, Map<String, Object> filters, String title, String subtitle, boolean multipleSelection, boolean readOnly, boolean startSearch) {
		if (multipleSelection) {
			searchPanel.getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			searchPanel.getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		if ((filters != null) && (searchPanel.getValueFilters() != null)) {
			for (Entry<String, Object> entry : filters.entrySet()) {
				if (searchPanel.getValueFilters().containsKey(entry.getKey())) {
					searchPanel.setFilterValue(entry.getKey(), entry.getValue());
				}
			}
		}

		this.startSearch = startSearch;

		return super.showDialog(parent, title, subtitle, readOnly);
	}

	@Override
	protected void openDialog() {
		if (startSearch || viewDescriptor.isSearchAtOpening()) {
			searchComponentsContext.searchComponents(searchPanel.getValueFilters());
		}
		updateValidation();
	}

	@Override
	protected void accept() {
		selectedComponents = searchPanel.getTablePageComponentsPanel().getSelectedComponents();
	}

	@Override
	public List<E> getSelectedComponents() {
		if (selectedComponents == null) {
			return Collections.emptyList();
		}
		return selectedComponents;
	}

	@Override
	public void setComponents(List<E> components) {
		searchPanel.setComponents(components);
	}

	@Override
	public void setCountLine(Integer count) {
		searchPanel.setCountLine(count);
	}

	@Override
	public void setPaginationView(int currentPage, boolean first, boolean previous, boolean next, boolean last) {
		searchPanel.setPaginationView(currentPage, first, previous, next, last);
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (searchPanel.getTable().getSelectedRowCount() < 1) {
			result.addError(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().selectAnItem());
		}
	}

	@Override
	public Set<String> getColumns() {
		return searchPanel.getColumns();
	}

	@Override
	public void exportExcel(File file, List<E> components) throws Exception {
		searchPanel.exportExcel(file, components);
	}

	@Override
	public void setValueFilters(Map<String, Object> map) {
		searchPanel.setValueFilters(map);
	}
}
