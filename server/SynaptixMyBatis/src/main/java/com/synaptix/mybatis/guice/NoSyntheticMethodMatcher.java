package com.synaptix.mybatis.guice;

import java.lang.reflect.Method;

import com.google.inject.matcher.AbstractMatcher;

/**
 * Created by NicolasP on 18/12/2015.
 */
public final class NoSyntheticMethodMatcher extends AbstractMatcher<Method> {

	public static final NoSyntheticMethodMatcher INSTANCE = new NoSyntheticMethodMatcher();

	private NoSyntheticMethodMatcher() {
	}

	@Override
	public boolean matches(Method method) {
		return !method.isSynthetic();
	}
}
