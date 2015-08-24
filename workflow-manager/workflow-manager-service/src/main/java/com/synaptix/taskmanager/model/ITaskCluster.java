package com.synaptix.taskmanager.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_TASK_CLUSTER")
public interface ITaskCluster extends ITracable, IEntity {

	@Column(name = "CHECK_GRAPH_CREATED", length = 1, nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckGraphCreated();

	public void setCheckGraphCreated(boolean checkGraphCreated);

	@Column(name = "CHECK_ARCHIVE", length = 1, nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckArchive();

	public void setCheckArchive(boolean checkArchive);

	@Collection(sqlTableName = "T_TASK_CLUSTER_DEPENDENCY", idSource = "ID", idTarget = "ID_CLUSTER", alias = "c_", componentClass = ITaskClusterDependency.class)
	public List<ITaskClusterDependency> getTaskClusterDependencies();

	public void setTaskClusterDependencies(List<ITaskClusterDependency> taskClusterDependencies);
}
