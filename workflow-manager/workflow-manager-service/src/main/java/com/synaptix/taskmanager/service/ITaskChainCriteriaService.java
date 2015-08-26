package com.synaptix.taskmanager.service;

import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITaskChainCriteriaService {

	/**
	 * getTaskChainCriteria by context
	 * 
	 * @param object
	 * @param objectType
	 * @return
	 */
	public ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(Object context, String status, String nextStatus, Class<? extends ITaskObject<?>> objectType);

}
