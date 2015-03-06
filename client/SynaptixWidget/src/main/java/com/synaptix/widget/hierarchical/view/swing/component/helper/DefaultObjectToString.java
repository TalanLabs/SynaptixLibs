package com.synaptix.widget.hierarchical.view.swing.component.helper;

import com.synaptix.swing.utils.GenericObjectToString;

public class DefaultObjectToString implements GenericObjectToString<Object> {

	public static final String EMPTY_STRING = "";

	@Override
	public String getString(final Object t) {
		if (t != null) {
			return t.toString();
		}
		return EMPTY_STRING;
	}

}
