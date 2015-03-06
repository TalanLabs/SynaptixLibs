package com.synaptix.auth.helper;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.auth.method.IAuthMethod;

public class AuthBuilder {

	public AuthBuilder() {
		super();
	}

	public AndAuthBuilder and(IAuthMethod authMethod) {
		return and(new DefaultAuth(authMethod));
	}

	public AndAuthBuilder and(IAuth auth) {
		return new AndAuthBuilder(auth);
	}

	public OrAuthBuilder or(IAuthMethod authMethod) {
		return or(new DefaultAuth(authMethod));
	}

	public OrAuthBuilder or(IAuth auth) {
		return new OrAuthBuilder(auth);
	}

	public static final class AndAuthBuilder {

		private List<IAuth> auths;

		public AndAuthBuilder(IAuth auth) {
			super();

			auths = new ArrayList<IAuth>();
			auths.add(auth);
		}

		public AndAuthBuilder and(IAuthMethod authMethod) {
			return and(new DefaultAuth(authMethod));
		}

		public AndAuthBuilder and(IAuth auth) {
			auths.add(auth);
			return this;
		}

		public IAuth build() {
			return new DefaultAndAuth(auths.toArray(new IAuth[auths.size()]));
		}
	}

	public static final class OrAuthBuilder {

		private List<IAuth> auths;

		public OrAuthBuilder(IAuth auth) {
			super();

			auths = new ArrayList<IAuth>();
			auths.add(auth);
		}

		public OrAuthBuilder or(IAuthMethod authMethod) {
			return or(new DefaultAuth(authMethod));
		}

		public OrAuthBuilder or(IAuth auth) {
			auths.add(auth);
			return this;
		}

		public IAuth build() {
			return new DefaultOrAuth(auths.toArray(new IAuth[auths.size()]));
		}
	}

}
