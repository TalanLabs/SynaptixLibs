package com.synaptix.taskmanager.controller.helper;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ServiceException;

public interface IImportEntitiesService<E extends IEntity> {

	/**
	 * Import entity list, verify unique
	 * 
	 * @param entities
	 * @param importServiceCallback
	 * @return
	 * @throws ServiceException
	 */
//	public int importEntities(List<E> entities, IImportServiceCallback importServiceCallback) throws ServiceException;

}
