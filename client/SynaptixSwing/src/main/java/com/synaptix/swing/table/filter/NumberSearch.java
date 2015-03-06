package com.synaptix.swing.table.filter;

import java.io.Serializable;

import com.synaptix.swing.SwingMessages;

public class NumberSearch implements Serializable {

	private static final long serialVersionUID = -7340541592357621397L;

	private int comparaisonType;

	private String text;

	public NumberSearch(int comparaisonType, String text) {
		this.comparaisonType = comparaisonType;
		this.text = text;
	}

	public int getComparaisonType() {
		return comparaisonType;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
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
			sb.append(SwingMessages.getString("NumberSearch.6")); //$NON-NLS-1$
			break;
		case 7:
			sb.append(SwingMessages.getString("NumberSearch.7")); //$NON-NLS-1$
			break;
		default:
			sb.append("="); //$NON-NLS-1$
			sb.append(text);
			break;
		}

		return sb.toString();
	}
}