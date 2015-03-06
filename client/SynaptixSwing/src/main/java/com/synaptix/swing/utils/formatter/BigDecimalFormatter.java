package com.synaptix.swing.utils.formatter;

import java.math.BigDecimal;
import java.text.ParseException;

import javax.swing.JFormattedTextField.AbstractFormatter;

// TODO é refaire en mieux
public class BigDecimalFormatter extends AbstractFormatter {

	private static final long serialVersionUID = -6512163337692274794L;

	@Override
	public Object stringToValue(String text) throws ParseException {
		Object res = null;
		if (text == null) {
			res = null;
		} else {
			try {
				res = new BigDecimal(text);
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
			res = ((BigDecimal) value).stripTrailingZeros().toPlainString();
		}
		return res;
	}

}
