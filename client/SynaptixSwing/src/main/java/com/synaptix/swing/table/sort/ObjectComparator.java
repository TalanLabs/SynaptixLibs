package com.synaptix.swing.table.sort;

import java.util.Comparator;

public class ObjectComparator implements Comparator<Object> {
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		if (o1 instanceof Comparable<?>) {
			return ((Comparable<Object>)o1).compareTo(o2);
		}
		return o1.toString().compareToIgnoreCase(o2.toString());
	}

}
