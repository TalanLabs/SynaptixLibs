package com.synaptix.swing.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmailHelper {

	private EmailHelper() {

	}

	/**
	 * Cette méthode prend en argument un email et vérifie qu'il est bien
	 * formaté.
	 * 
	 * @param String
	 * @return boolean
	 */
	private static boolean isEmailAdress(final String email) {
		final Pattern p = Pattern
				.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$"); //$NON-NLS-1$
		final Matcher m = p.matcher(email.toUpperCase());
		return m.matches();
	}

	/**
	 * Cette méthode prend en argument un email et renvoie vrai s'il est null ou
	 * mal formaté.
	 * 
	 * @param String
	 * @return boolean
	 */
	public static boolean isNotValidEmailAddress(final String address) {
		return address == null || !isEmailAdress(address);
	}
}
