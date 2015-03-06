package com.synaptix.gwt.server.guice;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.synaptix.gwt.server.constants.DefaultHtmlPageCacheManager;
import com.synaptix.gwt.server.constants.IHtmlPageCacheManager;
import com.synaptix.gwt.server.constants.IHtmlPageLocaleConverter;
import com.synaptix.gwt.server.constants.IHtmlPageLocaleSession;
import com.synaptix.gwt.server.constants.servlet.ConstantsGWTServlet;

public class SynaptixGWTServerModule extends ServletModule {

	private final Class<? extends IHtmlPageLocaleSession> htmlPageLocaleSession;

	private final Class<? extends IHtmlPageLocaleConverter> defaultHtmlPageLocaleConverter;

	public SynaptixGWTServerModule(Class<? extends IHtmlPageLocaleSession> htmlPageLocaleSession, Class<? extends IHtmlPageLocaleConverter> defaultHtmlPageLocaleConverter) {
		super();
		this.htmlPageLocaleSession = htmlPageLocaleSession;
		this.defaultHtmlPageLocaleConverter = defaultHtmlPageLocaleConverter;
	}

	@Override
	protected void configureServlets() {
		super.configureServlets();

		bind(IHtmlPageLocaleSession.class).to(htmlPageLocaleSession).in(Singleton.class);
		bind(IHtmlPageCacheManager.class).to(DefaultHtmlPageCacheManager.class).in(Singleton.class);
		bind(IHtmlPageLocaleConverter.class).to(defaultHtmlPageLocaleConverter).in(Singleton.class);

		bind(ConstantsGWTServlet.class).in(Singleton.class);
		serve("*.html", "*.js").with(ConstantsGWTServlet.class);
	}
}
