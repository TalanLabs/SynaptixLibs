package com.synaptix.pmgr.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncrementHelper {

	private Integer increment;

	private Calendar calendar;

	private SimpleDateFormat simpleDateFormat;

	private static volatile IncrementHelper instance;
	private static final Object lock = new Object();

	public IncrementHelper() {
		super();

		this.calendar = Calendar.getInstance();
		this.increment = 0;
		this.simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

		calendar.set(Calendar.MILLISECOND, 0);
	}

	public static IncrementHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new IncrementHelper();
				}
			}
		}
		return instance;
	}

	private synchronized Integer getIncrement() {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);
		if (now.compareTo(calendar) == 0) {
			increment++;
		} else {
			calendar = now;
			increment = 0;
		}
		return increment;
	}

	/**
	 * Get a date signature with a unique increment associated to that date<br>
	 * Format: yyyyMMdd_HHmmss_increment
	 * 
	 * @return
	 */
	public String getUniqueDate() {
		Integer increment = getIncrement();
		StringBuilder builder = new StringBuilder(simpleDateFormat.format(calendar.getTime()));
		builder.append("_");
		builder.append(increment);
		return builder.toString();
	}
}
