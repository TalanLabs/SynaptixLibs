package com.synaptix.taskmanager.manager.updatestatus;

import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskObject;

public interface IUpdateStatusTaskService<E extends Enum<E>, F extends ITaskObject<E>> extends ITaskService {

	public E getNewStatus();

	public Class<E> getStatusClass();

}
