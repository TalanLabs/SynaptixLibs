package com.synaptix.server.vysper.guice;

import org.apache.vysper.xmpp.modules.Module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synaptix.server.vysper.xmpp.SynaptixXMPPServer;
import com.synaptix.server.vysper.xmpp.hack.SynaptixTCPEndpoint;
import com.synaptix.server.vysper.xmpp.modules.SynaptixAbonnementIQHandler;
import com.synaptix.server.vysper.xmpp.modules.SynaptixAbonnementModule;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServerVersionIQHandler;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServerVersionModule;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServiceFactoryIQHandler;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServiceFactoryModule;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServiceIQHandler;
import com.synaptix.server.vysper.xmpp.modules.SynaptixServiceModule;

public class SynaptixXMPPModule extends AbstractModule {

	private String serverDomain;

	private String serverVersion;

	public SynaptixXMPPModule(String serverDomain, String serverVersion) {
		super();

		this.serverDomain = serverDomain;
		this.serverVersion = serverVersion;
	}

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("serverDomain")).to(serverDomain);
		bindConstant().annotatedWith(Names.named("serverVersion")).to(serverVersion);

		bind(SynaptixTCPEndpoint.class).in(Singleton.class);

		bind(SynaptixXMPPServer.class).in(Singleton.class);

		Multibinder<Module> moduleMultibinder = Multibinder.newSetBinder(binder(), Module.class);
		bind(SynaptixAbonnementIQHandler.class).in(Singleton.class);
		moduleMultibinder.addBinding().to(SynaptixAbonnementModule.class).in(Singleton.class);
		bind(SynaptixServiceFactoryIQHandler.class).in(Singleton.class);
		moduleMultibinder.addBinding().to(SynaptixServiceFactoryModule.class).in(Singleton.class);
		bind(SynaptixServiceIQHandler.class).in(Singleton.class);
		moduleMultibinder.addBinding().to(SynaptixServiceModule.class).in(Singleton.class);
		bind(SynaptixServerVersionIQHandler.class).in(Singleton.class);
		moduleMultibinder.addBinding().to(SynaptixServerVersionModule.class).in(Singleton.class);
		// bind(PingIQHandler.class).in(Singleton.class);
		// moduleMultibinder.addBinding().to(PingModule.class).in(Singleton.class);
	}
}
