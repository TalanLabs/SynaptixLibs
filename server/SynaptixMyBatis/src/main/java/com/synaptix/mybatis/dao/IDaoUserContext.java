package com.synaptix.mybatis.dao;

import java.util.Locale;

import com.synaptix.entity.IId;

public interface IDaoUserContext {

	public IId getCurrentIdUser();

	public String getCurrentLogin();

	public Locale getCurrentLocale();

}
