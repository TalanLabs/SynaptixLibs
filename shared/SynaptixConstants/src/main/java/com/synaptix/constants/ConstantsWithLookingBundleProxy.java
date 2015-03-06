package com.synaptix.constants;

import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.constants.shared.ConstantsBundle;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;

/* package protected */class ConstantsWithLookingBundleProxy extends ConstantsBundleProxy {

	private static Log LOG = LogFactory.getLog(ConstantsWithLookingBundleProxy.class);

	public ConstantsWithLookingBundleProxy(String bundleName, Class<? extends ConstantsBundle> bundleClass, DefaultConstantsBundleManager defaultCconstantsBundleManager, boolean inherits) {
		super(bundleName, bundleClass, defaultCconstantsBundleManager, inherits);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == Object.class) {
			LOG.debug("Call method=" + method);
			return method.invoke(this, args);
		}
		Object res = null;
		ConstantsWithLookingBundleMethod mbm = ConstantsWithLookingBundleMethod.which(method);
		if (ConstantsWithLookingBundleMethod.OTHER.equals(mbm)) {
			res = super.invoke(proxy, method, args);
		} else {
			String key = (String) args[0];
			res = getResult(mbm, key, args);
			if (res == null) {
				res = _getResult(mbm, key, args);
			}
		}
		return res;
	}

	public Object getResult(ConstantsWithLookingBundleMethod mbm, String key, Object[] args) {
		Properties properties = getProperties();
		Object res = null;
		LOG.debug("CALL method=" + mbm + " key=" + key + " from " + bundleClass.getName());
		if (properties != null) {
			if (properties.containsKey(key)) {
				res = _getResult(mbm, key, args);
			} else {
				if (!inherits) {
					Class<?>[] inters = bundleClass.getInterfaces();
					int i = 0;
					while (i < inters.length && res == null) {
						Class<?> inter = inters[i];
						if (inter != ConstantsWithLookingBundle.class && inter != ConstantsBundle.class) {
							if (ConstantsWithLookingBundle.class.isAssignableFrom(inter)) {
								ConstantsWithLookingBundleProxy proxy = (ConstantsWithLookingBundleProxy) defaultConstantsBundleManager.getConstantsBundleProxy(inter.getName());
								if (proxy != null) {
									res = proxy.getResult(mbm, key, args);
								}
							}
						}
						i++;
					}
				}
				if (res == null && key.contains(".")) {
					String first = key.substring(0, key.indexOf("."));
					try {
						Method method = bundleClass.getMethod(first);
						if (method != null && ConstantsWithLookingBundle.class.isAssignableFrom(method.getReturnType())) {
							ConstantsWithLookingBundleProxy p = (ConstantsWithLookingBundleProxy) defaultConstantsBundleManager.getConstantsBundleProxy(method.getReturnType().getName());
							if (p != null) {
								res = p.getResult(mbm, key.substring(first.length() + 1), args);
							}
						}
					} catch (SecurityException e) {
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}
		return res;
	}

	private Object _getResult(ConstantsWithLookingBundleMethod mbm, String key, Object[] args) {
		Object res = null;
		switch (mbm) {
		case GET_STRING:
			res = getString(key);
			break;
		case GET_STRING_ARGS:
			res = getString(key, (Object[]) args[1]);
			break;
		case GET_BOOLEAN:
			res = getBoolean(key);
			break;
		case GET_INT:
			res = getInt(key);
			break;
		case GET_DOUBLE:
			res = getDouble(key);
			break;
		case GET_FLOAT:
			res = getFloat(key);
			break;
		case GET_STRING_ARRAY:
			res = getStringArray(key);
			break;
		case GET_MAP:
			res = getMap(key);
			break;
		case OTHER:
			res = null;
			break;
		}
		return res;
	}
}
