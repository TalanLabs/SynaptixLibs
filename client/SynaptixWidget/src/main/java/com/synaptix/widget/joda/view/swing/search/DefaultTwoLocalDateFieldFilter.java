package com.synaptix.widget.joda.view.swing.search;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDate;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class DefaultTwoLocalDateFieldFilter extends AbstractTwoLocalFieldFilter<LocalDate> {

	public DefaultTwoLocalDateFieldFilter(String id, String name) {
		super(id, name);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDate(localField);
	}
}
