package com.synaptix.widget.view.swing.renderer;

import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JavaDateTimeSecondTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<Date> {

	private static final long serialVersionUID = -2171628524700078107L;

	public JavaDateTimeSecondTableCellRenderer() {
		super(new AbstractDateGenericObjectToString() {
			@Override
			protected DateFormat getDateFormat() {
				return new SimpleDateFormat(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateTimeSecondFormat());
			}
		});
	}
}
