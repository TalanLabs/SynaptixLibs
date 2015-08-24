package com.synaptix.taskmanager.service;

import java.util.List;

import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;

public interface IStatusGraphService {

	/**
	 * Find all status graphs by task object class
	 * 
	 * @param taskObjectClass
	 * @return
	 */
	public List<IStatusGraph> findStatusGraphsBy(Class<? extends ITaskObject<?>> taskObjectClass);

}
