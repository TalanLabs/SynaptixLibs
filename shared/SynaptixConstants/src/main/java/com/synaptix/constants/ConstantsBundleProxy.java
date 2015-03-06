package com.synaptix.constants;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.constants.shared.ConstantsBundle;
import com.synaptix.constants.shared.LocalizableResource;

/* package protected */class ConstantsBundleProxy implements InvocationHandler {

	private static Log LOG = LogFactory.getLog(ConstantsBundleProxy.class);

	protected final String bundleName;

	protected final Class<? extends ConstantsBundle> bundleClass;

	protected final DefaultConstantsBundleManager defaultConstantsBundleManager;

	protected final boolean inherits;

	protected Map<Method, String> keyCache = new HashMap<Method, String>();

	protected Map<Locale, Map<String, Map<String, String>>> stringCacheMap = new HashMap<Locale, Map<String, Map<String, String>>>();

	protected Map<Locale, Map<String, String[]>> stringArrayCache = new HashMap<Locale, Map<String, String[]>>();

	public ConstantsBundleProxy(String bundleName, Class<? extends ConstantsBundle> bundleClass, DefaultConstantsBundleManager defaultConstantsBundleManager, boolean inherits) {
		super();
		this.bundleName = bundleName;
		this.bundleClass = bundleClass;
		this.defaultConstantsBundleManager = defaultConstantsBundleManager;
		this.inherits = inherits;
	}

	/**
	 * Clear cache
	 */
	public void clearCache() {
		stringCacheMap.clear();
		stringArrayCache.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			LOG.debug("Call method=" + method);
			return method.invoke(this, args);
		}
		Object res = null;
		if (!inherits && method.getDeclaringClass() != bundleClass && method.getDeclaringClass() != ConstantsBundle.class && ConstantsBundle.class.isAssignableFrom(method.getDeclaringClass())) {
			ConstantsBundle otherClass = defaultConstantsBundleManager.getBundle((Class<? extends ConstantsBundle>) method.getDeclaringClass());
			res = method.invoke(otherClass, args);
		} else {
			ConstantsBundleMethod mbm = ConstantsBundleMethod.which(method);
			String key = getKey(method);
			LOG.debug("CALL method=" + mbm + " key=" + key + " from " + bundleClass.getName());
			switch (mbm) {
			case CALL_STRING:
				res = getString(key);
				break;
			case CALL_STRING_ARGS:
				res = getString(key, args);
				break;
			case CALL_BOOLEAN:
				res = getBoolean(key);
				break;
			case CALL_INT:
				res = getInt(key);
				break;
			case CALL_DOUBLE:
				res = getDouble(key);
				break;
			case CALL_FLOAT:
				res = getFloat(key);
				break;
			case CALL_STRINGARRAY:
				res = getStringArray(key);
				break;
			case CALL_MAP:
				res = getMap(key);
				break;
			case CALL_CONSTANTS_BUNDLE:
				res = defaultConstantsBundleManager.getBundle((Class<? extends ConstantsBundle>) method.getReturnType());
				break;
			case OTHER:
				res = null;
				break;
			}
		}
		if (res == null && method.getReturnType().isPrimitive() && !method.getReturnType().equals(Void.TYPE)) {
			throw new NullPointerException("Null result for " + method);
		}
		return res;
	}

	private String getKey(Method method) {
		String res = keyCache.get(method);
		if (res == null) {
			LocalizableResource.Key key = method.getAnnotation(LocalizableResource.Key.class);
			if (key != null) {
				res = key.value();
			} else {
				res = method.getName();
			}
			keyCache.put(method, res);
		}
		return res;
	}

	protected Properties getProperties() {
		return defaultConstantsBundleManager.getProperties(defaultConstantsBundleManager.getConstantsLocaleSession().getLocale(), bundleName);
	}

	protected String getString(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return new StringBuilder().append("!").append(key).append("!").toString();
		}
		return properties.getProperty(key);
	}

	protected String getString(String key, Object... args) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return new StringBuilder().append("!").append(key).append("!").toString();
		}
		return MessageFormat.format(properties.getProperty(key), args);
	}

	protected Boolean getBoolean(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		String value = properties.getProperty(key);
		return value != null ? Boolean.parseBoolean(value) : null;
	}

	protected Integer getInt(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		String value = properties.getProperty(key);
		return value != null ? Integer.parseInt(value) : null;
	}

	protected Double getDouble(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		String value = properties.getProperty(key);
		return value != null ? Double.parseDouble(value) : null;
	}

	protected Float getFloat(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		String value = properties.getProperty(key);
		return value != null ? Float.parseFloat(value) : null;
	}

	protected String[] getStringArray(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		Locale locale = defaultConstantsBundleManager.getConstantsLocaleSession().getLocale();
		synchronized (stringCacheMap) {
			Map<String, String[]> cacheMap = stringArrayCache.get(locale);
			if (cacheMap == null) {
				cacheMap = new HashMap<String, String[]>();
				stringArrayCache.put(locale, cacheMap);
			}
			String[] res = cacheMap.get(key);
			if (res == null) {
				String value = properties.getProperty(key);
				if (value != null) {
					String[] rs = value.split("(?<=[^\\\\\\\\]{2}),|(?<=^.),");
					res = new String[rs.length];
					for (int i = 0; i < rs.length; i++) {
						res[i] = rs[i].replace("\\,", ",");
					}
				}
				cacheMap.put(key, res);
			}
			return res;
		}
	}

	protected Map<String, String> getMap(String key) {
		Properties properties = getProperties();
		if (properties == null || !properties.containsKey(key)) {
			return null;
		}
		Locale locale = defaultConstantsBundleManager.getConstantsLocaleSession().getLocale();
		synchronized (stringCacheMap) {
			Map<String, Map<String, String>> cacheMap = stringCacheMap.get(locale);
			if (cacheMap == null) {
				cacheMap = new HashMap<String, Map<String, String>>();
				stringCacheMap.put(locale, cacheMap);
			}
			Map<String, String> res = cacheMap.get(key);
			if (res == null) {
				String map = properties.getProperty(key);
				if (map != null) {
					res = new ConstantsBundleHashMap();
					String[] ks = map.split("(?<=[^\\\\\\\\]{2}),|(?<=^.),");
					if (ks != null) {
						for (String k : ks) {
							k = k.trim().replace("\\,", ",");
							res.put(k, properties.getProperty(new StringBuilder(key).append(".").append(k).toString()));
						}
					}
				}
				cacheMap.put(key, res);
			}
			return res;
		}
	}

	public static class ConstantsBundleHashMap extends HashMap<String, String> {

		private static final long serialVersionUID = 871007895020939844L;

		@Override
		public String get(Object key) {
			String res = super.get(key);
			if (res == null) {
				return new StringBuilder().append("!").append(key).append("!").toString();
			}
			return res;
		}

	}
}
