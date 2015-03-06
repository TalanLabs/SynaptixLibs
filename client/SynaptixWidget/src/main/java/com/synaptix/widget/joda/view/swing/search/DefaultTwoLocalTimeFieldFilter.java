package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalTime;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultTwoLocalTimeFieldFilter extends AbstractTwoLocalFieldFilter<LocalTime> {

	public DefaultTwoLocalTimeFieldFilter(String id, String name) {
		super(id, name);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalTime(localField);
	}
}
