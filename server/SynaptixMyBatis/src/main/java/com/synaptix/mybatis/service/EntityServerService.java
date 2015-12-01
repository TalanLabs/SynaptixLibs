package com.synaptix.mybatis.service;

import com.google.inject.Inject;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
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
	public <E extends IEntity> IId addEntity(E entity) throws ServiceException {
		IId res = null;
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
	public <E extends IEntity> IId editEntity(E entity) throws ServiceException {
		IId res = null;
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
	public <E extends IEntity> IId removeEntity(E entity) throws ServiceException {
		IId res = null;
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
	public <E extends IEntity> E findEntityById(Class<E> entityClass, IId id) throws ServiceException {
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
