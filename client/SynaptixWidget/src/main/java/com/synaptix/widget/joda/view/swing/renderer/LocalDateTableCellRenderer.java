package com.synaptix.widget.joda.view.swing.renderer;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalDateTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<LocalDate> {

	private static final long serialVersionUID = -2171627954700057007L;

	public LocalDateTableCellRenderer() {
		super(new AbstractLocalGenericObjectToString<LocalDate>() {
			@Override
			protected DateTimeFormatter getDateTimeFormatter() {
				return new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateFormat()).toFormatter();
			}
		});
	}
}
