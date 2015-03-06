package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.synaptix.swing.search.AbstractFilter;

public class DefaultCheckBoxFilter extends AbstractFilter {

	private String id;

	private String name;

	private JCheckBox checkBox;

	private JCheckBox defaultCheckBox;

	public DefaultCheckBoxFilter(String name) {
		this(name, name);
	}

	public DefaultCheckBoxFilter(String name, int width) {
		this(name, name, width);
	}

	public DefaultCheckBoxFilter(String name, int width, boolean useDefault, boolean defaultValue) {
		this(name, name, width, useDefault, defaultValue);
	}

	public DefaultCheckBoxFilter(String name, int width, String title, boolean useDefault, boolean defaultValue) {
		this(name, name, width, title, useDefault, defaultValue);
	}

	public DefaultCheckBoxFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultCheckBoxFilter(String id, String name, boolean useDefault, Boolean defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultCheckBoxFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultCheckBoxFilter(String id, String name, int width, boolean useDefault, Boolean defaultValue) {
		this(id, name, width, null, useDefault, defaultValue);
	}

	public DefaultCheckBoxFilter(String id, String name, int width, String title, boolean useDefault, Boolean defaultValue) {
		super();
		this.id = id;
		this.name = name;
		checkBox = new JCheckBox(title == null ? "" : title);
		checkBox.setPreferredSize(new Dimension(width, checkBox.getPreferredSize().height));

		if (useDefault) {
			defaultCheckBox = new JCheckBox(title == null ? "Coché" : title);
			defaultCheckBox.setPreferredSize(new Dimension(width, defaultCheckBox.getPreferredSize().height));
			setDefaultValue(defaultValue);
		}

		initialize();
	}

	@Override
	public JComponent getComponent() {
		return checkBox;
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
		return checkBox.isSelected();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			checkBox.setSelected((Boolean) o);
		} else {
			checkBox.setSelected(false);
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultCheckBox != null) {
			return defaultCheckBox.isSelected();
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultCheckBox != null) {
			checkBox.setSelected(defaultCheckBox.isSelected());
		} else {
			checkBox.setSelected(false);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultCheckBox;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultCheckBox != null) {
			if (o != null) {
				defaultCheckBox.setSelected((Boolean) o);
			} else {
				defaultCheckBox.setSelected(false);
			}
		}
	}
}
