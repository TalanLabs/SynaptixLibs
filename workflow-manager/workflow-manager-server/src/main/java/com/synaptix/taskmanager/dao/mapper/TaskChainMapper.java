package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;

public interface TaskChainMapper {

	/**
	 * Find the tasks chain using a task type
	 */
	List<IId> findTaskChainsByTaskType(@Param("idTaskType") IId idTaskType);

	/**
	 * Find the tasks chain criteria using a task chain
	 */
	List<IId> findTaskChainCriteriaByTaskChain(@Param("idTaskChain") IId idTaskChain);

	void deleteAssoTaskChainTaskType(@Param("idTaskChain") IId idTaskChain);

	void deleteAssoTaskChainCriteriaTaskChain(@Param("idTaskChainCriteria") IId idTaskChainCriteria);

}
