package com.synaptix.swing.widget;

import javax.swing.table.TableModel;

import com.synaptix.swing.table.AbstractStringExcelColumnRenderer;
import com.synaptix.swing.utils.DateTimeUtils;

public class HourExcelColumnRenderer extends AbstractStringExcelColumnRenderer {

	public String getString(TableModel tableModel, Object value, int row, int col) {
		String res = null;
		if (value != null && value instanceof Integer) {
			Integer h = (Integer) value;
			res = DateTimeUtils.toHoursString(h);
		} else {
			res = ""; //$NON-NLS-1$
		}
		return res;
	}
}
