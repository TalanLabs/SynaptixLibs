package com.synaptix.widget.joda.view.swing.renderer;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalTimeTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<LocalTime> {

	private static final long serialVersionUID = -2171627954700057007L;

	public LocalTimeTableCellRenderer() {
		super(new AbstractLocalGenericObjectToString<LocalTime>() {
			@Override
			protected DateTimeFormatter getDateTimeFormatter() {
				return new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()).toFormatter();
			}
		});
	}
}
