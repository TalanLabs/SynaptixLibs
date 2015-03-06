package i18n;

import i18n.test.SousConstantsBundle;
import i18n.test.TraceConstantsBundle;

import java.util.Locale;

import com.synaptix.constants.DefaultConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsLocaleSession;

public class MainConstantsBundle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DefaultConstantsBundleManager manager = new DefaultConstantsBundleManager(new DefaultConstantsLocaleSession(Locale.ENGLISH));
		manager.addBundle(SousConstantsBundle.class);
		manager.addBundle(MyConstantsBundle.class);
		manager.addBundle(LieuConstantsBundle.class);
		manager.addBundle(TraceConstantsBundle.class);

		System.out.println(manager.getConstantInformation(MyConstantsBundle.class.getName(), "map.key1"));

		MyConstantsBundle cb = manager.getBundle(MyConstantsBundle.class);

		System.out.println(cb.map().get("key1"));
		System.out.println(cb.map().get("existspas"));

		//
		// LieuConstantsBundle l = manager.getBundle(LieuConstantsBundle.class);

		// System.out.println(l.getcity());

		// System.out.println(cb.getString("lieu.city"));
		//
		// System.out.println(cb.lieu().trace().trace());
		// System.out.println(cb.getString("lieu.trace.trace"));
		//
		// System.out.println(cb.getString("lieu.test"));
		//
		// System.out.println(cb.getString("lieu.rien"));
		//
		// System.out.println(cb.getMap("map"));

		// ConstantInformation ci = manager.getConstantInformation(MyConstantsBundle.class.getName(), "cava");
		// System.out.println(ci);
	}
}
