package com.synaptix.swing.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This class provides some utilities methods to manipulate java.util.date
 * Objects
 * 
 * @author synaptix02
 */
public class DateTimeUtils {
	private static final String LONG_DATE_FORMAT = "dd MMM yyyy HH:mm"; //$NON-NLS-1$
	private static final String SHORT_DATE_FORMAT = "dd/MM/yyyy"; //$NON-NLS-1$
	private static final String SUPER_SHORT_DATE_FORMAT = "ddMMyy"; //$NON-NLS-1$
	private static final String SHORT_TIME_FORMAT = "HH:mm"; //$NON-NLS-1$
	private static final String LONG_TIME_FORMAT = "HH:mm:ss"; //$NON-NLS-1$
	private static final String DATABASE_FORMAT = "dd/MM/yy HH:mm"; //$NON-NLS-1$
	private static final String SHORT_DATABASE_FORMAT = "dd/MM/yyyy"; //$NON-NLS-1$
	private static final String COMPLET_DATE_FORMAT = "EEEE dd MMMMMM yyyy"; //$NON-NLS-1$
	private static final String JOURNAL_VENTES_DATE_FORMAT = "MMyy"; //$NON-NLS-1$

	static DateFormat longFormatter = new SimpleDateFormat(LONG_DATE_FORMAT);

	static DateFormat shortFormatter = new SimpleDateFormat(SHORT_DATE_FORMAT);

	static DateFormat superShortDateFormatter = new SimpleDateFormat(
			SUPER_SHORT_DATE_FORMAT);

	static DateFormat shortTimeFormatter = new SimpleDateFormat(
			SHORT_TIME_FORMAT);

	static DateFormat longTimeFormatter = new SimpleDateFormat(
			LONG_TIME_FORMAT);

	static DateFormat databaseFormatter = new SimpleDateFormat(DATABASE_FORMAT);

	static DateFormat shortDatabaseFormatter = new SimpleDateFormat(
			SHORT_DATABASE_FORMAT);

	static DateFormat completFormatter = new SimpleDateFormat(
			COMPLET_DATE_FORMAT);

	static DateFormat journalVentesFormatter = new SimpleDateFormat(
			JOURNAL_VENTES_DATE_FORMAT);

	/**
	 * dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatShortDate(Date date) {
		return shortFormatter.format(date);
	}

	/**
	 * dd MMM yyyy HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatLongDate(Date date) {
		return longFormatter.format(date);
	}

	/**
	 * HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatShortTime(Date date) {
		return shortTimeFormatter.format(date);
	}

	/**
	 * ddMMyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatSuperShortDate(Date date) {
		return superShortDateFormatter.format(date);
	}

	/**
	 * HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatLongTime(Date date) {
		return longTimeFormatter.format(date);
	}

	/**
	 * dd/MM/yy HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDatabaseDate(Date date) {
		return databaseFormatter.format(date);
	}

	/**
	 * dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatShortDatabaseDate(Date date) {
		return shortDatabaseFormatter.format(date);
	}

	/**
	 * EEEE dd MMMMMM yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatCompletDate(Date date) {
		return completFormatter.format(date);
	}

	/**
	 * MMyy
	 * 
	 * @param date
	 * @return
	 */
	public static String formatJournalVenteDate(Date date) {
		return journalVentesFormatter.format(date);
	}

	public static Date clearHourForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public static Date clearSecondForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	public static int getMinutesFromDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);

		return h * 60 + m;
	}

	public static Date createDateAndHour(Date date, int minutes) {
		Calendar c = Calendar.getInstance();
		c.setTime(clearHourForDate(date));

		int m = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
				+ minutes;
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
				.get(Calendar.DAY_OF_MONTH), 0, m, 0);

		return c.getTime();
	}

	public static String toHoursString(int minutes) {
		int h = minutes / 60;
		int m = minutes - h * 60;
		return new String(
				(h < 10 ? "0" + String.valueOf(h) : String.valueOf(h)) //$NON-NLS-1$
						+ ":" //$NON-NLS-1$
						+ (m < 10 ? "0" + String.valueOf(m) : String.valueOf(m))); //$NON-NLS-1$
	}

	public static int getNumberOfDays(Date dateMin, Date dateMax) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(clearHourForDate(dateMin));

		Calendar cMax = Calendar.getInstance();
		cMax.setTime(clearHourForDate(dateMax));

		long milliseconds = (cMax.getTimeInMillis() + cMax
				.get(Calendar.DST_OFFSET))
				- (cMin.getTimeInMillis() + cMin.get(Calendar.DST_OFFSET));
		return (int) (milliseconds / (1000 * 60 * 60 * 24)) + 1;
	}

	public static long getNumberOfMilliseconds(Date dateMin, Date dateMax) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(dateMin);

		Calendar cMax = Calendar.getInstance();
		cMax.setTime(dateMax);

		return (cMax.getTimeInMillis() + cMax.get(Calendar.DST_OFFSET))
				- (cMin.getTimeInMillis() + cMin.get(Calendar.DST_OFFSET));
	}

	public static long getNumberOfMinutes(Date dateMin, Date dateMax) {
		return getNumberOfMilliseconds(dateMin, dateMax) / (1000 * 60);
	}

	public static int compareToDate(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return c1.compareTo(c2);
	}

	public static boolean includeDates(Date dateMin, Date dateMax, Date date1,
			Date date2) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(dateMin);
		Calendar cMax = Calendar.getInstance();
		cMax.setTime(dateMax);

		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if ((c1.after(cMin) && c1.before(cMax))
				|| (c2.after(cMin) && c2.before(cMax))
				|| (c1.before(cMin) && c2.after(cMax)) || c1.equals(cMin)
				|| c2.equals(cMax)) {
			return true;
		}

		return false;
	}

	public static boolean isFete(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		switch (calendar.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			if (day == 1)
				return true;
			break;
		case Calendar.FEBRUARY:
			break;
		case Calendar.MARCH:
			break;
		case Calendar.APRIL:
			break;
		case Calendar.MAY:
			if (day == 1)
				return true;
			if (day == 8)
				return true;
			break;
		case Calendar.JUNE:
			break;
		case Calendar.JULY:
			if (day == 14)
				return true;
			break;
		case Calendar.AUGUST:
			if (day == 15)
				return true;
			break;
		case Calendar.SEPTEMBER:
			break;
		case Calendar.OCTOBER:
			break;
		case Calendar.NOVEMBER:
			if (day == 1)
				return true;
			if (day == 11)
				return true;
			break;
		case Calendar.DECEMBER:
			if (day == 25)
				return true;
			break;
		}
		Calendar ferie = getPaques(calendar.get(Calendar.YEAR));
		ferie.add(Calendar.DAY_OF_MONTH, 1);
		if (sameDay(ferie.getTime(), calendar.getTime())) {
			return true;
		}

		ferie.add(Calendar.DAY_OF_MONTH, 38);
		if (sameDay(ferie.getTime(), calendar.getTime())) {
			return true;
		}

		ferie.add(Calendar.DAY_OF_MONTH, 11);
		if (sameDay(ferie.getTime(), calendar.getTime())) {
			return true;
		}

		return false;
	}

	private static final Calendar getPaques(int year) {
		if (year <= 1582) {
			throw new IllegalArgumentException(
					"Algorithm invalid before April 1583"); //$NON-NLS-1$
		}
		int golden, century, x, z, d, epact, n;

		golden = (year % 19) + 1; /* E1: metonic cycle */
		century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
		x = (3 * century / 4) - 12; /* E3: leap year correction */
		z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
		d = (5 * year / 4) - x - 10;
		epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
		if ((epact == 25 && golden > 11) || epact == 24)
			epact++;
		n = 44 - epact;
		n += 30 * (n < 21 ? 1 : 0); /* E6: */
		n += 7 - ((d + n) % 7);
		if (n > 31) /* E7: */
			return new GregorianCalendar(year, 4 - 1, n - 31); /* April */
		else
			return new GregorianCalendar(year, 3 - 1, n); /* March */
	}

	public static boolean sameDay(Date date1, Date date2) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(date1);
		Calendar cMax = Calendar.getInstance();
		cMax.setTime(date2);
		return cMin.get(Calendar.DAY_OF_MONTH) == cMax
				.get(Calendar.DAY_OF_MONTH)
				&& cMin.get(Calendar.MONTH) == cMax.get(Calendar.MONTH)
				&& cMin.get(Calendar.YEAR) == cMax.get(Calendar.YEAR);
	}

	public static boolean sameDayHourMinute(Date date1, Date date2) {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(date1);
		Calendar cMax = Calendar.getInstance();
		cMax.setTime(date2);
		return cMin.get(Calendar.MINUTE) == cMax.get(Calendar.MINUTE)
				&& cMin.get(Calendar.HOUR_OF_DAY) == cMax
						.get(Calendar.HOUR_OF_DAY)
				&& cMin.get(Calendar.DAY_OF_MONTH) == cMax
						.get(Calendar.DAY_OF_MONTH)
				&& cMin.get(Calendar.MONTH) == cMax.get(Calendar.MONTH)
				&& cMin.get(Calendar.YEAR) == cMax.get(Calendar.YEAR);
	}
}
