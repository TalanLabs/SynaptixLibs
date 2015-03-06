package com.synaptix.widget.view.swing.tablemodel;

import java.util.Set;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.table.Column;
import com.synaptix.swing.table.SimpleColumn;
import com.synaptix.widget.view.swing.tablemodel.field.CompositeField;

public class DefaultColumnTableFactory<E extends IComponent> implements IColumnTableFactory<E> {

	protected final Class<E> componentClass;

	protected final ConstantsWithLookingBundle constantsWithLookingBundle;

	private ICompositeValueFactory compositeValueFactory;

	protected final Set<String> defaultHidePropertyNames;

	public DefaultColumnTableFactory(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle) {
		this(componentClass, constantsWithLookingBundle, null);
	}

	public DefaultColumnTableFactory(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, String[] defaultHidePropertyNames) {
		this(componentClass, constantsWithLookingBundle, new DefaultCompositeValueFactory(), defaultHidePropertyNames);
	}

	public DefaultColumnTableFactory(Class<E> componentClass, ConstantsWithLookingBundle constantsWithLookingBundle, ICompositeValueFactory compositeValueFactory, String[] defaultHidePropertyNames) {
		super();

		this.componentClass = componentClass;
		this.constantsWithLookingBundle = constantsWithLookingBundle;
		this.compositeValueFactory = compositeValueFactory;
		this.defaultHidePropertyNames = CollectionHelper.asSet(defaultHidePropertyNames);
	}

	@Override
	public Column createColumn(final String propertyName) {
		Class<?> clazz = Object.class;
		if (CompositeField.is(propertyName)) {
			clazz = String.class;
		} else {
			clazz = ComponentHelper.getFinalClass(componentClass, propertyName);
			if (clazz == null) {
				clazz = Object.class;
			}
		}
		return new SimpleColumn(propertyName, constantsWithLookingBundle.getString(propertyName), clazz, isDefaultVisible(propertyName), isLock(propertyName), isSearchable(propertyName),
				isSortable(propertyName));
	}

	public void setCompositeValueFactory(ICompositeValueFactory compositeValueFactory) {
		this.compositeValueFactory = compositeValueFactory;
	}

	public ICompositeValueFactory getCompositeValueFactory() {
		return compositeValueFactory;
	}

	protected boolean isDefaultVisible(String propertyName) {
		return defaultHidePropertyNames != null ? !defaultHidePropertyNames.contains(propertyName) : true;
	}

	protected boolean isLock(String propertyName) {
		return false;
	}

	protected boolean isSearchable(String propertyName) {
		return true;
	}

	protected boolean isSortable(String propertyName) {
		return true;
	}

	@Override
	public Object getValue(String propertyName, E component) {
		if (CompositeField.is(propertyName)) {
			CompositeField cf = CompositeField.of(propertyName);
			return compositeValueFactory.createValueString(componentClass, cf.originalNames(), cf.format(), component);
		} else {
			return ComponentHelper.getValue(component, propertyName);
		}
	}

	@Override
	public void setValue(String propertyName, E component, Object value) {
	}

	public interface ICompositeValueFactory {

		public <E extends IComponent> String createValueString(Class<E> componentClass, String[] propertyNames, String format, E component);

	}
}
