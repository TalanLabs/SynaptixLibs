package com.synaptix.swing.table.filter;

import java.io.Serializable;

import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.DateTimeUtils;

public class HourSearch implements Serializable {

	private static final long serialVersionUID = -7340541592357621397L;

	private int comparaisonType;

	private Integer minutes;

	public HourSearch(int comparaisonType, Integer minutes) {
		this.comparaisonType = comparaisonType;
		this.minutes = minutes;
	}

	public int getComparaisonType() {
		return comparaisonType;
	}

	public Integer getMinutes() {
		return minutes;
	}

	private String toHourString() {
		return minutes == null ? "" : DateTimeUtils.toHoursString(minutes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		switch (comparaisonType) {
		case 0:
			sb.append("> "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 1:
			sb.append(">= "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 2:
			sb.append("< "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 3:
			sb.append("<= "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 4:
			sb.append("= "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 5:
			sb.append("<> "); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		case 6:
			sb.append(SwingMessages.getString("NumberSearch.6")); //$NON-NLS-1$
			break;
		case 7:
			sb.append(SwingMessages.getString("NumberSearch.7")); //$NON-NLS-1$
			break;
		default:
			sb.append("="); //$NON-NLS-1$
			sb.append(toHourString());
			break;
		}

		return sb.toString();
	}
}