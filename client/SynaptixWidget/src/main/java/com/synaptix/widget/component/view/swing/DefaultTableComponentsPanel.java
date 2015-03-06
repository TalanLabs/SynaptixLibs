package com.synaptix.widget.component.view.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.PopupMenuMouseListener;
import com.synaptix.widget.component.view.ITableComponentsView;
import com.synaptix.widget.component.view.swing.descriptor.DefaultTablePanelDescriptor;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;
import com.synaptix.widget.view.swing.descriptor.AbstractTableViewDescriptor;
import com.synaptix.widget.view.swing.helper.PopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.DefaultColumnTableFactory;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

public class DefaultTableComponentsPanel<E extends IComponent> extends WaitComponentFeedbackPanel implements ITableComponentsView<E> {

	private static final long serialVersionUID = 7322877788433038447L;

	protected final Class<E> componentClass;

	protected final AbstractTableViewDescriptor<E> viewDescriptor;

	private DefaultComponentTableModel<E> componentTableModel;

	private JSyTable table;

	public DefaultTableComponentsPanel(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		this(componentClass, new DefaultTablePanelDescriptor<E>(constantsWithLookingBundle, tableColumns));
	}

	public DefaultTableComponentsPanel(Class<E> componentClass, AbstractTableViewDescriptor<E> viewDescriptor) {
		super();

		this.componentClass = componentClass;
		this.viewDescriptor = viewDescriptor;

		initComponents();

		this.addContent(buildContents());
	}

	protected final AbstractTableViewDescriptor<E> getViewDescriptor() {
		return viewDescriptor;
	}

	private void initComponents() {
		IColumnTableFactory<E> columnTableFactory = viewDescriptor.getColumnTableFactory();
		if (columnTableFactory == null) {
			columnTableFactory = new DefaultColumnTableFactory<E>(componentClass, getViewDescriptor().getDescriptorBundle(), viewDescriptor.getDefaultHideTableColumns());
			viewDescriptor.setColumnTableFactory(columnTableFactory);
		}
		componentTableModel = new DefaultComponentTableModel<E>(componentClass, columnTableFactory, getViewDescriptor().getTableColumns());

		table = new JSyTable(componentTableModel, buildTableName());
		JodaSwingUtils.decorateTable(table);
		installTable(table);

		viewDescriptor.install(this);
		viewDescriptor.installTable(table);

		viewDescriptor.initialize();
	}

	protected String buildTableName() {
		return new StringBuilder().append(this.getClass().getName()).append("_").append(componentClass.getName()).append("_").append(Arrays.toString(getViewDescriptor().getTableColumns())).toString();
	}

	private JComponent buildContents() {
		JComponent toolBarComponents = null;
		JPopupMenu popupMenu = null;
		AbstractTableViewDescriptor<E> searchViewDescriptor = getViewDescriptor();

		ToolBarActionsBuilder toolBarActionBuilder = new ToolBarActionsBuilder();
		searchViewDescriptor.installToolBar(toolBarActionBuilder);
		toolBarComponents = toolBarActionBuilder.build();

		PopupMenuActionsBuilder popupMenuActionsBuilder = new PopupMenuActionsBuilder();
		searchViewDescriptor.installPopupMenu(popupMenuActionsBuilder);
		popupMenu = popupMenuActionsBuilder.build();

		FormLayout layout = null;
		if (toolBarComponents != null) {
			layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:100DLU:GROW(1.0)");
		} else {
			layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:100DLU:GROW(1.0)");
		}
		PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
		CellConstraints cc = new CellConstraints();
		int line = 1;
		if (toolBarComponents != null) {
			builder.add(toolBarComponents, cc.xy(1, line));
			line += 2;
		}
		builder.add(buildCenter(), cc.xy(1, line));
		if (popupMenu != null) {
			table.addMouseListener(new PopupMenuMouseListener(popupMenu));
		}

		JComponent detailComponent = viewDescriptor.getDetailComponent();
		if (detailComponent == null) {
			return builder.getPanel();
		} else {
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, builder.getPanel(), detailComponent);
			splitPane.setResizeWeight(0.75);
			return splitPane;
		}
	}

	protected Component buildCenter() {
		return new JSyScrollPane(table);
	}

	protected void installTable(JSyTable table) {
		table.setShowTableLines(true);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public final JSyTable getTable() {
		return table;
	}

	public DefaultComponentTableModel<E> getComponentTableModel() {
		return componentTableModel;
	}

	public List<E> getSelectedComponents() {
		if (table.getSelectedRowCount() < 1) {
			return null;
		}
		List<E> res = new ArrayList<E>();
		for (int row : table.getSelectedRows()) {
			res.add(componentTableModel.getComponent(table.convertRowIndexToModel(row)));
		}
		return res;
	}

	@Override
	public void setComponents(List<E> components) {
		componentTableModel.setComponents(components);
	}
}
