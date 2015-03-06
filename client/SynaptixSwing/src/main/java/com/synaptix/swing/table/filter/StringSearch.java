package com.synaptix.swing.table.filter;

import java.io.Serializable;

import com.synaptix.swing.SwingMessages;

public class StringSearch implements Serializable {

	private static final long serialVersionUID = 7946835957789677215L;

	private int comparaisonType;

	private String text;

	public StringSearch(int comparaisonType, String text) {
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
			sb.append(text);
			break;
		case 1:
			sb.append("<> "); //$NON-NLS-1$
			sb.append(text);
			break;
		case 2:
			sb.append(SwingMessages.getString("StringSearch.1")); //$NON-NLS-1$
			break;
		case 3:
			sb.append(SwingMessages.getString("StringSearch.2")); //$NON-NLS-1$
			break;
		default:
			sb.append(""); //$NON-NLS-1$
			break;
		}
		return sb.toString();
	}
}
