package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultHourFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField hourField;

	private JFormattedTextField defaultHourField;

	public DefaultHourFieldFilter(String name) {
		this(name, name);
	}

	public DefaultHourFieldFilter(String name, boolean useDefault, Integer defaultHour) {
		this(name, name, useDefault, defaultHour);
	}

	public DefaultHourFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultHourFieldFilter(String id, String name, boolean useDefault, Integer defaultHour) {
		this(id, name, 75, useDefault, defaultHour);
	}

	public DefaultHourFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultHourFieldFilter(String id, String name, int width, boolean useDefault, Integer defaultHour) {
		super();
		this.id = id;
		this.name = name;

		hourField = SwingComponentFactory.createHourField(true);
		hourField.setPreferredSize(new Dimension(width, hourField.getPreferredSize().height));

		if (useDefault) {
			defaultHourField = SwingComponentFactory.createHourField(true);
			defaultHourField.setPreferredSize(new Dimension(width, hourField.getPreferredSize().height));
			setDefaultValue(defaultHour);
		}

		initialize();
	}

	@Override
	public JComponent getComponent() {
		return hourField;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Serializable getValue() {
		return (Integer) hourField.getValue();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			hourField.setValue(o);
		} else {
			hourField.setValue(null);
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultHourField != null) {
			return (Integer) defaultHourField.getValue();
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultHourField != null) {
			hourField.setValue(defaultHourField.getValue());
		} else {
			hourField.setValue(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultHourField;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultHourField != null) {
			if (o != null) {
				defaultHourField.setValue(o);
			} else {
				defaultHourField.setValue(null);
			}
		}
	}
}
