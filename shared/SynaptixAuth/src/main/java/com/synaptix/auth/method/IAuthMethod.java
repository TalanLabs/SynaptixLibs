package com.synaptix.auth.method;

import com.synaptix.auth.AuthsBundle;

public interface IAuthMethod {

	public Class<? extends AuthsBundle> getBundleClass();

	public String getObject();

	public String getAction();

}
