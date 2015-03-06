package com.synaptix.swing.table.filter;

import java.io.Serializable;
import java.util.Date;

import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.DateTimeUtils;

public class DateSearch implements Serializable {

	private static final long serialVersionUID = 4374698666605409477L;

	private int comparaisonType;

	private boolean useHour;

	private Date date;

	public DateSearch(int comparaisonType, Date date, boolean useHour) {
		this.comparaisonType = comparaisonType;
		this.useHour = useHour;
		this.date = date;
	}

	public int getComparaisonType() {
		return comparaisonType;
	}

	public Date getDate() {
		return date;
	}

	public boolean isUseHour() {
		return useHour;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String text;
		if (useHour) {
			text = DateTimeUtils.formatDatabaseDate(date);
		} else {
			text = DateTimeUtils.formatShortDatabaseDate(date);
		}

		switch (comparaisonType) {
		case 0:
			sb.append("> "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 1:
			sb.append(">= "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 2:
			sb.append("< "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 3:
			sb.append("<= "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 4:
			sb.append("= "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 5:
			sb.append("<> "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 6:
			sb.append(SwingMessages.getString("DateSearch.6")); //$NON-NLS-1$
			break;
		case 7:
			sb.append(SwingMessages.getString("DateSearch.7")); //$NON-NLS-1$
			break;
		default:
			sb.append("="); //$NON-NLS-1$
			sb.append(text);
			break;
		}

		return sb.toString();
	}
}