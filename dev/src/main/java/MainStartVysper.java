import org.apache.vysper.storage.OpenStorageProviderRegistry;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.authorization.UserAuthorization;
import org.apache.vysper.xmpp.modules.roster.persistence.MemoryRosterManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.server.vysper.guice.SynaptixXMPPModule;
import com.synaptix.server.vysper.xmpp.EmptyOfflineStorageProvider;
import com.synaptix.server.vysper.xmpp.SynaptixXMPPServer;

public class MainStartVysper {

	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new SynaptixXMPPModule("gaby.com", "1.0"));

		SynaptixXMPPServer synaptixXMPPServer = injector.getInstance(SynaptixXMPPServer.class);

		StorageProviderRegistry providerRegistry = new OpenStorageProviderRegistry();
		providerRegistry.add(new MemoryRosterManager());
		providerRegistry.add(new MyUserAuthorization());
		providerRegistry.add(new EmptyOfflineStorageProvider());

		synaptixXMPPServer.setTLSCertificateInfo(MainStartVysper.class.getResourceAsStream("/keystore.jks"), "opti123");
		synaptixXMPPServer.setStorageProviderRegistry(providerRegistry);

		synaptixXMPPServer.start();
	}

	private static final class MyUserAuthorization implements UserAuthorization {

		@Override
		public boolean verifyCredentials(String username, String passwordCleartext, Object credentials) {
			return true;
		}

		@Override
		public boolean verifyCredentials(Entity jid, String passwordCleartext, Object credentials) {
			return true;
		}
	}

}
