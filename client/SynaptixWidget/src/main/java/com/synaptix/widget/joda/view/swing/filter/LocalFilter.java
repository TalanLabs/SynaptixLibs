package com.synaptix.widget.joda.view.swing.filter;

import javax.swing.RowFilter;

import org.joda.time.base.BaseLocal;

import com.synaptix.swing.table.filter.SyRowFilter.SyComparaisonType;

/*protected package*/class LocalFilter extends RowFilter<Object, Object> {

	private BaseLocal date;

	private SyComparaisonType type;

	private int columnIndex;

	public LocalFilter(SyComparaisonType type, BaseLocal date, int columnIndex) {
		super();
		this.columnIndex = columnIndex;
		this.type = type;
		this.date = date;

		if (type == null) {
			throw new IllegalArgumentException("type must be non-null"); //$NON-NLS-1$
		}
	}

	public boolean include(Entry<? extends Object, ? extends Object> value) {
		Object v = value.getValue(columnIndex);

		if (v != null && v instanceof BaseLocal) {
			BaseLocal vDate = (BaseLocal) v;
			switch (type) {
			case BEFORE:
				return vDate.isBefore(date);
			case BEFORE_OR_EQUAL:
				return !vDate.isAfter(date);
			case AFTER:
				return vDate.isAfter(date);
			case AFTER_OR_EQUAL:
				return !vDate.isBefore(date);
			case EQUAL:
				return vDate.isEqual(date);
			case NOT_EQUAL:
				return !vDate.isEqual(date);
			case NOT_NULL:
				return true;
			default:
				break;
			}
		} else {
			switch (type) {
			case NULL:
				return true;
			default:
				break;
			}
		}
		return false;
	}
}