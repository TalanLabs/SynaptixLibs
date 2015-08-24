package com.synaptix.taskmanager.service;

import java.util.Map;

import com.google.inject.Inject;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.manager.IObjectTypeTaskFactory;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskObject;

public class TaskChainCriteriaServerService extends AbstractSimpleService implements ITaskChainCriteriaService {

	@Inject
	private Map<Class<? extends ITaskObject<?>>, IObjectTypeTaskFactory<?>> objectTypeTaskFactoryMap;

	@Override
	public ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(Object context, String status, String nextStatus, Class<? extends ITaskObject<?>> objectType) {
		ITaskChainCriteria<? extends Enum<?>> taskChainCriteria = null;
		if (objectTypeTaskFactoryMap != null && !objectTypeTaskFactoryMap.isEmpty()) {
			IObjectTypeTaskFactory<?> objectTypeTaskFactory = objectTypeTaskFactoryMap.get(objectType);
			if (objectTypeTaskFactory != null) {
				try {
					getDaoSession().begin();
					taskChainCriteria = objectTypeTaskFactory.getTaskChainCriteria(context, status, nextStatus);
				} catch (Exception e) {
					throw new ServiceException(e);
				} finally {
					getDaoSession().end();
				}
			}
		}
		return taskChainCriteria;
	}

}
