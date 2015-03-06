package com.synaptix.mybatis.component.mapper;

import java.lang.reflect.Method;

class MethodMapper {

	private final Method method;

	private final boolean useProvider;

	private final String sql;

	private final Class<?> providerClass;

	public MethodMapper(Method method, String sql) {
		this(method, false, sql, null);
	}

	public MethodMapper(Method method, Class<?> providerClass) {
		this(method, true, null, providerClass);
	}

	private MethodMapper(Method method, boolean useProvider, String sql, Class<?> providerClass) {
		super();

		this.method = method;
		this.useProvider = useProvider;
		this.sql = sql;
		this.providerClass = providerClass;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getProviderClass() {
		return providerClass;
	}

	public String getSql() {
		return sql;
	}

	public boolean isUseProvider() {
		return useProvider;
	}
}
