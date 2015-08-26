package com.synaptix.taskmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.service.model.ICancellableEntity;

@SynaptixComponent
@Table(name = "T_STATUS_GRAPH")
@Cache(readOnly = true)
public interface IStatusGraph extends ITracable, ICancellableEntity {

	@BusinessKey
	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@BusinessKey
	@Column(name = "CURRENT_STATUS", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCurrentStatus();

	public void setCurrentStatus(String currentStatus);

	@BusinessKey
	@Column(name = "NEXT_STATUS", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getNextStatus();

	public void setNextStatus(String nextStatus);

	@Column(name = "ID_TASK_TYPE")
	public Serializable getIdTaskType();

	public void setIdTaskType(Serializable idTaskType);

	@Collection(sqlTableName = "T_TASK_TYPE", idSource = "ID_TASK_TYPE", alias = "tt_", componentClass = ITaskType.class)
	public ITaskType getTaskType();

	public void setTaskType(ITaskType taskType);

}
