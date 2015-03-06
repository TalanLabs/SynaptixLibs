package com.synaptix.widget.view.swing.tablemodel;

import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.widget.view.swing.tablemodel.DefaultColumnTableFactory.ICompositeValueFactory;
import com.synaptix.widget.view.swing.tablemodel.field.CompositeField;

public class DefaultCompositeValueFactory implements ICompositeValueFactory {

	private String separator;

	public DefaultCompositeValueFactory() {
		this(" ");
	}

	public DefaultCompositeValueFactory(String separator) {
		super();

		this.separator = separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	@Override
	public <E extends IComponent> String createValueString(Class<E> componentClass, String[] propertyNames, String format, E component) {
		if (format == null) {
			return _createValueString(componentClass, propertyNames, component);
		} else {
			return _createValueString(componentClass, propertyNames, format, component);
		}

	}

	private <E extends IComponent> String _createValueString(Class<E> componentClass, String[] propertyNames, E component) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		boolean previousNull = true;
		String sep = separator;
		for (String propertyName : propertyNames) {
			if (first) {
				first = false;
			}

			Object value;
			if (CompositeField.is(propertyName)) {
				CompositeField cf = CompositeField.of(propertyName);
				value = _createValueString(componentClass, cf.originalNames(), component);
			} else {
				value = ComponentHelper.getValue(component, propertyName);
			}
			if (value != null) {
				if (!previousNull) {
					sb.append(sep);
				}
				sb.append(value.toString());
				previousNull = false;
			}
		}
		return sb.toString();
	}

	private <E extends IComponent> String _createValueString(Class<E> componentClass, String[] propertyNames, String format, E component) {
		Object[] args = new Object[propertyNames.length];
		int i = 0;
		for (String propertyName : propertyNames) {
			Object value;
			if (CompositeField.is(propertyName)) {
				CompositeField cf = CompositeField.of(propertyName);
				value = createValueString(componentClass, cf.originalNames(), cf.format(), component);
			} else {
				value = ComponentHelper.getValue(component, propertyName);
			}
			args[i] = value;
			i++;
		}
		return String.format(format, args);
	}
}