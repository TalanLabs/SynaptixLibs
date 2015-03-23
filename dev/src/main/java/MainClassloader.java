import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

public class MainClassloader {

	public static void main(String[] args) throws Exception {
		URLClassLoader cl = new URLClassLoader(new URL[] { new File("D:/gefmap-core-1.10-SNAPSHOT.jar").toURI().toURL() });

		Field f = ClassLoader.class.getDeclaredField("classes");
		f.setAccessible(true);

		Vector<Class> classes = (Vector<Class>) f.get(cl);
		System.out.println(classes);
		//
		// Reflections ref = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner()).addClassLoader(cl).setUrls(new File("D:/gefmap-core-1.10-SNAPSHOT.jar").toURI().toURL()));
		//
		// System.out.println(ref.getSubTypesOf(Externalizable.class));
	}

}
