package com.synaptix.mybatis.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.IDaoSessionExt;
import com.synaptix.mybatis.dao.exceptions.VersionConflictDaoException;
import com.synaptix.mybatis.service.Transactional;
import com.synaptix.service.ServiceException;
import com.synaptix.service.exceptions.VersionConflictServiceException;

public class TransactionnelMethodInterceptor implements MethodInterceptor {

	@Inject
	private IDaoSession daoSession;

	@Inject
	private SqlSessionManager sqlSessionManager;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Transactional transactionnel = invocation.getMethod().getAnnotation(Transactional.class);
		try {
			if (daoSession instanceof IDaoSessionExt) {
				if (!sqlSessionManager.isManagedSessionStarted()) {
					((IDaoSessionExt) daoSession).setCheckVersionConflictDaoExceptionInSession(transactionnel.checkVersionConflict());
				}
			}
			daoSession.begin();
			Object res = invocation.proceed();
			if (transactionnel.commit()) {
				daoSession.commit();
			}
			return res;
		} catch (ServiceException e) {
			throw e;
		} catch (VersionConflictDaoException e) {
			throw new VersionConflictServiceException();
		} catch (Exception e) {
			throw new ServiceException("", e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}
}
