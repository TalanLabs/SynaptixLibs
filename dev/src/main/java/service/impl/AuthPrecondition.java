package service.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.auth.helper.AuthsBundleHelper;
import com.synaptix.auth.helper.DefaultAndAuth;
import com.synaptix.auth.helper.DefaultAuth;
import com.synaptix.auth.helper.IAuth;

public class AuthPrecondition extends AbstractPrecondition {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Auths {

		public Auth[] value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Auth {

		public String value();

	}

	private final AuthsBundleManager authsBundleManager;

	private Map<Method, IAuth> map = new HashMap<Method, IAuth>();

	public AuthPrecondition(AuthsBundleManager authsBundleManager) {
		super();
		this.authsBundleManager = authsBundleManager;

	}

	@Override
	public boolean process(Method method, Object[] args) throws Throwable {
		IAuth auth = map.get(method);
		if (auth == null) {
			List<String> values = new ArrayList<String>();
			if (method.isAnnotationPresent(Auths.class)) {
				for (Auth v : method.getAnnotation(Auths.class).value()) {
					values.add(v.value());
				}
			} else if (method.isAnnotationPresent(Auth.class)) {
				values.add(method.getAnnotation(Auth.class).value());
			} else {
				throw new IllegalAccessException(
						"Missing Auths or Auth annotation");
			}

			List<IAuth> auths = new ArrayList<IAuth>();
			for (String value : values) {
				auths.add(new DefaultAuth(AuthsBundleHelper.toAuthMethod(value)));
			}
			auth = new DefaultAndAuth(auths.toArray(new IAuth[auths.size()]));
			map.put(method, auth);
		}
		return auth.hasAuth(authsBundleManager);
	}
}
