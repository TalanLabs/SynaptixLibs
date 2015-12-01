package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;

public interface TaskChainMapper {

	/**
	 * Find the tasks chain using a task type
	 */
	public List<Serializable> findTaskChainsByTaskType(@Param("idTaskType") IId idTaskType);

	/**
	 * Find the tasks chain criteria using a task chain
	 */
	public List<Serializable> findTaskChainCriteriaByTaskChain(@Param("idTaskChain") IId idTaskChain);

	public void deleteAssoTaskChainTaskType(@Param("idTaskChain") IId idTaskChain);

	public void deleteAssoTaskChainCriteriaTaskChain(@Param("idTaskChainCriteria") IId idTaskChainCriteria);

}
