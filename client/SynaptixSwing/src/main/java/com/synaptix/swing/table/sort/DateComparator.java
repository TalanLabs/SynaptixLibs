package com.synaptix.swing.table.sort;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Date> {

	private Calendar c1 = Calendar.getInstance();
	
	private Calendar c2 = Calendar.getInstance();
	
	@Override
	public int compare(Date d1, Date d2) {
		c1.setTime(d1);
		c2.setTime(d2);
		return c1.compareTo(c2);
	}

}
