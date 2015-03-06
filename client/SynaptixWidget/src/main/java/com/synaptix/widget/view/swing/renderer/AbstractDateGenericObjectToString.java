package com.synaptix.widget.view.swing.renderer;

import java.text.DateFormat;
import java.util.Date;

import com.synaptix.swing.utils.GenericObjectToString;

/*package protected*/abstract class AbstractDateGenericObjectToString implements GenericObjectToString<Date> {

	private DateFormat dateFormat;

	protected abstract DateFormat getDateFormat();

	@Override
	public String getString(Date t) {
		if (dateFormat == null) {
			dateFormat = getDateFormat();
		}
		return t != null ? dateFormat.format(t) : null;
	}
}
