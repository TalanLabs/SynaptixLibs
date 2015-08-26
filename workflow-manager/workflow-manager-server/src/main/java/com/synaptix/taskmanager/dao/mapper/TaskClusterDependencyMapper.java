package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.taskmanager.model.ITaskClusterDependency;

public interface TaskClusterDependencyMapper {

	public List<ITaskClusterDependency> selectTaskClusterDependenciesByIdTaskCluster(@Param("idTaskCluster") Serializable idTaskCluster);

	public void updateDependency(@Param("newIdCluster") Serializable idTaskCluster, @Param("idObject") Serializable id);
}
