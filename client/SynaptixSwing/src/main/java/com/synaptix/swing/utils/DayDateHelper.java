package com.synaptix.swing.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.synaptix.swing.DayDate;

public final class DayDateHelper {

	private static final Comparator<DayDate> DAY_DATE_COMPARATOR = new Comparator<DayDate>() {
		public int compare(final DayDate o1, final DayDate o2) {
			final long diffMin = o1.getTimeInMinutes() - o2.getTimeInMinutes();
			if (diffMin == 0) {
				return (int) (o1.getTimeInMinutes() - o2.getTimeInMinutes());
			}
			return (int) diffMin;
		}
	};

	private DayDateHelper() {

	}

	public static int compareTo(final DayDate one, final DayDate two) {
		if (one.getDay() == two.getDay()) {
			if (one.getHour() == two.getHour()) {
				if (one.getMinute() == two.getMinute()) {
					return 0;
				}
				return one.getMinute() - two.getMinute();
			}
			return one.getHour() - two.getHour();
		}
		return one.getDay() - two.getDay();
	}

	public static void sort(List<DayDate> list) {
		Collections.sort(list, DAY_DATE_COMPARATOR);
	}
}
