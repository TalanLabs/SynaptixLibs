package com.synaptix.constants;

import java.util.Locale;

public class DefaultConstantsLocaleSession implements IConstantsLocaleSession {

	private Locale locale;

	public DefaultConstantsLocaleSession() {
		this(Locale.getDefault());
	}

	public DefaultConstantsLocaleSession(Locale locale) {
		super();
		this.locale = locale;
	}

	public void setLocale(Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("locale must not null");
		}
		this.locale = locale;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

}
