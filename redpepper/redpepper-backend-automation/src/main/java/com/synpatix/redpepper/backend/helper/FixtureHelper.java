package com.synpatix.redpepper.backend.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.google.common.base.CaseFormat;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IdRaw;

public class FixtureHelper {

	/**
	 * Format pattern for LocalTime
	 */
	public static final String TimePattern = "HH:mm:ss";
	/**
	 * Format pattern for LocalDate
	 */
	public static final String DatePattern = "dd/MM/yyyy";
	/**
	 * Format pattern for LocalDateTime
	 */
	public static final String DateTimePattern = "dd/MM/yyyy HH:mm:ss";

	public static boolean isEmptyOrNull(String str) {
		if (StringUtils.isEmpty(str) || str.equals("null") || str.equals("ï¿½") || str.equals("�")) {
			return true;
		}
		return false;
	}

	public static LocalDateTime parseLocalDateTimeFromString(String dateTime) {
		if (isEmptyOrNull(dateTime)) {
			return null;
		}
		return LocalDateTime.parse(dateTime, new DateTimeFormatterBuilder().appendPattern(DateTimePattern).toFormatter());
	}

	public static LocalDate parseDateFromString(String date) {
		if (isEmptyOrNull(date)) {
			return null;
		}
		return LocalDate.parse(date, new DateTimeFormatterBuilder().appendPattern(DatePattern).toFormatter());
	}

	public static LocalTime parseTimeFromString(String time) {
		if (isEmptyOrNull(time)) {
			return null;
		}
		return LocalTime.parse(time, new DateTimeFormatterBuilder().appendPattern(TimePattern).toFormatter());
	}

	public static Double parseDouble(String str) {
		if (isEmptyOrNull(str)) {
			return null;
		}
		return Double.parseDouble(str);
	}

	public static boolean isNotEmptyOrNull(String str) {
		return !isEmptyOrNull(str);
	}

	/**
	 * Returns the string, or null if the string is null or empty or equals to "null".
	 * 
	 * @param str
	 * @return
	 */
	public static String getString(String str) {
		return isEmptyOrNull(str) ? null : str;
	}

	public static Serializable parseId(String str) {
		return isEmptyOrNull(str) ? null : new IdRaw(stringToHexString(str));
	}

	private static String stringToHexString(String str) {
		Pattern p = Pattern.compile("ID\\(([0-9A-F]+)\\)");
		Matcher m = p.matcher(str);
		if (m.matches()) {
			return m.group(1);
		}
		if (str.length() <= 16) {
			str = StringUtils.rightPad(str, 16);
		} else {
			str.substring(0, 16);
		}
		StringBuilder builder = new StringBuilder(str.length() * 2);
		for (int i = 0; i < str.length(); i++) {
			builder.append(Integer.toHexString(str.charAt(i)).toUpperCase());
		}
		return builder.toString();
	}

	/**
	 * Gets entity's ID, or null if the entity is null.
	 * 
	 * @param entity
	 * @return
	 */
	public static IId getId(IEntity entity) {
		return entity == null ? null : entity.getId();
	}

	/**
	 * Parse boolean
	 * 
	 * @param str
	 *            , if null then false
	 * @return
	 */
	public static boolean parseBoolean(String str) {
		if (isEmptyOrNull(str)) {
			return false;
		} else {
			return Boolean.parseBoolean(str);
		}
	}

	/**
	 * Parse enum
	 * 
	 * @param str
	 * @param enumType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Enum<?> parseEnum(Class<? extends Enum> enumType, String str) {
		if (isEmptyOrNull(str)) {
			return null;
		} else {
			if (isNumeric(str)) {
				str = '_' + str;
			}
			return Enum.valueOf(enumType, str);
		}
	}

	public static boolean isNumeric(String str) {
		return str.matches("\\d+"); // match a number with optional '-' and decimal.
	}

	public static BigDecimal parseBigDecimal(String str) {
		if (isEmptyOrNull(str)) {
			return null;
		}
		return new BigDecimal(str);
	}

	public static Class<?> loadClass(String nameClass) {
		try {
			return FixtureHelper.class.getClassLoader().loadClass(nameClass);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Integer parseInteger(String str) {
		if (isEmptyOrNull(str)) {
			return null;
		}
		return new Integer(str);
	}

	/**
	 * @param propertyValue
	 * @return
	 */
	public static Duration parseDuration(String str) {
		return new Duration(parseLong(str));
	}

	public static Duration parseDurationFromTime(String time) {
		if (StringUtils.isEmpty(time)) {
			return null;
		}
		return Duration.millis(LocalTime.parse(time, new DateTimeFormatterBuilder().appendPattern(TimePattern).toFormatter()).getMillisOfDay());
	}

	/**
	 * @param propertyValue
	 * @return
	 */
	public static Long parseLong(String str) {
		if (isEmptyOrNull(str)) {
			return null;
		}
		return Long.parseLong(str);
	}

	public static String parseTestString(String text) {
		if (text == null) {
			return null;
		}
		text = text.trim().replaceAll(" +", " ");
		if (text.contains(" ")) {
			text = text.toLowerCase().replace(" ", "_");
			text = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, text);
		}
		if (text.contains("_")) {
			text = text.toLowerCase();
			text = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, text);
		} else {
			text = StringUtils.uncapitalize(text);
		}

		return text;
	}

	public static String decodeId(String str) {
		return isEmptyOrNull(str) ? null : hexStringToString(str);
	}

	private static String hexStringToString(String hex) {
		if (hex.length() % 2 != 0) {
			System.err.println("requires EVEN number of chars");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		// Convert Hex 0232343536AB into two characters stream.
		for (int i = 0; i < hex.length() - 1; i += 2) {
			// Grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// Convert Hex to Decimal
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
		}
		return sb.toString().trim();
	}
}
