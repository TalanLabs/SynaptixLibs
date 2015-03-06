package com.synaptix.swing.utils.formatter;

import java.text.ParseException;

import javax.swing.text.DefaultFormatter;

import com.jgoodies.validation.util.ValidationUtils;

public class HourFormatter extends DefaultFormatter {

	private static final long serialVersionUID = -6512163337692274794L;

	private static final int MAX_MINUTE = 24 * 60;

	private boolean defaultNull;

	public HourFormatter() {
		this(false);
	}

	public HourFormatter(boolean defaultNull) {
		this.defaultNull = defaultNull;
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		Object res;
		if (defaultNull) {
			res = null;
		} else {
			res = new Integer(0);
		}
		if (text != null && !text.isEmpty()) {
			String[] ss = text.trim().split(":"); //$NON-NLS-1$
			if (ss.length == 1 && text.trim().endsWith(":")) { //$NON-NLS-1$
				if (ss[0] != null && !ss[0].isEmpty()
						&& ValidationUtils.isNumeric(ss[0])) {
					int hours = Integer.parseInt(ss[0]) * 60;
					if (hours < MAX_MINUTE) {
						res = new Integer(hours);
					}
				}
			} else if (ss.length == 1) {
				if (ss[0] != null && !ss[0].isEmpty()
						&& ValidationUtils.isNumeric(ss[0])) {
					int h = Integer.parseInt(ss[0]) / 100;
					if (h >= 24) {
						h = 0;
					}
					int m = Integer.parseInt(ss[0]) - h * 100;
					if (m >= 60) {
						m = 0;
					}
					res = new Integer(h * 60 + m);
				}
			} else if (ss.length >= 2) {
				int h = 0;
				if (ss[0] != null && !ss[0].isEmpty()
						&& ValidationUtils.isNumeric(ss[0])) {
					h = Integer.parseInt(ss[0]);
					if (h >= 24) {
						h = 0;
					}
				}
				int m = 0;
				if (ss[1] != null && !ss[1].isEmpty()
						&& ValidationUtils.isNumeric(ss[1])) {

					m = Integer.parseInt(ss[1]);
					if (m >= 60) {
						m = 0;
					}
				}
				res = new Integer(h * 60 + m);
			}
		}
		return res;
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		String res = "00:00"; //$NON-NLS-1$
		if (defaultNull) {
			res = null;
		} else {
			res = "00:00"; //$NON-NLS-1$
		}
		if (value != null && value instanceof Integer) {
			int hours = ((Integer) value).intValue();
			int h = hours / 60;
			int m = hours - h * 60;
			res = new String((h < 10 ? "0" + String.valueOf(h) : String //$NON-NLS-1$
					.valueOf(h)) + ":" //$NON-NLS-1$
					+ (m < 10 ? "0" + String.valueOf(m) : String.valueOf(m))); //$NON-NLS-1$
		}
		return res;
	}
}
