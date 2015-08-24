package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface TaskChainMapper {

	/**
	 * Find the tasks chain using a task type
	 */
	public List<Serializable> findTaskChainsByTaskType(@Param("idTaskType") Serializable idTaskType);

	/**
	 * Find the tasks chain criteria using a task chain
	 */
	public List<Serializable> findTaskChainCriteriaByTaskChain(@Param("idTaskChain") Serializable idTaskChain);

	public void deleteAssoTaskChainTaskType(@Param("idTaskChain") Serializable idTaskChain);

	public void deleteAssoTaskChainCriteriaTaskChain(@Param("idTaskChainCriteria") Serializable idTaskChainCriteria);

}