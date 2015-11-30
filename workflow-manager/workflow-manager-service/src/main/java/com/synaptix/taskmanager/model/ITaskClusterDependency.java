package com.synaptix.taskmanager.model;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_TASK_CLUSTER_DEPENDENCY")
public interface ITaskClusterDependency extends ITracable, IEntity {
	@BusinessKey
	@Column(name = "ID_CLUSTER", nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public IId getIdCluster();

	public void setIdCluster(IId idCluster);

	@BusinessKey
	@Column(name = "ID_OBJECT", nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public IId getIdObject();

	public void setIdObject(IId idObject);

	@BusinessKey
	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

}
