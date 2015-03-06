package com.synaptix.auth;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.auth.extension.IWithLookupExtension;
import com.synaptix.auth.extension.WithLookupExtensionProcessor;
import com.synaptix.auth.method.IAuthMethod;

/**
 * Default implementation AuthsBundleManager
 * 
 * @author Gaby
 * 
 */
public class DefaultAuthsBundleManager extends AuthsBundleManager {

	private static Log LOG = LogFactory.getLog(DefaultAuthsBundleManager.class);

	private Map<Class<? extends AuthsBundle>, AuthsBundle> instanceMap = new HashMap<Class<? extends AuthsBundle>, AuthsBundle>();

	private Map<Class<? extends AuthsBundle>, AuthsBundleProxy> proxyMap = new HashMap<Class<? extends AuthsBundle>, AuthsBundleProxy>();

	private Map<Class<? extends AuthsBundle>, Map<ObjectActionCouple, AuthInformation>> authInformationMap = new HashMap<Class<? extends AuthsBundle>, Map<ObjectActionCouple, AuthInformation>>();

	private Map<Class<?>, IAuthExtensionProcessor> authExtensionMap = new HashMap<Class<?>, IAuthExtensionProcessor>();

	protected Map<Class<? extends AuthsBundle>, List<ObjectActionCouple>> valueMap = new HashMap<Class<? extends AuthsBundle>, List<ObjectActionCouple>>();

	public DefaultAuthsBundleManager() {
		super();

		this.addExtension(IWithLookupExtension.class,
				new WithLookupExtensionProcessor());
	}

	/**
	 * Add authMethod value
	 * 
	 * @param authMethod
	 */
	public void putAuthValue(IAuthMethod authMethod) {
		if (authMethod == null) {
			throw new NullArgumentException("authMethod is null");
		}
		putAuthValue(authMethod.getBundleClass(), authMethod.getObject(),
				authMethod.getAction());
	}

	/**
	 * Add object action value
	 * 
	 * @param bundleClass
	 * @param object
	 * @param action
	 */
	public void putAuthValue(Class<? extends AuthsBundle> bundleClass,
			String object, String action) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		if (object == null) {
			throw new NullArgumentException("object is null");
		}
		if (action == null) {
			throw new NullArgumentException("action is null");
		}
		checkExistBundleName(bundleClass);

		if (getAuthInformation(bundleClass, object, action) == null) {
			LOG.warn("For " + bundleClass + " bundle class not exists object="
					+ object + " action=" + action);
		} else {
			List<ObjectActionCouple> map = valueMap.get(bundleClass);
			if (map == null) {
				map = new ArrayList<ObjectActionCouple>();
				valueMap.put(bundleClass, map);
			}
			map.add(new ObjectActionCouple(object, action));
		}
	}

	/**
	 * Clear all auth values for bundle class
	 * 
	 * @param bundleClass
	 */
	public void clearAuthValues(Class<? extends AuthsBundle> bundleClass) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		checkExistBundleName(bundleClass);

		valueMap.remove(bundleClass);
	}

	/**
	 * Clear all auth values
	 */
	public void clearAllAuthValues() {
		valueMap.clear();
	}

	@Override
	public boolean hasAuth(Class<? extends AuthsBundle> bundleClass,
			String object, String action) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		checkExistBundleName(bundleClass);
		if (getAuthInformation(bundleClass, object, action) == null) {
			LOG.warn("Not exists object=" + object + " action=" + action);
			return false;
		}
		List<ObjectActionCouple> map = valueMap.get(bundleClass);
		return map != null
				&& map.contains(new ObjectActionCouple(object, action));
	}

	@Override
	public void addExtension(Class<?> authExtensionClass,
			IAuthExtensionProcessor authExtensionProcessor) {
		if (authExtensionClass == null) {
			throw new NullArgumentException("authExtensionClass is null");
		}
		if (authExtensionProcessor == null) {
			throw new NullArgumentException("authExtensionProcessor is null");
		}
		if (!authExtensionClass.isInterface()) {
			throw new IllegalArgumentException(authExtensionClass
					+ "must be a interface class");
		}
		if (!authExtensionMap.containsKey(authExtensionClass)) {
			authExtensionMap.put(authExtensionClass, authExtensionProcessor);
		} else {
			LOG.warn(authExtensionClass + " already containts");
		}
	}

	@Override
	public IAuthExtensionProcessor getExtension(Class<?> authExtensionClass) {
		if (authExtensionClass == null) {
			throw new NullArgumentException("authExtensionClass is null");
		}
		return authExtensionMap.get(authExtensionClass);
	}

	@Override
	public List<Class<?>> getAllAuthExtensionClasss() {
		return Collections.unmodifiableList(new ArrayList<Class<?>>(
				authExtensionMap.keySet()));
	}

	@Override
	public void addBundle(Class<? extends AuthsBundle> bundleClass) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		if (!bundleClass.isInterface()) {
			throw new IllegalArgumentException(bundleClass
					+ "must be a interface class");
		}

		LOG.info("Add bundle " + bundleClass);

		loadAnnotations(bundleClass);

		ClassLoader classLoader = bundleClass.getClassLoader();
		Class<?>[] interfaces = new Class[] { bundleClass };
		AuthsBundleProxy proxy = new AuthsBundleProxy(this, bundleClass);
		proxyMap.put(bundleClass, proxy);
		AuthsBundle e = (AuthsBundle) Proxy.newProxyInstance(classLoader,
				interfaces, proxy);
		instanceMap.put(bundleClass, e);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E extends AuthsBundle> E getBundle(Class<E> bundleClass) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		checkExistBundleName(bundleClass);

		return (E) instanceMap.get(bundleClass);
	}

	@Override
	public List<Class<? extends AuthsBundle>> getAllBundleClasss() {
		return Collections
				.unmodifiableList(new ArrayList<Class<? extends AuthsBundle>>(
						instanceMap.keySet()));
	}

	private void checkExistBundleName(Class<? extends AuthsBundle> bundleClass) {
		if (!instanceMap.containsKey(bundleClass)) {
			throw new IllegalArgumentException(bundleClass
					+ " not a bundle class");
		}
	}

	/**
	 * Get a information for auth, description, meaning
	 * 
	 * @param bundleClass
	 * @param object
	 * @param action
	 * @return
	 */
	public AuthInformation getAuthInformation(
			Class<? extends AuthsBundle> bundleClass, String object,
			String action) {
		checkExistBundleName(bundleClass);

		AuthInformation res = null;
		Map<ObjectActionCouple, AuthInformation> map = authInformationMap
				.get(bundleClass);
		if (map != null) {
			res = map.get(new ObjectActionCouple(object, action));
		}
		return res;
	}

	/**
	 * Get all information for bundle name
	 * 
	 * @param bundleClass
	 * @return
	 */
	public List<AuthInformation> getAuthInformations(
			Class<? extends AuthsBundle> bundleClass) {
		checkExistBundleName(bundleClass);

		List<AuthInformation> res = null;
		Map<ObjectActionCouple, AuthInformation> map = authInformationMap
				.get(bundleClass);
		if (map != null) {
			res = Collections.unmodifiableList(new ArrayList<AuthInformation>(
					map.values()));
		}
		return res;
	}

	private void loadAnnotations(Class<? extends AuthsBundle> bundleClass) {
		for (Method method : bundleClass.getMethods()) {
			if (method.getDeclaringClass() != Object.class) {
				AuthsBundleMethod abm = AuthsBundleMethod.which(method);
				if (abm != null && AuthsBundleMethod.CALL_BOOLEAN.equals(abm)) {
					AuthsBundle.Key key = method
							.getAnnotation(AuthsBundle.Key.class);
					AuthInformation ci = new AuthInformation(method,
							bundleClass, key.object(), key.action(),
							loadDescriptionAnnonation(method));

					Map<ObjectActionCouple, AuthInformation> map = authInformationMap
							.get(bundleClass);
					if (map == null) {
						map = new HashMap<ObjectActionCouple, AuthInformation>();
						authInformationMap.put(bundleClass, map);
					}
					ObjectActionCouple objectActionCouple = new ObjectActionCouple(
							key.object(), key.action());
					if (map.containsKey(objectActionCouple)) {
						LOG.warn(bundleClass + " exists object=" + key.object()
								+ " and action=" + key.action());
					} else {
						map.put(objectActionCouple, ci);

						for (IAuthExtensionProcessor authExtensionProcessor : authExtensionMap
								.values()) {
							authExtensionProcessor.addAuthInformation(this,
									bundleClass, ci);
						}
					}
				}
			}
		}
	}

	private String loadDescriptionAnnonation(Method method) {
		AuthsBundle.Description defaultValue = method
				.getAnnotation(AuthsBundle.Description.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return null;
	}

	protected final static class ObjectActionCouple {

		final String object;

		final String action;

		ObjectActionCouple(String object, String action) {
			super();
			this.object = object;
			this.action = action;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(object).append(action)
					.toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectActionCouple) {
				ObjectActionCouple o = (ObjectActionCouple) obj;
				return new EqualsBuilder().append(object, o.object)
						.append(action, o.action).isEquals();
			}
			return super.equals(obj);
		}
	}
}
