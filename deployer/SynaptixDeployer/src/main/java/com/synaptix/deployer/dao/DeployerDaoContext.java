package com.synaptix.deployer.dao;

import java.io.Serializable;
import java.util.Locale;

import com.synaptix.mybatis.dao.IDaoUserContext;

public class DeployerDaoContext implements IDaoUserContext {

	@Override
	public String getCurrentLogin() {
		return "DEPLOYER";
	}

	@Override
	public Serializable getCurrentIdUser() {
		return null;
	}

	@Override
	public Locale getCurrentLocale() {
		return Locale.FRANCE;
	}
}
