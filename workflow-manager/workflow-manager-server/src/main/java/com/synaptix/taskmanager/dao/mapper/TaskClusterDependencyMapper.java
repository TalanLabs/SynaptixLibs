package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITaskClusterDependency;

public interface TaskClusterDependencyMapper {

	public List<ITaskClusterDependency> selectTaskClusterDependenciesByIdTaskCluster(@Param("idTaskCluster") IId idTaskCluster);

	public void updateDependency(@Param("newIdCluster") IId idTaskCluster, @Param("idObject") IId id);
}
