package com.synaptix.mybatis.dao;

import java.io.Serializable;
import java.util.Locale;

public interface IDaoUserContext {

	public Serializable getCurrentIdUser();

	public String getCurrentLogin();

	public Locale getCurrentLocale();

}
