package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;

import org.apache.ibatis.annotations.Param;

public interface ErrorMapper {

	void deleteErrorsByIdObject(@Param("idObject") Serializable idObject);

	void deleteErrorsByIdTask(@Param("idTask") Serializable idTask);

}
