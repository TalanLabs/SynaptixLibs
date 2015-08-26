package com.synaptix.taskmanager.guice.child;

import com.google.inject.Module;
import com.synaptix.mybatis.guice.AbstractSynaptixMyBatisModule;
import com.synaptix.taskmanager.dao.mapper.AssoTaskPreviousTaskMapper;
import com.synaptix.taskmanager.dao.mapper.StatusGraphMapper;
import com.synaptix.taskmanager.dao.mapper.TaskChainMapper;
import com.synaptix.taskmanager.dao.mapper.TaskClusterDependencyMapper;
import com.synaptix.taskmanager.dao.mapper.TaskManagerMapper;
import com.synaptix.taskmanager.dao.mapper.TaskMapper;
import com.synaptix.taskmanager.dao.mapper.TodoMapper;

public class MyBatisTaskManagerModule extends AbstractSynaptixMyBatisModule implements Module {

	@Override
	protected void configure() {
		addMapperClass(TaskMapper.class);
		addMapperClass(AssoTaskPreviousTaskMapper.class);
		addMapperClass(TodoMapper.class);
		addMapperClass(TaskClusterDependencyMapper.class);
		addMapperClass(StatusGraphMapper.class);
		addMapperClass(TaskManagerMapper.class);
		addMapperClass(TaskChainMapper.class);
	}
}
