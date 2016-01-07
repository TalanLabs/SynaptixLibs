package com.synaptix.taskmanager.model;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_TASK_BACKUP")
public interface ITaskBackup extends IEntity, ITracable {

	@Column(name = "ID_CLUSTER")
	IId getIdCluster();

	void setIdCluster(IId idCluster);

	@Column(name = "ID_OBJECT")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	IId getIdObject();

	void setIdObject(IId idObject);

	@Column(name = "OBJECT_TYPE", length = 512)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	Class<? extends ITaskObject<?>> getObjectType();

	void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "NB_RETRY", length = 2, nullable = false)
	int getNbRetry();

	void setNbRetry(int nbRetry);

	@Column(name = "SERVICE_CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getServiceCode();

	void setServiceCode(String serviceCode);

	/**
	 * The priority of the backup: 0 = top priority
	 */
	@Column(name = "PRIORITY", length = 2, nullable = false)
	int getPriority();

	void setPriority(int priority);

}
