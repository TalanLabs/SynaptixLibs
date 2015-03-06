package com.synaptix.mybatis.hack;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.dao.IDaoUserContext;

public class SynaptixUserSession {

	private static final Log LOG = LogFactory.getLog(SynaptixUserSession.class);

	@Inject
	private IDaoUserContext daoUserContext;

	public void insertTempUserSession(Connection connection) {
		String sq;
		if (daoUserContext != null && connection != null) {
			String language = null;
			try {
				language = daoUserContext.getCurrentLocale() != null ? daoUserContext.getCurrentLocale().getISO3Language() : null;
			} catch (MissingResourceException e) {
			}

			StringBuilder sb = new StringBuilder();
			sb.append("call p_user_session.set_current_user(");
			if (daoUserContext.getCurrentIdUser() != null) {
				sb.append("hextoraw('").append(((IdRaw) daoUserContext.getCurrentIdUser()).getHex()).append("')");
			} else {
				sb.append("null");
			}
			sb.append(",");
			if (daoUserContext.getCurrentLogin() != null) {
				sb.append("'").append(daoUserContext.getCurrentLogin()).append("'");
			} else {
				sb.append("null");
			}
			sb.append(",");
			if (language != null) {
				sb.append("'").append(language).append("'");
			} else {
				sb.append("null");
			}
			sb.append(")");
			sq = sb.toString();
		} else {
			sq = "call p_user_session.set_current_user(null,null,null)";
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Call user session for " + sq);
		}
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(sq);
			s.close();
		} catch (SQLException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
