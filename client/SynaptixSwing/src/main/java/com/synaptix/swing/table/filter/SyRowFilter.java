package com.synaptix.swing.table.filter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;

import com.synaptix.swing.utils.DateTimeUtils;

public class SyRowFilter {

	public enum SyComparaisonType {
		BEFORE, BEFORE_OR_EQUAL, AFTER, AFTER_OR_EQUAL, EQUAL, NOT_EQUAL, NULL, NOT_NULL
	}

	@SuppressWarnings("unchecked")
	public static <M, I> RowFilter<M, I> booleanFilter(Boolean value, int columnIndex) {
		return (RowFilter<M, I>) new BooleanFilter(value, columnIndex);
	}

	private static class BooleanFilter extends RowFilter<Object, Object> {

		private Boolean b;

		private int columnIndex;

		public BooleanFilter(Boolean value, int columnIndex) {
			super();
			this.columnIndex = columnIndex;
			this.b = value;
		}

		public boolean include(Entry<? extends Object, ? extends Object> value) {
			return b != null ? b.equals((Boolean) value.getValue(columnIndex)) : true;
		}
	}

	@SuppressWarnings("unchecked")
	public static <M, I> RowFilter<M, I> regexFilter(SyComparaisonType type, String regex, int columnIndex) {
		return (RowFilter<M, I>) new RegexFilter(type, Pattern.compile(regex, Pattern.CASE_INSENSITIVE), columnIndex);
	}

	private static class RegexFilter extends RowFilter<Object, Object> {

		private Matcher matcher;

		private SyComparaisonType type;

		private int columnIndex;

		public RegexFilter(SyComparaisonType type, Pattern regex, int columnIndex) {
			super();
			this.columnIndex = columnIndex;
			this.type = type;
			if (regex == null) {
				throw new IllegalArgumentException("Pattern must be non-null"); //$NON-NLS-1$
			}
			matcher = regex.matcher(""); //$NON-NLS-1$
		}

		public boolean include(Entry<? extends Object, ? extends Object> value) {

			switch (type) {
			case EQUAL:
				matcher.reset(value.getStringValue(columnIndex));
				return matcher.find();
			case NOT_EQUAL:
				matcher.reset(value.getStringValue(columnIndex));
				return !matcher.find();
			case NULL:
				return value.getValue(columnIndex) == null;
			case NOT_NULL:
				return value.getValue(columnIndex) != null;
			default:
				break;
			}
			matcher.reset(value.getStringValue(columnIndex));
			return matcher.find();
		}
	}

	@SuppressWarnings("unchecked")
	public static <M, I> RowFilter<M, I> dateFilter(SyComparaisonType type, Date date, boolean useHour, int columnIndex) {
		return (RowFilter<M, I>) new DateFilter(type, date, useHour, columnIndex);
	}

	private static class DateFilter extends RowFilter<Object, Object> {

		private long date;

		private boolean useHour;

		private SyComparaisonType type;

		private int columnIndex;

		public DateFilter(SyComparaisonType type, Date date, boolean useHour, int columnIndex) {
			super();
			this.columnIndex = columnIndex;
			this.type = type;
			if (useHour) {
				this.date = date.getTime();
			} else {
				this.date = DateTimeUtils.clearHourForDate(date).getTime();
			}
			this.useHour = useHour;

			if (type == null) {
				throw new IllegalArgumentException("type must be non-null"); //$NON-NLS-1$
			}
		}

		public boolean include(Entry<? extends Object, ? extends Object> value) {
			Object v = value.getValue(columnIndex);

			if (v != null && v instanceof Date) {
				long vDate = 0;
				if (useHour) {
					vDate = ((Date) v).getTime();
				} else {
					vDate = DateTimeUtils.clearHourForDate((Date) v).getTime();
				}
				switch (type) {
				case BEFORE:
					return (vDate < date);
				case BEFORE_OR_EQUAL:
					return (vDate <= date);
				case AFTER:
					return (vDate > date);
				case AFTER_OR_EQUAL:
					return (vDate >= date);
				case EQUAL:
					return (vDate == date);
				case NOT_EQUAL:
					return (vDate != date);
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

	@SuppressWarnings("unchecked")
	public static <M, I> RowFilter<M, I> numberFilter(SyComparaisonType type, BigDecimal number, int columnIndex) {
		return (RowFilter<M, I>) new NumberFilter(type, number, columnIndex);
	}

	private static class NumberFilter extends RowFilter<Object, Object> {

		private BigDecimal number;

		private SyComparaisonType type;

		private int columnIndex;

		public NumberFilter(SyComparaisonType type, BigDecimal number, int columnIndex) {
			super();
			this.columnIndex = columnIndex;
			this.type = type;
			this.number = number;

			if (type == null) {
				throw new IllegalArgumentException("type must be non-null"); //$NON-NLS-1$
			}

		}

		public boolean include(Entry<? extends Object, ? extends Object> value) {
			Object v = value.getValue(columnIndex);

			if (number == null) {
				switch (type) {
				case NULL:
					return v == null;
				case NOT_NULL:
					return v != null;
				default:
					return true;
				}
			}

			if (v != null && v instanceof Number) {
				int compareResult;
				compareResult = number.compareTo(new BigDecimal(v.toString()));
				switch (type) {
				case BEFORE:
					return (compareResult > 0);
				case BEFORE_OR_EQUAL:
					return (compareResult >= 0);
				case AFTER:
					return (compareResult < 0);
				case AFTER_OR_EQUAL:
					return (compareResult <= 0);
				case EQUAL:
					return (compareResult == 0);
				case NOT_EQUAL:
					return (compareResult != 0);
				case NOT_NULL:
					return true;
				default:
					break;
				}
			} else {
				switch (type) {
				case NULL:
					return true;
				}
			}
			return false;
		}
	}

}
