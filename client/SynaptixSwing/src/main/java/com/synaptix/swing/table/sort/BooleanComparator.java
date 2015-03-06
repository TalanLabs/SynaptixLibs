package com.synaptix.swing.table.sort;

import java.util.Comparator;

public class BooleanComparator implements Comparator<Boolean> {
	
	@Override
	public int compare(Boolean b1, Boolean b2) {
		return b1.compareTo(b2);
	}

}
