package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JFormattedTextField;

import com.synaptix.swing.utils.SwingComponentFactory;

public class DefaultTwoDatesFieldFilter extends AbstractTwoDatesFieldFilter {

	public DefaultTwoDatesFieldFilter(String name) {
		this(name, name);
	}

	public DefaultTwoDatesFieldFilter(String id, String name) {
		super(id, name);
	}

	@Override
	protected JFormattedTextField createDateField() {
		return SwingComponentFactory.createDateField(false, true);
	}
}
