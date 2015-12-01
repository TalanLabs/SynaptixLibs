package com.synaptix.taskmanager.service;

import com.synaptix.service.ICRUDEntityService;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskType;

public interface ITaskChainService extends ICRUDEntityService<ITaskChain> {

	public boolean getTaskChainsFromTaskType(ITaskType taskType);

	public boolean getTaskChainCriteriasFromTaskChain(ITaskChain taskChain);

}
