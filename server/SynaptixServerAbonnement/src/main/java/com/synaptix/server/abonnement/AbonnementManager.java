package com.synaptix.server.abonnement;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.synaptix.abonnement.AbonnementDescriptor;
import com.synaptix.abonnement.IAbonnement;

public final class AbonnementManager {

	private static String PLUGIN_FILE = "META-INF/abonnements/plugin.xml";

	private static final Log log = LogFactory.getLog(AbonnementManager.class);

	private static AbonnementManager instance;

	public static AbonnementManager getInstance() {
		if (instance == null) {
			instance = new AbonnementManager();
		}
		return instance;
	}

	private Map<String, AbonnementDescriptor> map;

	private AbonnementManager() {
		initialize();
	}

	private void initialize() {
		map = new HashMap<String, AbonnementDescriptor>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);

			Enumeration<URL> urls = AbonnementManager.class.getClassLoader()
					.getResources(PLUGIN_FILE);
			log.info("Search Abonnements...");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				log.info("Find " + url);

				String manifest = url.toExternalForm();
				if (manifest.endsWith(PLUGIN_FILE)) {

					URL manifestUrl = new URL(manifest);
					XmlPullParser xpp = factory.newPullParser();

					InputStream is = manifestUrl.openStream();
					xpp.setInput(is, "UTF-8");

					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							if ("abonnements".equalsIgnoreCase(xpp.getName())
									&& "http://synaptix.com/config/1.0"
											.equalsIgnoreCase(xpp
													.getNamespace())) {
								processAbonnements(xpp);
							}
						}
						eventType = xpp.next();
					}
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void processAbonnements(XmlPullParser xpp) throws Exception {
		boolean done = false;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("abonnement".equalsIgnoreCase(xpp.getName())) {
					processAbonnement(xpp);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("abonnements".equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}
	}

	private void processAbonnement(XmlPullParser xpp) throws Exception {
		boolean done = false;

		String name = null;
		String className = null;
		String description = null;

		while (!done) {
			int eventType = xpp.next();
			if (eventType == XmlPullParser.START_TAG) {
				if ("name".equalsIgnoreCase(xpp.getName())) {
					name = xpp.nextText();
				} else if ("className".equalsIgnoreCase(xpp.getName())) {
					className = xpp.nextText();
				} else if ("description".equalsIgnoreCase(xpp.getName())) {
					description = xpp.nextText();
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("abonnement".equalsIgnoreCase(xpp.getName())) {
					done = true;
				}
			}
		}

		if (name != null && className != null && !name.trim().isEmpty()) {
			try {
				ClassLoader classLoader = AbonnementManager.class
						.getClassLoader();
				Class<?> clazz = classLoader.loadClass(className.trim());

				if (IAbonnement.class.isAssignableFrom(clazz)) {
					log.info("Find abonnement " + name);
					IAbonnement<?> abonnement = (IAbonnement<?>) clazz
							.newInstance();

					AbonnementDescriptor desc = new AbonnementDescriptor(name
							.trim(), abonnement,
							description != null ? description.trim() : null);

					map.put(name.trim(), desc);
				} else {
					log.error(clazz.getName()
							+ " is not assignable from IAbonnement");
				}
			} catch (Exception e) {
				log.error(className + " not exist", e);
			}
		}
	}

	public AbonnementDescriptor getAbonnementDescriptor(String name)
			throws NotFoundAbonnementException {
		if (!map.containsKey(name)) {
			throw new NotFoundAbonnementException(name + " not exist");
		}
		return map.get(name);
	}

	public IAbonnement<?> getAbonnement(String name)
			throws NotFoundAbonnementException {
		if (!map.containsKey(name)) {
			throw new NotFoundAbonnementException(name + " not exist");
		}
		return map.get(name).getAbonnement();
	}

	public AbonnementDescriptor[] getAbonnementDescriptors() {
		Collection<AbonnementDescriptor> c = map.values();
		return c.toArray(new AbonnementDescriptor[c.size()]);
	}

	public String[] getAbonnementNames() {
		Collection<String> c = map.keySet();
		return c.toArray(new String[c.size()]);
	}
}
