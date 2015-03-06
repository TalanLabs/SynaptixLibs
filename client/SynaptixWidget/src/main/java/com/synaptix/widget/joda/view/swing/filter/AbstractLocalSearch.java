package com.synaptix.widget.joda.view.swing.filter;

import java.io.Serializable;

import org.joda.time.base.BaseLocal;
import org.joda.time.format.DateTimeFormatter;

import com.synaptix.swing.SwingMessages;

/*proected package*/abstract class AbstractLocalSearch<E extends BaseLocal> implements Serializable {

	private static final long serialVersionUID = 4339237726886904630L;

	private E value;

	private int comparaisonType;

	public AbstractLocalSearch(int comparaisonType, E value) {
		super();

		this.comparaisonType = comparaisonType;
		this.value = value;
	}

	public int getComparaisonType() {
		return comparaisonType;
	}

	public E getValue() {
		return value;
	}

	protected abstract DateTimeFormatter getDateTimeFormatter();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String text = value.toString(getDateTimeFormatter());

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
