package com.synaptix.deployer.dao;

import java.util.Locale;

import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.IDaoUserContext;

public class DeployerDaoContext implements IDaoUserContext {

	@Override
	public String getCurrentLogin() {
		return "DEPLOYER";
	}

	@Override
	public IId getCurrentIdUser() {
		return null;
	}

	@Override
	public Locale getCurrentLocale() {
		return Locale.FRANCE;
	}
}
