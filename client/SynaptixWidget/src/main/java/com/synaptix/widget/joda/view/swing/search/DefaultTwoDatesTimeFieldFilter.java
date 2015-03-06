package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JFormattedTextField;

import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultTwoDatesTimeFieldFilter extends AbstractTwoDatesFieldFilter {

	public DefaultTwoDatesTimeFieldFilter(String name) {
		this(name, name);
	}

	public DefaultTwoDatesTimeFieldFilter(String id, String name) {
		super(id, name);
	}

	@Override
	protected JFormattedTextField createDateField() {
		return SwingComponentFactory.createDateHourField(false, true);
	}
}
