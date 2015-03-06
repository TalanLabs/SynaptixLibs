package com.synaptix.gwt.server.constants;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DefaultLocaleSession implements IHtmlPageLocaleSession {

	@Inject
	private Provider<HttpSession> httpSessionProvider;

	@Inject
	public DefaultLocaleSession() {
		super();
	}

	@Override
	public Locale getLocale() {
		HttpSession session = httpSessionProvider.get();
		if (session != null) {
			return (Locale) session.getAttribute("locale");
		}
		return null;
	}
}
