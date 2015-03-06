package com.synaptix.auth.method;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.synaptix.auth.AuthsBundle;

public class DefaultAuthMethod implements IAuthMethod {

	private final Class<? extends AuthsBundle> bundleClass;

	private final String object;

	private final String action;

	public DefaultAuthMethod(Class<? extends AuthsBundle> bundleClass,
			String object, String action) {
		super();
		this.bundleClass = bundleClass;
		this.object = object;
		this.action = action;
	}

	@Override
	public Class<? extends AuthsBundle> getBundleClass() {
		return bundleClass;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public String getObject() {
		return object;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(bundleClass).append(object)
				.append(action).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DefaultAuthMethod) {
			DefaultAuthMethod o = (DefaultAuthMethod) obj;
			return new EqualsBuilder().append(bundleClass, o.bundleClass)
					.append(object, o.object).append(action, o.action)
					.isEquals();
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return new StringBuilder("[").append(bundleClass.getName()).append(",")
				.append(object).append(",").append(action).append("]")
				.toString();
	}
}
