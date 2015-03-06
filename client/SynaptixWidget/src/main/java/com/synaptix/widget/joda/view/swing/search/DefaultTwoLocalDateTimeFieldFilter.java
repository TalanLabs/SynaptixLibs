package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDateTime;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultTwoLocalDateTimeFieldFilter extends AbstractTwoLocalFieldFilter<LocalDateTime> {

	public DefaultTwoLocalDateTimeFieldFilter(String id, String name) {
		super(id, name);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDateTime(localField);
	}
}
