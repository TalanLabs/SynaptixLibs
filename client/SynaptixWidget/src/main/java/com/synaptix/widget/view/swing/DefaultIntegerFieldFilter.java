package com.synaptix.widget.view.swing;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultIntegerFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JFormattedTextField textField;

	private JFormattedTextField defaultTextField;

	public DefaultIntegerFieldFilter(String name) {
		this(name, name);
	}

	public DefaultIntegerFieldFilter(String name, int width) {
		this(name, name, width);
	}

	public DefaultIntegerFieldFilter(String name, int width, boolean useDefault, Integer defaultValue) {
		this(name, name, width, useDefault, defaultValue);
	}

	public DefaultIntegerFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultIntegerFieldFilter(String id, String name, boolean useDefault, Integer defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultIntegerFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultIntegerFieldFilter(String id, String name, int width, boolean useDefault, Integer defaultValue) {
		super();
		this.id = id;
		this.name = name;
		textField = SwingComponentFactory.createIntegerField();
		textField.setPreferredSize(new Dimension(width, textField.getPreferredSize().height));

		if (useDefault) {
			defaultTextField = SwingComponentFactory.createIntegerField();
			defaultTextField.setPreferredSize(new Dimension(width, defaultTextField.getPreferredSize().height));
			setDefaultValue(defaultValue);
		}

		initialize();
	}

	@Override
	public JComponent getComponent() {
		return textField;
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
		return textField.getText();
	}

	@Override
	public void setValue(Object o) {
		if (o != null) {
			textField.setText((String) o);
		} else {
			textField.setText(null);
		}
	}

	@Override
	public Serializable getDefaultValue() {
		if (defaultTextField != null) {
			return defaultTextField.getText();
		}
		return null;
	}

	@Override
	public void copyDefaultValue() {
		if (defaultTextField != null) {
			textField.setText(defaultTextField.getText());
		} else {
			textField.setText(null);
		}
	}

	@Override
	public JComponent getDefaultComponent() {
		return defaultTextField;
	}

	@Override
	public void setDefaultValue(Object o) {
		if (defaultTextField != null) {
			if (o != null) {
				defaultTextField.setText((String) o);
			} else {
				defaultTextField.setText(null);
			}
		}
	}
}