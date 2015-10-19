package com.synaptix.constants;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.constants.shared.ConstantsBundle;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;
import com.synaptix.constants.shared.LocalizableResource;

/**
 * Default implementation ConstantsBundleManage
 *
 * @author Gaby
 *
 */
public class DefaultConstantsBundleManager extends ConstantsBundleManager {

	private static Log LOG = LogFactory.getLog(DefaultConstantsBundleManager.class);

	private final IConstantsLocaleSession constantsLocaleSession;

	private Map<String, String> bundleNameMap = new HashMap<String, String>();

	private Map<String, ConstantsBundle> instanceMap = new HashMap<String, ConstantsBundle>();

	private Map<String, ConstantsBundleProxy> proxyMap = new HashMap<String, ConstantsBundleProxy>();

	private Map<String, Properties> defaultMap = new HashMap<String, Properties>();

	private Map<String, Map<String, ConstantInformation>> constantInformationMap = new HashMap<String, Map<String, ConstantInformation>>();

	private Map<Locale, Map<String, Properties>> localeCacheMap = new HashMap<Locale, Map<String, Properties>>();

	public DefaultConstantsBundleManager() {
		super();

		this.constantsLocaleSession = createConstantsLocaleSession();
	}

	public DefaultConstantsBundleManager(IConstantsLocaleSession constantsLocaleSession) {
		super();

		this.constantsLocaleSession = constantsLocaleSession;
	}

	protected IConstantsLocaleSession createConstantsLocaleSession() {
		return new DefaultConstantsLocaleSession();
	}

	/**
	 * Get a current constants locale session
	 *
	 * @return
	 */
	public IConstantsLocaleSession getConstantsLocaleSession() {
		return constantsLocaleSession;
	}

	@Override
	public void addBundle(String bundleName, String baseName) {
		if (bundleName == null) {
			throw new NullArgumentException("bundleName is null");
		}
		if (baseName == null) {
			throw new NullArgumentException("baseName is null");
		}

		if (!bundleNameMap.containsKey(bundleName)) {
			LOG.info("Add bundle " + bundleName);
			addBundle(bundleName, baseName, ConstantsWithLookingBundle.class, true);
		} else {
			LOG.warn("Bundle " + bundleName + " already exists");
		}
	}

	@Override
	public <E extends ConstantsBundle> void addBundle(Class<E> bundleClass) {
		addBundle(bundleClass, false);
	}

	private <E extends ConstantsBundle> void addBundle(Class<E> bundleClass, boolean inherits) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		if (!bundleClass.isInterface()) {
			throw new IllegalArgumentException(bundleClass + " must be a interface class");
		}

		if (!bundleNameMap.containsKey(bundleClass.getName())) {
			LOG.info("Add bundle " + bundleClass.getName());
			addBundle(bundleClass.getName(), getBaseName(bundleClass), bundleClass, inherits);
		} else {
			LOG.warn("Bundle " + bundleClass.getName() + " already exists");
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends ConstantsBundle> void addBundle(String bundleName, String baseName, Class<E> bundleClass, boolean inherits) {
		_addBundle(bundleName, baseName, bundleClass, inherits);
		if (!inherits) {
			for (Class<?> inter : bundleClass.getInterfaces()) {
				if (inter != ConstantsWithLookingBundle.class && inter != ConstantsBundle.class) {
					if (ConstantsBundle.class.isAssignableFrom(inter)) {
						addBundle((Class<? extends ConstantsBundle>) inter, false);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends ConstantsBundle> void _addBundle(String bundleName, String baseName, Class<E> bundleClass, boolean inherits) {
		bundleNameMap.put(bundleName, baseName);
		defaultMap.put(bundleName, loadAnnotations(bundleName, bundleClass, inherits));

		ClassLoader classLoader = bundleClass.getClassLoader();
		Class<?>[] interfaces = new Class[] { bundleClass };
		ConstantsBundleProxy proxy = null;
		if (ConstantsWithLookingBundle.class.isAssignableFrom(bundleClass)) {
			proxy = new ConstantsWithLookingBundleProxy(bundleName, bundleClass, this, inherits);
		} else {
			proxy = new ConstantsBundleProxy(bundleName, bundleClass, this, inherits);
		}
		proxyMap.put(bundleName, proxy);
		E e = (E) Proxy.newProxyInstance(classLoader, interfaces, proxy);
		instanceMap.put(bundleName, e);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends ConstantsBundle> E getBundle(Class<E> bundleClass) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		checkExistBundleName(bundleClass.getName());

		return (E) instanceMap.get(bundleClass.getName());
	}

	@Override
	public ConstantsBundle getBundle(String bundleName) {
		if (bundleName == null) {
			throw new NullArgumentException("bundleName is null");
		}
		checkExistBundleName(bundleName);

		return instanceMap.get(bundleName);
	}

	private void checkExistBundleName(String bundleName) {
		if (!bundleNameMap.containsKey(bundleName)) {
			throw new IllegalArgumentException(bundleName + " not a bundle name");
		}
	}

	private String getBaseName(Class<? extends ConstantsBundle> messagesClass) {
		return messagesClass.getName().replace('.', '/');
	}

	@Override
	public List<String> getAllBundleNames() {
		return Collections.unmodifiableList(new ArrayList<String>(instanceMap.keySet()));
	}

	/**
	 * Get a constants bundle proxy
	 *
	 * @param bundleName
	 * @return
	 */
	public ConstantsBundleProxy getConstantsBundleProxy(String bundleName) {
		return proxyMap.get(bundleName);
	}

	/**
	 * Load a local properties, annotation
	 *
	 * @param locale
	 * @param bundleClass
	 * @return
	 */
	public <E extends ConstantsBundle> Properties getProperties(Locale locale, Class<E> bundleClass) {
		if (locale == null || bundleClass == null) {
			return null;
		}
		return getProperties(locale, bundleClass.getName());
	}

	/**
	 * Load a properties
	 *
	 * @param bundleName
	 * @param locale
	 * @return
	 */
	public Properties getProperties(Locale locale, String bundleName) {
		if (locale == null || bundleName == null) {
			return null;
		}
		checkExistBundleName(bundleName);
		synchronized (localeCacheMap) {
			Map<String, Properties> propertiesMap = localeCacheMap.get(locale);
			if (propertiesMap == null) {
				propertiesMap = new HashMap<String, Properties>();
				localeCacheMap.put(locale, propertiesMap);
			}
			Properties p = propertiesMap.get(bundleName);
			if (p == null) {
				Properties defaultP = defaultMap.get(bundleName);
				p = new Properties();
				if (defaultP != null) {
					p.putAll(defaultP);
				}
				p.putAll(loadBaseName(locale, bundleName, bundleNameMap.get(bundleName)));
				propertiesMap.put(bundleName, p);
			}
			return p;
		}
	}

	/**
	 * Clear locale cache
	 */
	public void clearCache() {
		synchronized (localeCacheMap) {
			localeCacheMap.clear();

			for (ConstantsBundleProxy constantsBundleProxy : proxyMap.values()) {
				constantsBundleProxy.clearCache();
			}
		}
	}

	/**
	 * Get a information for constant, description, meaning
	 *
	 * @param bundleName
	 * @param key
	 * @return
	 */
	public ConstantInformation getConstantInformation(String bundleName, String key) {
		checkExistBundleName(bundleName);

		ConstantInformation res = null;
		Map<String, ConstantInformation> map = constantInformationMap.get(bundleName);
		if (map != null) {
			res = map.get(key);
		}
		return res;
	}

	/**
	 * Get all information for bundle name
	 *
	 * @param bundleName
	 * @return
	 */
	public List<ConstantInformation> getConstantInformations(String bundleName) {
		checkExistBundleName(bundleName);

		List<ConstantInformation> res = null;
		Map<String, ConstantInformation> map = constantInformationMap.get(bundleName);
		if (map != null) {
			res = Collections.unmodifiableList(new ArrayList<ConstantInformation>(map.values()));
		}
		return res;
	}

	private Properties loadBaseName(Locale locale, String bundleName, String baseName) {
		Properties p = new Properties();
		try {
			LOG.debug("Loading " + bundleName + " " + baseName + " " + locale);
			// Ignore fallback, fallback use default locale
			ResourceBundle r = ResourceBundle.getBundle(baseName, locale, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
			if (r.getLocale().equals(locale) || r.getLocale().getISO3Language().equals(locale.getISO3Language()) || r.getLocale().equals(Locale.ROOT)) {
				for (String key : r.keySet()) {
					p.put(key, r.getString(key));
				}
			}
		} catch (Exception e) {
			// Ignore, file not exist
		}
		return p;
	}

	private Properties loadAnnotations(String bundleName, Class<? extends ConstantsBundle> bundleClass, boolean inherits) {
		DefaultDescription defaultAnnonation = bundleClass.getAnnotation(DefaultDescription.class);
		String defaultDescription = defaultAnnonation != null ? defaultAnnonation.value() : null;
		Properties p = new Properties();
		for (Method method : inherits ? bundleClass.getMethods() : bundleClass.getDeclaredMethods()) {
			if (method.getDeclaringClass() != Object.class) {
				String key = getKey(method);

				ConstantsBundleMethod bm = null;
				if (ConstantsWithLookingBundle.class.isAssignableFrom(bundleClass)) {
					ConstantsWithLookingBundleMethod cwlbm = ConstantsWithLookingBundleMethod.which(method);
					if (ConstantsWithLookingBundleMethod.OTHER.equals(cwlbm)) {
						bm = ConstantsBundleMethod.which(method);
					}
				} else {
					bm = ConstantsBundleMethod.which(method);
				}
				if (bm != null && !ConstantsBundleMethod.OTHER.equals(bm)) {
					ConstantInformation ci = new ConstantInformation(bundleName, key, loadDescriptionAnnonation(method, defaultDescription), loadMeaningAnnonation(method));
					Map<String, ConstantInformation> map = constantInformationMap.get(bundleName);
					if (map == null) {
						map = new HashMap<String, ConstantInformation>();
						constantInformationMap.put(bundleName, map);
					}
					map.put(key, ci);

					switch (bm) {
					case CALL_STRING:
						loadDefaultStringValueAnnonation(method, p, key);
						break;
					case CALL_STRING_ARGS:
						loadDefaultStringValueAnnonation(method, p, key);
						break;
					case CALL_BOOLEAN:
						loadDefaultBooleanValueAnnonation(method, p, key);
						break;
					case CALL_DOUBLE:
						loadDefaultDoubleValueAnnonation(method, p, key);
						break;
					case CALL_FLOAT:
						loadDefaultFloatValueAnnonation(method, p, key);
						break;
					case CALL_INT:
						loadDefaultIntValueAnnonation(method, p, key);
						break;
					case CALL_MAP:
						loadDefaultStringMapValueAnnonation(method, p, key);
						break;
					case CALL_STRINGARRAY:
						loadDefaultStringArrayValueAnnonation(method, p, key);
						break;
					case CALL_CONSTANTS_BUNDLE:
						break;
					case OTHER:
						break;
					}
				}
			}

		}
		return p;
	}

	private String getKey(Method method) {
		LocalizableResource.Key key = method.getAnnotation(LocalizableResource.Key.class);
		if (key != null) {
			return key.value();
		}
		return method.getName();
	}

	private String loadDescriptionAnnonation(Method method, String defaultValue) {
		ConstantsBundle.Description value = method.getAnnotation(ConstantsBundle.Description.class);
		return value != null ? value.value() : defaultValue;
	}

	private String loadMeaningAnnonation(Method method) {
		ConstantsBundle.Meaning value = method.getAnnotation(ConstantsBundle.Meaning.class);
		return value != null ? value.value() : null;
	}

	private void loadDefaultStringValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultStringValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultStringValue.class);
		if (defaultValue != null) {
			if (p.containsKey(key)) {
				LOG.error("Key " + key + " already exist for method " + method.toString());
			} else {
				p.setProperty(key, defaultValue.value());
			}
		}
	}

	private void loadDefaultBooleanValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultBooleanValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultBooleanValue.class);
		if (defaultValue != null) {
			if (p.containsKey(key)) {
				LOG.warn("Key " + key + " already exist for method " + method.toString());
			} else {
				p.setProperty(key, Boolean.toString(defaultValue.value()));
			}
		}
	}

	private void loadDefaultIntValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultIntValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultIntValue.class);
		if (defaultValue != null) {
			if (p.containsKey(key)) {
				LOG.warn("Key " + key + " already exist for method " + method.toString());
			} else {
				p.setProperty(key, Integer.toString(defaultValue.value()));
			}
		}
	}

	private void loadDefaultFloatValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultFloatValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultFloatValue.class);
		if (defaultValue != null) {
			if (p.containsKey(key)) {
				LOG.warn("Key " + key + " already exist for method " + method.toString());
			} else {
				p.setProperty(key, Float.toString(defaultValue.value()));
			}
		}
	}

	private void loadDefaultDoubleValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultDoubleValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultDoubleValue.class);
		if (defaultValue != null) {
			if (p.containsKey(key)) {
				LOG.error("Key " + key + " already exist for method " + method.toString());
			} else {
				p.setProperty(key, Double.toString(defaultValue.value()));
			}
		}
	}

	private void loadDefaultStringArrayValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultStringArrayValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultStringArrayValue.class);
		if (defaultValue != null) {
			String[] rs = defaultValue.value();
			if (rs != null) {
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (String r : rs) {
					if (first) {
						first = false;
					} else {
						sb.append(",");
					}
					sb.append(r.replace(",", "\\,"));
				}
				if (p.containsKey(key)) {
					LOG.warn("Key " + key + " already exist for method " + method.toString());
				} else {
					p.setProperty(key, sb.toString());
				}
			}
		}
	}

	private void loadDefaultStringMapValueAnnonation(Method method, Properties p, String key) {
		ConstantsBundle.DefaultStringMapValue defaultValue = method.getAnnotation(ConstantsBundle.DefaultStringMapValue.class);
		if (defaultValue != null) {
			String[] map = defaultValue.value();
			if (map != null && map.length % 2 == 0) {
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (int i = 0; i < map.length; i += 2) {
					String k = map[i];
					String finalKey = new StringBuilder(key).append(".").append(k).toString();
					String v = map[i + 1];
					if (p.containsKey(finalKey)) {
						LOG.warn("Key " + finalKey + " already exist for method " + method.toString());
					} else {
						p.setProperty(finalKey, v);
					}
					if (first) {
						first = false;
					} else {
						sb.append(",");
					}
					sb.append(k);
				}
				if (p.containsKey(key)) {
					LOG.warn("Key " + key + " already exist for method " + method.toString());
				} else {
					p.setProperty(key, sb.toString());
				}
			}
		}
	}
}
