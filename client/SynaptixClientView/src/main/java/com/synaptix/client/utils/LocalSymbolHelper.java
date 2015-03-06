package com.synaptix.client.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public final class LocalSymbolHelper {

	private static String[] shortWeekDays;

	private static String[] weekDays;

	private static String[] shortMonths;

	private static String[] months;

	static {
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		shortWeekDays = dateFormatSymbols.getShortWeekdays();
		weekDays = dateFormatSymbols.getWeekdays();
		shortMonths = dateFormatSymbols.getShortMonths();
		months = dateFormatSymbols.getMonths();
	}

	private LocalSymbolHelper() {
	}

	// short day
	public static String getShortWeekDay(final int day) {
		return shortWeekDays[day];
	}

	public static String getShortMonday() {
		return getShortWeekDay(Calendar.MONDAY);
	}

	public static String getShortTuesday() {
		return getShortWeekDay(Calendar.TUESDAY);
	}

	public static String getShortWednesday() {
		return getShortWeekDay(Calendar.WEDNESDAY);
	}

	public static String getShortThursday() {
		return getShortWeekDay(Calendar.THURSDAY);
	}

	public static String getShortFriday() {
		return getShortWeekDay(Calendar.FRIDAY);
	}

	public static String getShortSaturday() {
		return getShortWeekDay(Calendar.SATURDAY);
	}

	public static String getShortSunday() {
		return getShortWeekDay(Calendar.SUNDAY);
	}

	// day
	public static String getWeekDay(final int day) {
		return weekDays[day];
	}

	public static String getMonday() {
		return getWeekDay(Calendar.MONDAY);
	}

	public static String getTuesday() {
		return getWeekDay(Calendar.TUESDAY);
	}

	public static String getWednesday() {
		return getWeekDay(Calendar.WEDNESDAY);
	}

	public static String getThursday() {
		return getWeekDay(Calendar.THURSDAY);
	}

	public static String getFriday() {
		return getWeekDay(Calendar.FRIDAY);
	}

	public static String getSaturday() {
		return getWeekDay(Calendar.SATURDAY);
	}

	public static String getSunday() {
		return getWeekDay(Calendar.SUNDAY);
	}

	// short month
	public static String getShortMonth(final int month) {
		return shortMonths[month];
	}

	public static String getShortJanuary() {
		return getShortMonth(Calendar.JANUARY);
	}

	public static String getShortFebruary() {
		return getShortMonth(Calendar.FEBRUARY);
	}

	public static String getShortMarch() {
		return getShortMonth(Calendar.MARCH);
	}

	public static String getShortApril() {
		return getShortMonth(Calendar.APRIL);
	}

	public static String getShortMay() {
		return getShortMonth(Calendar.MAY);
	}

	public static String getShortJune() {
		return getShortMonth(Calendar.JUNE);
	}

	public static String getShortJuly() {
		return getShortMonth(Calendar.JULY);
	}

	public static String getShortAugust() {
		return getShortMonth(Calendar.AUGUST);
	}

	public static String getShortSeptember() {
		return getShortMonth(Calendar.SEPTEMBER);
	}

	public static String getShortOctober() {
		return getShortMonth(Calendar.OCTOBER);
	}

	public static String getShortNovember() {
		return getShortMonth(Calendar.NOVEMBER);
	}

	public static String getShortDecember() {
		return getShortMonth(Calendar.DECEMBER);
	}

	// months
	public static String getMonth(final int month) {
		return months[month];
	}

	public static String getJanuary() {
		return getMonth(Calendar.JANUARY);
	}

	public static String getFebruary() {
		return getMonth(Calendar.FEBRUARY);
	}

	public static String getMarch() {
		return getMonth(Calendar.MARCH);
	}

	public static String getApril() {
		return getMonth(Calendar.APRIL);
	}

	public static String getMay() {
		return getMonth(Calendar.MAY);
	}

	public static String getJune() {
		return getMonth(Calendar.JUNE);
	}

	public static String getJuly() {
		return getMonth(Calendar.JULY);
	}

	public static String getAugust() {
		return getMonth(Calendar.AUGUST);
	}

	public static String getSeptember() {
		return getMonth(Calendar.SEPTEMBER);
	}

	public static String getOctober() {
		return getMonth(Calendar.OCTOBER);
	}

	public static String getNovember() {
		return getMonth(Calendar.NOVEMBER);
	}

	public static String getDecember() {
		return getMonth(Calendar.DECEMBER);
	}
}
