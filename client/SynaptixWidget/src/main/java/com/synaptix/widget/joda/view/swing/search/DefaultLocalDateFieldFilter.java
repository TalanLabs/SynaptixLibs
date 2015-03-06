package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDate;

import com.synaptix.widget.joda.context.DefaultLocal;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultLocalDateFieldFilter extends AbstractLocalFieldFilter<LocalDate> {

	public DefaultLocalDateFieldFilter(String name) {
		this(name, name);
	}

	public DefaultLocalDateFieldFilter(String name, boolean useDefault, DefaultLocal<LocalDate> defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultLocalDateFieldFilter(String id, String name) {
		this(id, name, 90);
	}

	public DefaultLocalDateFieldFilter(String id, String name, boolean useDefault, DefaultLocal<LocalDate> defaultValue) {
		this(id, name, 90, useDefault, defaultValue);
	}

	public DefaultLocalDateFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultLocalDateFieldFilter(String id, String name, int width, boolean useDefault, DefaultLocal<LocalDate> defaultValue) {
		super(id, name, width, useDefault, defaultValue);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDate(localField);
	}

	@Override
	protected LocalDate newTodayLocal() {
		return new LocalDate();
	}
}
