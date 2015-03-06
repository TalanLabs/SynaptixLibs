package com.synaptix.widget.joda.view.swing.filter;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDateTime;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class LocalDateTimeFilterColumn extends AbstractLocalFilterColumn<LocalDateTime, LocalDateTimeSearch> {

	@Override
	protected LocalDateTimeSearch createLocalSearch(int comparaisonType, LocalDateTime local) {
		return new LocalDateTimeSearch(comparaisonType, local);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDateTime(localField);
	}

	@Override
	protected LocalDateTime newDefaultLocal() {
		return new LocalDateTime();
	}
}
