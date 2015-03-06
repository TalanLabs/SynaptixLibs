import com.synaptix.component.factory.ComponentFactory;

public class MainTest {

	public static void main(String[] args) throws Exception {
		IGaby gaby = ComponentFactory.getInstance().createInstance(IGaby.class);

		// for (String p : gaby.straightGetPropertyNames()) {
		// System.out.println(p + " " + gaby.straightGetPropertyClass(p));
		// }

		System.out.println(gaby.toString());
		// gaby.notify();
	}

}
