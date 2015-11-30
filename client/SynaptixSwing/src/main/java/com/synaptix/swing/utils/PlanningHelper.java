package com.synaptix.swing.utils;

import java.util.Calendar;
import java.util.Date;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;

public final class PlanningHelper {

	private PlanningHelper() {
	}

	public static final Date convertDayDateToDate(Date dateDebut,
			DayDate dayDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateDebut);
		c.add(Calendar.DAY_OF_YEAR, dayDate.getDay());
		c.set(Calendar.HOUR_OF_DAY, dayDate.getHour());
		c.set(Calendar.MINUTE, dayDate.getMinute());
		return c.getTime();
	}

	public static final DayDate convertDateToDayDate(Date dateDebut, Date date) {
		Calendar c = Calendar.getInstance();

		c.setTime(date);
		int dayDepart = DateTimeUtils.getNumberOfDays(dateDebut, DateTimeUtils
				.clearHourForDate(date)) - 1;
		return new DayDate(dayDepart, c.get(Calendar.HOUR_OF_DAY), c
				.get(Calendar.MINUTE));
	}

	public static final PlageDeuxDates giveFirstAndLastDayOfWeekForDate(
			Date date) {
		Calendar aCalendarPremierJourSemaine = Calendar.getInstance();
		aCalendarPremierJourSemaine.setTime(DateTimeUtils
				.clearHourForDate(date));
		aCalendarPremierJourSemaine.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date datePremierJourSemaine = aCalendarPremierJourSemaine.getTime();

		Calendar aCalendarDatefinCleaned = (Calendar) aCalendarPremierJourSemaine
				.clone();
		aCalendarDatefinCleaned.add(Calendar.DAY_OF_WEEK,
				aCalendarDatefinCleaned.getActualMaximum(Calendar.DAY_OF_WEEK));
		aCalendarDatefinCleaned.add(Calendar.MINUTE, -1);
		Date dateDernierJourSemaine = aCalendarDatefinCleaned.getTime();
		return new PlageDeuxDates(datePremierJourSemaine,
				dateDernierJourSemaine);
	}

	public static class PlageDeuxDates {
		PlageDeuxDates(Date aDateprem, Date aDateDerniere) {
			premiereDate = aDateprem;
			derniereDate = aDateDerniere;
		}

		public Date premiereDate;
		public Date derniereDate;

	} // fin class PlageDeuxDates

	public static final DayDate getMinDayDateFromTasks(
			final SimpleDaysTask[] tasks) {
		DayDate dayDateMin = null;
		if (tasks != null && tasks.length > 0) {
			for (SimpleDaysTask task : tasks) {
				if (dayDateMin == null
						|| task.getDayDateMin().before(dayDateMin)) {
					dayDateMin = task.getDayDateMin();
				}
			}

		}
		return dayDateMin;
	}

	public static final DayDate getMaxDayDateFromTasks(
			final SimpleDaysTask[] tasks) {
		DayDate dayDateMax = null;
		if (tasks != null && tasks.length > 0) {
			for (SimpleDaysTask task : tasks) {
				if (dayDateMax == null
						|| task.getDayDateMax().after(dayDateMax)) {
					dayDateMax = task.getDayDateMax();
				}
			}
		}
		return dayDateMax;
	}

	public static DayDate getMinDayDate(final DayDate dayDateOne,
			final DayDate dayDateTwo) {
		if (dayDateOne != null) {
			if (dayDateTwo != null) {
				return dayDateOne.before(dayDateTwo) ? dayDateOne : dayDateTwo;
			} else {
				return dayDateOne;
			}
		} else {
			return dayDateTwo;
		}
	}

	public static DayDate getMinDayDateFromDayDates(final DayDate[] dayDates) {
		if (dayDates != null && dayDates.length > 0) {
			DayDate dayDateMin = new DayDate(Integer.MAX_VALUE);
			for (DayDate dayDate : dayDates) {
				if (dayDateMin.after(dayDate)) {
					dayDateMin = dayDate;
				}
			}
			return dayDateMin;
		}
		return null;
	}
}
