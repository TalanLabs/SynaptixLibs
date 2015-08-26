package com.synaptix.taskmanager.service;

import java.io.Serializable;

import com.google.inject.Inject;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.mybatis.service.Transactional;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.model.ITodoFolder;

public class TodoFolderServerService extends AbstractSimpleService implements ITodoFolderService {

	@Inject
	private EntityServiceDelegate entityServiceDelegate;

	@Override
	@Transactional(commit = true)
	public Serializable addCRUDEntity(ITodoFolder entity) throws ServiceException {
		return entityServiceDelegate.addEntity(entity, false) > 0 ? entity.getId() : null;
	}

	@Override
	@Transactional(commit = true)
	public Serializable editCRUDEntity(ITodoFolder entity) throws ServiceException {
		return entityServiceDelegate.editEntity(entity, false) > 0 ? entity.getId() : null;
	}

	@Override
	@Transactional(commit = true)
	public Serializable removeCRUDEntity(ITodoFolder entity) throws ServiceException {
		return entityServiceDelegate.removeEntity(entity) > 0 ? entity.getId() : null;
	}
}
