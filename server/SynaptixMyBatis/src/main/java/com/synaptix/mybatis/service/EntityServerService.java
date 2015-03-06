package com.synaptix.mybatis.service;

import java.io.Serializable;

import com.google.inject.Inject;
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.service.IEntityService;
import com.synaptix.service.ServiceException;

public class EntityServerService implements IEntityService {

	private final IDaoSession daoSession;

	private final EntityServiceDelegate entityServiceDelegate;

	@Inject
	public EntityServerService(IDaoSession daoSession, EntityServiceDelegate entityServiceDelegate) {
		super();
		this.daoSession = daoSession;
		this.entityServiceDelegate = entityServiceDelegate;
	}

	@Override
	public <E extends IEntity> Serializable addEntity(E entity) throws ServiceException {
		Serializable res = null;
		try {
			daoSession.begin();
			int count = entityServiceDelegate.addEntity(entity, true);
			if (count == 1) {
				res = entity.getId();
				daoSession.commit();
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException("", e.getMessage(), e);
		} finally {
			daoSession.end();
		}
		return res;
	}

	@Override
	public <E extends IEntity> Serializable editEntity(E entity) throws ServiceException {
		Serializable res = null;
		try {
			daoSession.begin();
			int count = entityServiceDelegate.editEntity(entity, true);
			if (count == 1) {
				res = entity.getId();
				daoSession.commit();
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException("", e.getMessage(), e);
		} finally {
			daoSession.end();
		}
		return res;
	}

	@Override
	public <E extends IEntity> Serializable removeEntity(E entity) throws ServiceException {
		Serializable res = null;
		try {
			daoSession.begin();
			int count = entityServiceDelegate.removeEntity(entity);
			if (count == 1) {
				res = entity.getId();
				daoSession.commit();
			}
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
		return res;
	}

	@Override
	public <E extends IEntity> E findEntityById(Class<E> entityClass, Serializable id) throws ServiceException {
		try {
			daoSession.begin();
			return entityServiceDelegate.findEntityById(entityClass, id);
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			daoSession.end();
		}
	}
}
