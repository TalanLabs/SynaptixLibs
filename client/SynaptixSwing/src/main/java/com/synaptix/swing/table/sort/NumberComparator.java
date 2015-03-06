package com.synaptix.swing.table.sort;

import java.math.BigDecimal;
import java.util.Comparator;

public class NumberComparator implements Comparator<Number> {

	@Override
	public int compare(Number n1, Number n2) {
		BigDecimal d1 = new BigDecimal(n1.toString());
		BigDecimal d2 = new BigDecimal(n2.toString());
		return d1.compareTo(d2);
	}

}
