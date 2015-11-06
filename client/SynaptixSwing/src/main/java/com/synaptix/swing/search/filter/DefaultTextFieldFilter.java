package com.synaptix.swing.search.filter;

import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.synaptix.swing.search.AbstractFilter;
import com.synaptix.swing.utils.ClipboardHelper;
import com.synaptix.swing.widget.JSyTextField;

public class DefaultTextFieldFilter extends AbstractFilter {

	private String id;

	private String name;

	private JTextField textField;

	private JTextField defaultTextField;

	private int maxLength;

	private boolean forceUppercase;

	public DefaultTextFieldFilter(String name) {
		this(name, name);
	}

	public DefaultTextFieldFilter(String name, int width) {
		this(name, name, width);
	}

	public DefaultTextFieldFilter(String name, int width, boolean useDefault, String defaultValue) {
		this(name, name, width, 0, false, useDefault, defaultValue);
	}

	public DefaultTextFieldFilter(String name, int width, int maxLength, boolean forceUppercase) {
		this(name, name, width, maxLength, forceUppercase, true, null);
	}

	public DefaultTextFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultTextFieldFilter(String id, String name, int maxLength, boolean forceUppercase) {
		this(id, name, 75, maxLength, forceUppercase, true, null);
	}

	public DefaultTextFieldFilter(String id, String name, int width) {
		this(id, name, width, 0, false, true, null);
	}

	/**
	 * Create a default text field filter.
	 * 
	 * @param id
	 *            Default value: same value as name parameter
	 * @param name
	 * @param width
	 *            Default value: 75.
	 * @param maxLength
	 *            Set maximum length of the field. 0 means no maximum. Default value: 0.
	 * @param forceUppercase
	 *            Force uppercase. Default value: false.
	 * @param useDefault
	 *            Default value: true.
	 * @param defaultValue
	 *            Default value: null.
	 */
	public DefaultTextFieldFilter(String id, String name, int width, int maxLength, boolean forceUppercase, boolean useDefault, String defaultValue) {
		super();
		this.id = id;
		this.name = name;
		textField = new JSyTextField(maxLength, forceUppercase);
		textField.setPreferredSize(new Dimension(width, textField.getPreferredSize().height));
		textField.setName(id);

		if (useDefault) {
			defaultTextField = new JSyTextField(maxLength, forceUppercase);
			defaultTextField.setPreferredSize(new Dimension(width, defaultTextField.getPreferredSize().height));
			setDefaultValue(defaultValue);
		}

		initialize();

		ClipboardHelper.installClipboardPasteListener(textField);
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
