package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalTime;

import com.synaptix.widget.joda.context.DefaultLocal;
import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultLocalTimeFieldFilter extends AbstractLocalFieldFilter<LocalTime> {

	public DefaultLocalTimeFieldFilter(String name) {
		this(name, name);
	}

	public DefaultLocalTimeFieldFilter(String name, boolean useDefault, DefaultLocal<LocalTime> defaultValue) {
		this(name, name, useDefault, defaultValue);
	}

	public DefaultLocalTimeFieldFilter(String id, String name) {
		this(id, name, 75);
	}

	public DefaultLocalTimeFieldFilter(String id, String name, boolean useDefault, DefaultLocal<LocalTime> defaultValue) {
		this(id, name, 75, useDefault, defaultValue);
	}

	public DefaultLocalTimeFieldFilter(String id, String name, int width) {
		this(id, name, width, true, null);
	}

	public DefaultLocalTimeFieldFilter(String id, String name, int width, boolean useDefault, DefaultLocal<LocalTime> defaultValue) {
		super(id, name, width, useDefault, defaultValue);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalTime(localField);
	}

	@Override
	protected LocalTime newTodayLocal() {
		return new LocalTime();
	}
}
