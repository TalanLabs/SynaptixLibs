package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;

public interface StatusGraphMapper {

	public List<IStatusGraph> selectStatusGraphsByTaskObjectType(@Param("objectType") Class<? extends ITaskObject<?>> objectType);

}
