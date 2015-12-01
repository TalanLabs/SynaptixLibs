import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.auth.DefaultAuthsBundleManager;
import com.synaptix.client.service.ClientServiceFactory;
import com.synaptix.client.service.impl.DefaultServiceCommunicator;

import auth.MyAuthsBundle;
import service.IGabyService;
import service.ISandraService;
import service.impl.GabyServerService;
import service.impl.SandraServerService;

public class MainClientService {

	public static void main(String[] args) {
		DefaultAuthsBundleManager defaultAuthsBundleManager = (DefaultAuthsBundleManager) AuthsBundleManager.getInstance();
		defaultAuthsBundleManager.addBundle(MyAuthsBundle.class);

		defaultAuthsBundleManager.putAuthValue(MyAuthsBundle.class, "Test", "READ");

		DefaultServiceCommunicator defaultServiceCommunicator = new DefaultServiceCommunicator();
		defaultServiceCommunicator.addService("test", IGabyService.class, new GabyServerService());
		defaultServiceCommunicator.addService("test", ISandraService.class, new SandraServerService());

		ClientServiceFactory csf = new ClientServiceFactory(defaultServiceCommunicator, "test");

		System.out.println(csf.getService(ISandraService.class).laureline());
		System.out.println(csf.getService(ISandraService.class).whatYourName());
		System.out.println(csf.getService(IGabyService.class).whatYourName());
	}

}
