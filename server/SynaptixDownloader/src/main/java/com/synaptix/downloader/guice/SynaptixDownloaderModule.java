package com.synaptix.downloader.guice;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import com.synaptix.downloader.IDownloaderRegistry;
import com.synaptix.downloader.impl.DefaultDownloaderRegistry;
import com.synaptix.downloader.servlet.DownloaderServlet;

public class SynaptixDownloaderModule extends ServletModule {

	private final String downloaderServletPath;

	public SynaptixDownloaderModule(String downloaderServletPath) {
		super();
		this.downloaderServletPath = downloaderServletPath;
	}

	@Override
	protected void configureServlets() {
		bindConstant().annotatedWith(Names.named("DownloaderServletPath")).to(downloaderServletPath);
		bind(DefaultDownloaderRegistry.class);
		bind(IDownloaderRegistry.class).to(DefaultDownloaderRegistry.class).in(Singleton.class);
		bind(DownloaderServlet.class).in(Singleton.class);
		serve(downloaderServletPath).with(DownloaderServlet.class);
	}
}
