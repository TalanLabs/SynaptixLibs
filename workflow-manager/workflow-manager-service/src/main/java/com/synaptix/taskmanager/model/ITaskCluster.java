package com.synaptix.taskmanager.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import org.joda.time.LocalDate;

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

	/**
	 * Archive date: day when cluster tasks were moved to T_TASK_ARCH
	 */
	@Column(name = "ARCHIVE_DATE")
	@JdbcType(JdbcTypesEnum.DATE)
	public LocalDate getArchiveDate();
	
	public void setArchiveDate(LocalDate archiveDate);

	/**
	 * True if cluster tasks have been archived, and deleted.
	 */
	@Column(name = "CHECK_TASK_ARCH_DELETED", length = 1)
	@JdbcType(JdbcTypesEnum.CHAR)
	public boolean isCheckTaskArchDeleted();

	public void setCheckTaskArchDeleted(boolean checkTaskArchDeleted);

}
