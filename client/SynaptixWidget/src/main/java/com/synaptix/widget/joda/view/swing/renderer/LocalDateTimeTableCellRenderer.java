package com.synaptix.widget.joda.view.swing.renderer;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalDateTimeTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<LocalDateTime> {

	private static final long serialVersionUID = -2171627954700057007L;

	public LocalDateTimeTableCellRenderer() {
		super(new AbstractLocalGenericObjectToString<LocalDateTime>() {
			@Override
			protected DateTimeFormatter getDateTimeFormatter() {
				return new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateTimeFormat()).toFormatter();
			}
		});
	}
}
