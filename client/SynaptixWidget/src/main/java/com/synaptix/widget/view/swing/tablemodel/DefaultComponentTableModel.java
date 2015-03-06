package com.synaptix.widget.view.swing.tablemodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.Column;

public class DefaultComponentTableModel<E extends IComponent> extends AbstractSimpleSpecialTableModel {

	private static final long serialVersionUID = 481943278261918849L;

	private final IColumnTableFactory<E> columnTableFactory;

	private final String[] propertyNames;

	private final List<Column> columns;

	private List<E> components;

	public DefaultComponentTableModel(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String... propertyNames) {
		this(componentClass, new DefaultColumnTableFactory<E>(componentClass, constantsWithLookingBundle), propertyNames);
	}

	public DefaultComponentTableModel(Class<E> componentClass, IColumnTableFactory<E> columnTableFactory, String... propertyNames) {
		super();

		this.columnTableFactory = columnTableFactory;
		this.propertyNames = propertyNames;

		List<String> pns = new ArrayList<String>();
		if (propertyNames == null || propertyNames.length == 0) {
			ComponentDescriptor descriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
			pns.addAll(descriptor.getPropertyNames());
			Collections.sort(pns);
		} else {
			pns.addAll(Arrays.asList(propertyNames));
		}

		columns = new ArrayList<Column>();
		for (String propertyName : pns) {
			columns.add(columnTableFactory.createColumn(propertyName));
		}
	}

	public final IColumnTableFactory<E> getColumnTableFactory() {
		return columnTableFactory;
	}

	public final String[] getPropertyNames() {
		return propertyNames;
	}

	public void setComponents(List<E> components) {
		this.components = components;
		fireTableDataChanged();
	}

	@Override
	public Column getColumn(int index) {
		return columns.get(index);
	}

	@Override
	public Object getValueAt(int rowIndex, String columnId) {
		E component = components.get(rowIndex);
		return columnTableFactory.getValue(columnId, component);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, String columnId) {
		E component = components.get(rowIndex);
		columnTableFactory.setValue(columnId, component, value);
	}

	@Override
	public int getRowCount() {
		if (this.components != null) {
			return this.components.size();
		}
		return 0;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	public E getComponent(int rowIndex) {
		return components.get(rowIndex);
	}

	public List<E> getComponentList() {
		return components;
	}
}
