package com.synaptix.widget.joda.view.swing.filter;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import org.joda.time.LocalDate;

import com.synaptix.widget.joda.view.swing.JodaSwingUtils;

public class LocalDateFilterColumn extends AbstractLocalFilterColumn<LocalDate, LocalDateSearch> {

	@Override
	protected LocalDateSearch createLocalSearch(int comparaisonType, LocalDate local) {
		return new LocalDateSearch(comparaisonType, local);
	}

	@Override
	protected JComponent decorate(JFormattedTextField localField) {
		return JodaSwingUtils.decorateLocalDate(localField);
	}

	@Override
	protected LocalDate newDefaultLocal() {
		return new LocalDate();
	}
}
