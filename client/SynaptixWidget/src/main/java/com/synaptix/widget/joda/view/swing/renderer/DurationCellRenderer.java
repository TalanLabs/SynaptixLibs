package com.synaptix.widget.joda.view.swing.renderer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.synaptix.widget.util.StaticWidgetHelper;

public final class DurationCellRenderer extends SubstanceDefaultTableCellRenderer {

	private static final long serialVersionUID = -3094112896068282323L;

	@Override
	protected void setValue(Object value) {
		if (value instanceof Duration) {
			String text = null;
			long vh = ((Duration) value).getStandardHours();
			long vm = ((Duration) value).getStandardMinutes();

			int days = getDayOfHour((int) vh / 60);
			if (days >= 1) {
				StringBuffer sb = new StringBuffer();
				sb.append(StaticWidgetHelper.getSynaptixDateConstantsBundle().daysShort(days)).append(" ");
				sb.append(toHoursString((int) vm % 1440, ":"));
				text = sb.toString();
			} else {
				text = toHoursString((int) vm, ":");
			}
			super.setValue(text);
		} else {
			super.setValue(value);
		}
	}

	public static final int getDayOfHour(int hour) {
		if (hour < 0) {
			return hour / 24 - 1;
		} else {
			return hour / 24;
		}
	}

	public static final String toHoursString(int minutes, String separator) {
		int h = minutes / 60;
		int m = minutes - h * 60;
		return new StringBuilder().append(StringUtils.leftPad(String.valueOf(h), 2, "0")).append(separator) //$NON-NLS-1$
				.append(StringUtils.leftPad(String.valueOf(m), 2, "0")) //$NON-NLS-1$
				.toString();
	}
}