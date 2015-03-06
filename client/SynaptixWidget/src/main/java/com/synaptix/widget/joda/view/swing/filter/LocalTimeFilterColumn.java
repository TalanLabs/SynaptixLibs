package com.synaptix.widget.joda.view.swing.filter;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalTime;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class LocalTimeFilterColumn extends AbstractLocalFilterColumn<LocalTime, LocalTimeSearch> {

	@Override
	protected LocalTimeSearch createLocalSearch(int comparaisonType, LocalTime local) {
		return new LocalTimeSearch(comparaisonType, local);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalTime(localField);
	}

	@Override
	protected LocalTime newDefaultLocal() {
		return new LocalTime();
	}
}
