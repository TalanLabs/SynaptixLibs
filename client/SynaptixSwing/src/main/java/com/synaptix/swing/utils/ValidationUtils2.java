package com.synaptix.swing.utils;

import java.math.BigDecimal;

public class ValidationUtils2 {

	private ValidationUtils2() {
	}

	public static boolean isInteger(String s) {
		boolean res = true;
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			res = false;
		}
		return res;
	}

	public static boolean isDouble(String s) {
		boolean res = true;
		try {
			Double.parseDouble(s);
		} catch (Exception e) {
			res = false;
		}
		return res;
	}

	public static boolean isLong(String s) {
		boolean res = true;
		try {
			Long.parseLong(s);
		} catch (Exception e) {
			res = false;
		}
		return res;
	}

	public static boolean isBigDecimal(String s) {
		boolean res = true;
		try {
			new BigDecimal(s);
		} catch (Exception e) {
			res = false;
		}
		return res;
	}
}
