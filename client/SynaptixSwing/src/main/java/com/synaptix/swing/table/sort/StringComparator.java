package com.synaptix.swing.table.sort;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
	
	@Override
	public int compare(String s1, String s2) {
		return s1.toString().compareToIgnoreCase(s2.toString());
	}

}
