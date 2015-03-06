package com.synaptix.swing;

public class DayDate {

	private int day;

	private int hour;

	private int minute;

	public DayDate() {
		this(0, 0, 0);
	}

	public DayDate(int day) {
		this(day, 0, 0);
	}

	public DayDate(DayDate dd) {
		this(dd.day, dd.hour, dd.minute);
	}

	public DayDate(int day, int hour, int minute) {
		super();
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public void addDay(int v) {
		this.day += v;
	}

	public void addHour(int v) {
		int r = (v + hour) / 24;
		this.hour = v - r * 24;
		addDay(r);
	}

	public void addMinute(int v) {
		int r = (v + minute) / 60;
		this.minute = v - r * 60;
		addHour(r);
	}

	public void setDayDate(DayDate dayDate) {
		this.day = dayDate.day;
		this.hour = dayDate.hour;
		this.minute = dayDate.minute;
	}

	public long getTimeInMinutes() {
		return day * 24L * 60L + hour * 60L + minute;
	}

	public void setTimeInMinutes(long minutes) {
		this.day = (int) (minutes / (24L * 60L));
		long t1 = day * 24L * 60L;
		this.hour = (int) ((minutes - t1) / 60L);
		long t2 = hour * 60L;
		this.minute = (int) (minutes - t1 - t2);
	}

	public boolean after(DayDate dd) {
		return getTimeInMinutes() > dd.getTimeInMinutes();
	}

	public boolean before(DayDate dd) {
		return getTimeInMinutes() < dd.getTimeInMinutes();
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DayDate) {
			DayDate dd = (DayDate) obj;
			return getTimeInMinutes() == dd.getTimeInMinutes();
		}
		return super.equals(obj);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("( DayDate - >"); //$NON-NLS-1$
		sb.append(" day "); //$NON-NLS-1$
		sb.append(day);
		sb.append(" hour "); //$NON-NLS-1$
		sb.append(hour);
		sb.append(" minute "); //$NON-NLS-1$
		sb.append(minute);
		sb.append(" )"); //$NON-NLS-1$
		return sb.toString();
	}
}
