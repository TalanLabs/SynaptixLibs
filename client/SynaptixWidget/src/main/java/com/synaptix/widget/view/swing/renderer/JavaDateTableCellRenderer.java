package com.synaptix.widget.view.swing.renderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;

public class JavaDateTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<Date> {

	private static final long serialVersionUID = -2171627954700057007L;

	public JavaDateTableCellRenderer() {
		super(new AbstractDateGenericObjectToString() {
			@Override
			protected DateFormat getDateFormat() {
				return new SimpleDateFormat(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateFormat());
			}
		});
	}
}
