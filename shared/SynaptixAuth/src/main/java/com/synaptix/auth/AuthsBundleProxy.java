package com.synaptix.auth;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AuthsBundleProxy implements InvocationHandler {

	private static Log LOG = LogFactory.getLog(AuthsBundleProxy.class);

	private Class<? extends AuthsBundle> authsBundleClass;

	private AuthsBundleManager defaultAuthsBundleManager;

	public AuthsBundleProxy(AuthsBundleManager defaultAuthsBundleManager, Class<? extends AuthsBundle> authsBundleClass) {
		super();

		this.defaultAuthsBundleManager = defaultAuthsBundleManager;
		this.authsBundleClass = authsBundleClass;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> declaringClass = method.getDeclaringClass();
		LOG.debug("Call method=" + method);
		if (declaringClass == Object.class) {
			return method.invoke(this, args);
		}
		Object res = null;
		IAuthExtensionProcessor authExtensionProcessor = defaultAuthsBundleManager.getExtension(declaringClass);
		if (authExtensionProcessor != null) {
			LOG.debug("Call authExtensionProcessor " + authExtensionProcessor);
			res = authExtensionProcessor.process(defaultAuthsBundleManager, authsBundleClass, declaringClass, method, args);
		} else {
			AuthsBundleMethod abm = AuthsBundleMethod.which(method);
			LOG.debug("CALL AuthsBundle method=" + abm);
			switch (abm) {
			case CALL_BOOLEAN:
				res = callBoolean(method);
				break;
			case OTHER:
				break;
			}
		}
		if (res == null && method.getReturnType().isPrimitive() && !method.getReturnType().equals(Void.TYPE)) {
			throw new NullPointerException("Null result for " + method);
		}
		return res;
	}

	private boolean hasAuth(String object, String action) {
		return defaultAuthsBundleManager.hasAuth(authsBundleClass, object, action);
	}

	private boolean callBoolean(Method method) {
		AuthsBundle.Key key = method.getAnnotation(AuthsBundle.Key.class);
		return hasAuth(key.object(), key.action());
	}
}
