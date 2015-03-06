package com.synaptix.auth;

import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AuthInformation {

	private final Class<? extends AuthsBundle> bundleClass;

	private final Method method;

	private final String object;

	private final String action;

	private final String description;

	public AuthInformation(Method method, Class<? extends AuthsBundle> bundleClass, String object, String action, String description) {
		super();
		this.method = method;
		this.bundleClass = bundleClass;
		this.object = object;
		this.action = action;
		this.description = description;
	}

	public Method getMethod() {
		return method;
	}

	public Class<? extends AuthsBundle> getBundleClass() {
		return bundleClass;
	}

	public String getObject() {
		return object;
	}

	public String getAction() {
		return action;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return method.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AuthInformation) {
			AuthInformation o = (AuthInformation) obj;
			return method.equals(o.method);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
