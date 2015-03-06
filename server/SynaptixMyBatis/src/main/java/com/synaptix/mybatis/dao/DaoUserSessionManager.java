package com.synaptix.mybatis.dao;

import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.synaptix.mybatis.hack.SynaptixUserSession;

public class DaoUserSessionManager {

	@Inject
	private SynaptixUserSession synaptixUserSession;

	@Inject
	private SqlSessionManager sqlSessionManager;

	public void updateDaoUserSession() {
		if (sqlSessionManager.isManagedSessionStarted()) {
			synaptixUserSession.insertTempUserSession(sqlSessionManager.getConnection());
		}
	}
}
