package com.synaptix.taskmanager.dao.mapper;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;

public interface ErrorMapper {

	void deleteErrorsByIdObject(@Param("idObject") IId idObject);

	void deleteErrorsByIdTask(@Param("idTask") IId idTask);

}
