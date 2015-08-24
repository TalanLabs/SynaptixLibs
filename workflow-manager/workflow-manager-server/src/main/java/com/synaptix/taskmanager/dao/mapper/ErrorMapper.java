package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IErrorEntity;

public interface ErrorMapper {

	public void deleteErrorsByIdObject(@Param("idObject") Serializable idObject);

	public void deleteErrorsByIdTask(@Param("idTask") Serializable idTask);

	public void pingDB();

	public List<IErrorEntity> searchErrorsByIdObject(@Param("idObject") Serializable idObject);

	public IErrorEntity findById(@Param("id") Serializable idError);

}
