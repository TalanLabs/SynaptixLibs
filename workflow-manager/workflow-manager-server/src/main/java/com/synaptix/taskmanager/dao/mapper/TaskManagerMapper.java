package com.synaptix.taskmanager.dao.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITaskBackup;
import com.synaptix.taskmanager.model.ITaskObject;

public interface TaskManagerMapper {

	public void archiveTaskCluster(@Param("idCluster") IId idCluster);

	public ITaskBackup findBackupByIdCluster(@Param("idCluster") IId idCluster);

	public <E extends Enum<E>, F extends ITaskObject<E>> ITaskBackup findBackupByIdObjectAndClass(@Param("idObject") IId idObject, @Param("objectType") Class<F> objectClass);

	public void flagTasksBackupToLaunch(@Param("idProcess") IId idProcess, @Param("nbLines") int nbLines, @Param("maxRetry") int maxRetry, @Param("date") Date date,
			@Param("createdDateDelay") int createdDateDelay, @Param("updatedDateDelay") int updatedDateDelay);

	public List<ITaskBackup> findTasksBackupToLaunch(@Param("idProcess") IId idProcess);

	public void deleteTaskCluster(@Param("idCluster") IId idCluster);

}
