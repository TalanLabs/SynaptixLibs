package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.taskmanager.model.ITaskBackup;
import com.synaptix.taskmanager.model.ITaskObject;

public interface TaskManagerMapper {

	public void archiveTaskCluster(@Param("idCluster") Serializable idCluster);

	public ITaskBackup findBackupByIdCluster(@Param("idCluster") Serializable idCluster);

	public <E extends Enum<E>, F extends ITaskObject<E>> ITaskBackup findBackupByIdObjectAndClass(@Param("idObject") Serializable idObject, @Param("objectType") Class<F> objectClass);

	public void flagTasksBackupToLaunch(@Param("idProcess") Serializable idProcess, @Param("nbLines") int nbLines, @Param("maxRetry") int maxRetry, @Param("date") Date date);

	public List<ITaskBackup> findTasksBackupToLaunch(@Param("idProcess") Serializable idProcess);

	public void deleteTaskCluster(@Param("idCluster") Serializable idCluster);
}
