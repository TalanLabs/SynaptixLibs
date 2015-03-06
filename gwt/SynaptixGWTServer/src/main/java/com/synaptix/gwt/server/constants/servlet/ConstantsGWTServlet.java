package com.synaptix.gwt.server.constants.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.gwt.server.constants.IHtmlPageCacheManager;
import com.synaptix.gwt.server.constants.IHtmlPageLocaleSession;

public class ConstantsGWTServlet extends HttpServlet {

	private static final long serialVersionUID = -123041678728564224L;

	private static final Log LOG = LogFactory.getLog(ConstantsGWTServlet.class);

	private static final String CONSTANTS_LOCALE = "constants-locale";

	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";

	private static final String LAST_MODIFIED = "Last-Modified";

	private static final String CACHE_CONTROL = "Cache-control";

	private static final String CACHE_CONTROL_VALUE = "must-revalidate";

	private static final String LOCALE_PARAMETER = "locale";

	@Inject
	private IHtmlPageLocaleSession localeSession;

	@Inject
	private IHtmlPageCacheManager htmlPageCacheManager;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String servletPath = request.getRequestURI();
		if (servletPath.startsWith(request.getContextPath())) {
			servletPath = servletPath.substring(request.getContextPath().length());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(servletPath);
		}

		File realFile = new File(this.getServletContext().getRealPath(servletPath));

		try {
			Locale forceLocale = localeSession.getLocale();
			if (forceLocale == null && request.getParameter(LOCALE_PARAMETER) != null) {
				try {
					forceLocale = LocaleUtils.toLocale(request.getParameter(LOCALE_PARAMETER));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}

			Locale existLocale = findExistLocale(request, servletPath);
			boolean notModified;
			Locale locale;
			if (existLocale != null) {
				if (forceLocale != null
						&& (!((forceLocale.getLanguage() == null && existLocale.getLanguage() == null) || forceLocale.getLanguage().equals(existLocale.getLanguage())) || !((forceLocale.getCountry() == null && existLocale
								.getCountry() == null) || forceLocale.getCountry().equals(existLocale.getCountry())))) {
					notModified = false;
					locale = forceLocale;
				} else {
					notModified = true;
					locale = existLocale;
				}
			} else {
				notModified = false;
				if (forceLocale != null) {
					locale = forceLocale;
				} else {
					locale = request.getLocale() != null ? request.getLocale() : Locale.ENGLISH;
				}
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("forceLocale=" + forceLocale + " existLocale=" + existLocale + " locale=" + locale);
			}
			File localeFile = htmlPageCacheManager.getLocaleFile(locale, realFile, servletPath);

			long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE);
			long lastModified = localeFile.lastModified();

			notModified = notModified && (ifModifiedSince != -1 && (ifModifiedSince / 1000) == (lastModified / 1000));
			if (LOG.isDebugEnabled()) {
				LOG.debug("notModified=" + notModified);
			}
			if (notModified) {
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			} else {
				HttpSession session = request.getSession(true);
				if (session != null) {
					request.getSession(true).setAttribute(CONSTANTS_LOCALE, locale);
				}

				Cookie cookie = new Cookie(CONSTANTS_LOCALE, locale.toString());
				response.addCookie(cookie);

				response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUE);
				response.setDateHeader(LAST_MODIFIED, lastModified);
				IOUtils.copy(new FileInputStream(localeFile), response.getOutputStream());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);

			long lastModified = realFile.lastModified();

			response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUE);
			response.setDateHeader(LAST_MODIFIED, lastModified);
			IOUtils.copy(new FileInputStream(realFile), response.getOutputStream());
		}
	}

	private Locale findExistLocale(HttpServletRequest request, String servletPath) {
		Locale locale = null;

		if (locale == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				locale = (Locale) session.getAttribute(CONSTANTS_LOCALE);
			}
		}

		if (locale == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				int i = 0;
				while (i < cookies.length && locale == null) {
					Cookie cookie = cookies[i];
					if (CONSTANTS_LOCALE.equals(cookie.getName())) {
						try {
							locale = LocaleUtils.toLocale(cookie.getValue());
						} catch (Exception e) {
						}
					}
					i++;
				}
			}
		}

		return locale;
	}
}
