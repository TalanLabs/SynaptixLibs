package auth;

import com.synaptix.auth.DefaultAuthsBundleManager;
import com.synaptix.auth.extension.IRootAuthExtension;
import com.synaptix.auth.extension.RootAuthExtensionProcessor;

public class MainAuthsBundle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DefaultAuthsBundleManager manager = new DefaultAuthsBundleManager();
		manager.addExtension(IConstraintAuthExtension.class, new ConstraintAuthExtensionProcessor());
		manager.addExtension(IRootAuthExtension.class, new RootAuthExtensionProcessor(true));
		manager.addBundle(MyAuthsBundle.class);

		manager.putAuthValue(MyAuthsBundle.class, "TEST", "READ");

		MyAuthsBundle authsBundle = manager.getBundle(MyAuthsBundle.class);

		System.out.println(authsBundle.hasReadTest());
		System.out.println(authsBundle.hasAuth("TEST", "READ"));
		System.out.println(authsBundle.hasWriteTest());
		System.out.println(authsBundle.isRoot());
		System.out.println(manager.getAuthInformations(MyAuthsBundle.class));

		authsBundle.getAuthConstraint("TEST3", "READ", "toto");
		authsBundle.getAuthConstraint("TEST3", "READ", "tata");
	}

}
