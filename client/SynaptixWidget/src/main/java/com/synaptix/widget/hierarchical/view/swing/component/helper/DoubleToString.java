package com.synaptix.widget.hierarchical.view.swing.component.helper;

import com.synaptix.swing.utils.GenericObjectToString;

public class DoubleToString implements GenericObjectToString<Double> {

	@Override
	public String getString(final Double t) {
		if (t instanceof Double) {
			return String.valueOf(Math.round(t));
		}
		return null;
	}

}
