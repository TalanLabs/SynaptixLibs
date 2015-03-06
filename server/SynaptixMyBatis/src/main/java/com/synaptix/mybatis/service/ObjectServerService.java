package com.synaptix.mybatis.service;

import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.delegate.ObjectServiceDelegate;
import com.synaptix.service.ServiceException;

public class ObjectServerService {

	@Inject
	private IDaoSession daoSession;

	@Inject
	private ObjectServiceDelegate objectServiceDelegate;

	public int insertMap(String tableName, Map<String, Object> valueMap) {
		try {
			daoSession.begin();
			int res = objectServiceDelegate.insertMap(tableName, valueMap);
			daoSession.commit();
			return res;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}
}
