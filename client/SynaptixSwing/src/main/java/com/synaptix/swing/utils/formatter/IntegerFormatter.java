package com.synaptix.swing.utils.formatter;

import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

// TODO é refaire en mieux
public class IntegerFormatter extends AbstractFormatter {

	private static final long serialVersionUID = -6512163337692274794L;

	@Override
	public Object stringToValue(String text) throws ParseException {
		Object res = null;
		if (text == null) {
			res = null;
		} else {
			try {
				res = Integer.parseInt(text);
			} catch (Exception e) {
			}
		}
		return res;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		String res;
		if (value == null) {
			res = null;
		} else {
			res = String.valueOf(((Integer) value));
		}
		return res;
	}

}
