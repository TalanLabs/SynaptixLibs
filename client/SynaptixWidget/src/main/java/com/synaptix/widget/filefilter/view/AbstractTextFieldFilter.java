package com.synaptix.widget.filefilter.view;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.synaptix.swing.search.AbstractFilter;

public abstract class AbstractTextFieldFilter<T> extends AbstractFilter {

	private String id;

	private String name;

	private JTextField textField;

	private JTextField defaultTextField;

	private T value;

	public AbstractTextFieldFilter(String name) {
		this(name, name);
	}

	public AbstractTextFieldFilter(String name, int width) {
		this(name, name, width);
	}

	public AbstractTextFieldFilter(String name, int width, boolean useDefault, String defaultValue) {
		this(name, name, width, useDefault, defaultValue);
	}

	public AbstractTextFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public AbstractTextFieldFilter(String id, String name, boolean useDefault, String defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public AbstractTextFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public AbstractTextFieldFilter(String id, String name, int width, boolean useDefault, String defaultValue) {
		super();
		this.id = id;
		this.name = name;
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(width, textField.getPreferredSize().height));
		textField.getDocument().addDocumentListener(new MyDocumentListener());

		if (useDefault) {
			defaultTextField = new JTextField();
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
		return (Serializable) value;
	}

	@Override
	public void setValue(Object o) {
		value = (T) o;
		if (o != null) {
			textField.setText(convertToString(value));
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

	private final class MyDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			value = convertToValue(textField.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			value = convertToValue(textField.getText());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			value = convertToValue(textField.getText());
		}
	}

	protected abstract T convertToValue(String val);

	protected abstract String convertToString(T string);

}
