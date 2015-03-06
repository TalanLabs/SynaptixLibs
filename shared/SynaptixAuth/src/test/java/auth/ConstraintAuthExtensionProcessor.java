package auth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.auth.AbstractAuthExtensionProcessor;
import com.synaptix.auth.AuthInformation;
import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.AuthsBundleManager;

public class ConstraintAuthExtensionProcessor extends AbstractAuthExtensionProcessor {

	private static Log LOG = LogFactory.getLog(ConstraintAuthExtensionProcessor.class);

	private Map<Class<? extends AuthsBundle>, Map<ObjectActionCouple, Map<String, ConstraintDesc>>> constraintMap = new HashMap<Class<? extends AuthsBundle>, Map<ObjectActionCouple, Map<String, ConstraintDesc>>>();

	@Override
	public void addAuthInformation(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, AuthInformation authInformation) {
		Map<ObjectActionCouple, Map<String, ConstraintDesc>> map = constraintMap.get(bundleClass);
		if (map == null) {
			map = new HashMap<ObjectActionCouple, Map<String, ConstraintDesc>>();
			constraintMap.put(bundleClass, map);
		}

		map.put(new ObjectActionCouple(authInformation.getObject(), authInformation.getAction()), loadConstraintsAnnonation(authInformation.getMethod()));
	}

	/**
	 * Get all constraints desc for bundle
	 * 
	 * @param bundleClass
	 * @param object
	 * @param action
	 * @return
	 */
	public List<ConstraintDesc> getAllConstraintDescs(Class<? extends AuthsBundle> bundleClass, String object, String action) {
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
		Map<ObjectActionCouple, Map<String, ConstraintDesc>> map = constraintMap.get(bundleClass);
		Map<String, ConstraintDesc> cMap = map.get(new ObjectActionCouple(object, action));
		if (cMap == null) {
			throw new IllegalArgumentException("Not exists constraint for object=" + object + " action=" + action);
		}
		return Collections.unmodifiableList(new ArrayList<ConstraintDesc>(cMap.values()));
	}

	/**
	 * Get a constraint desc for bundle
	 * 
	 * @param bundleClass
	 * @param object
	 * @param action
	 * @param idConstraint
	 * @return
	 */
	public ConstraintDesc getConstraintDesc(Class<? extends AuthsBundle> bundleClass, String object, String action, String idConstraint) {
		if (bundleClass == null) {
			throw new NullArgumentException("bundleClass is null");
		}
		if (object == null) {
			throw new NullArgumentException("object is null");
		}
		if (action == null) {
			throw new NullArgumentException("action is null");
		}
		if (idConstraint == null) {
			throw new NullArgumentException("idConstraint is null");
		}
		checkExistBundleName(bundleClass);
		Map<ObjectActionCouple, Map<String, ConstraintDesc>> map = constraintMap.get(bundleClass);
		Map<String, ConstraintDesc> cMap = map.get(new ObjectActionCouple(object, action));
		if (cMap == null) {
			throw new IllegalArgumentException("Not exists constraint for object=" + object + " action=" + action);
		}
		ConstraintDesc c = cMap.get(idConstraint);
		if (c == null) {
			throw new IllegalArgumentException("Not exists constraint for object=" + object + " action=" + action + " idConstraint=" + idConstraint);
		}
		return c;
	}

	private void checkExistBundleName(Class<? extends AuthsBundle> bundleClass) {
		if (!constraintMap.containsKey(bundleClass)) {
			throw new IllegalArgumentException(bundleClass + " not a bundle class");
		}
	}

	@Override
	public Object process(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, Class<?> authExtensionClass, Method method, Object[] args) {
		if (authExtensionClass == IConstraintAuthExtension.class && method.getName().equals("getAuthConstraint") && method.getReturnType().equals(Object.class)
				&& method.getParameterTypes().length == 3 && method.getParameterTypes()[0] == String.class && method.getParameterTypes()[1] == String.class
				&& method.getParameterTypes()[2] == String.class) {
			String object = (String) args[0];
			String action = (String) args[1];
			String idConstraint = (String) args[2];

			Map<ObjectActionCouple, Map<String, ConstraintDesc>> map = constraintMap.get(bundleClass);
			Map<String, ConstraintDesc> cMap = map.get(new ObjectActionCouple(object, action));
			if (cMap == null) {
				LOG.error("Not exists constraint for object=" + object + " action=" + action + " idConstraint=" + idConstraint);
			} else {
				ConstraintDesc c = cMap.get(idConstraint);
				if (c == null) {
					LOG.error("Not exists constraint for object=" + object + " action=" + action + " idConstraint=" + idConstraint);
				} else {
					System.out.println("getAuthConstraint object=" + object + " action=" + action + " idConstraint=" + idConstraint);
				}
			}
		}
		return null;
	}

	private Map<String, ConstraintDesc> loadConstraintsAnnonation(Method method) {
		Map<String, ConstraintDesc> res = new HashMap<String, ConstraintDesc>();
		IConstraintAuthExtension.Constraint constraint = method.getAnnotation(IConstraintAuthExtension.Constraint.class);
		if (constraint != null) {
			res.put(constraint.id(), new ConstraintDesc(constraint.id(), constraint.type()));
		}

		IConstraintAuthExtension.Constraints constraints = method.getAnnotation(IConstraintAuthExtension.Constraints.class);
		if (constraints != null && constraints.value() != null && constraints.value().length > 0) {
			for (IConstraintAuthExtension.Constraint c : constraints.value()) {
				if (c != null) {
					if (res.containsKey(c.id())) {
						LOG.error("Constraint id=" + c.id() + " already exist for method=" + method);
					} else {
						res.put(c.id(), new ConstraintDesc(c.id(), c.type()));
					}
				}
			}
		}
		return res;
	}

	public static class ConstraintDesc {

		private final String id;

		private final Class<? extends IConstraint> type;

		public ConstraintDesc(String id, Class<? extends IConstraint> type) {
			super();
			this.id = id;
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public Class<? extends IConstraint> getType() {
			return type;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
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
			return new HashCodeBuilder().append(object).append(action).toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectActionCouple) {
				ObjectActionCouple o = (ObjectActionCouple) obj;
				return new EqualsBuilder().append(object, o.object).append(action, o.action).isEquals();
			}
			return super.equals(obj);
		}
	}
}
