package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDateTime;

import com.synaptix.widget.joda.context.DefaultLocal;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultLocalDateTimeFieldFilter extends AbstractLocalFieldFilter<LocalDateTime> {

	public DefaultLocalDateTimeFieldFilter(String name) {
		this(name, name);
	}

	public DefaultLocalDateTimeFieldFilter(String name, boolean useDefault, DefaultLocal<LocalDateTime> defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultLocalDateTimeFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultLocalDateTimeFieldFilter(String id, String name, boolean useDefault, DefaultLocal<LocalDateTime> defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultLocalDateTimeFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultLocalDateTimeFieldFilter(String id, String name, int width, boolean useDefault, DefaultLocal<LocalDateTime> defaultValue) {
		super(id, name, width, useDefault, defaultValue);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDateTime(localField);
	}

	@Override
	protected LocalDateTime newTodayLocal() {
		return new LocalDateTime();
	}
}
