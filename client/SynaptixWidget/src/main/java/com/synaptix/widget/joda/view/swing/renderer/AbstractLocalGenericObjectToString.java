package com.synaptix.widget.joda.view.swing.renderer;

import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormatter;

import com.synaptix.swing.utils.GenericObjectToString;

/*package protected*/abstract class AbstractLocalGenericObjectToString<E extends BaseLocal> implements GenericObjectToString<E> {

	private DateTimeFormatter dateTimeFormatter;

	protected abstract DateTimeFormatter getDateTimeFormatter();

	@Override
	public String getString(E t) {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = getDateTimeFormatter();
		}
		return t != null ? t.toString(dateTimeFormatter) : null;
	}
}
