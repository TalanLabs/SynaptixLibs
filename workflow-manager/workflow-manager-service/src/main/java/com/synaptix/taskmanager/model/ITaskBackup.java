package com.synaptix.taskmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_TASK_BACKUP")
public interface ITaskBackup extends IEntity, ITracable {

	@Column(name = "ID_CLUSTER")
	public Serializable getIdCluster();

	public void setIdCluster(Serializable idCluster);

	@Column(name = "ID_OBJECT")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Serializable getIdObject();

	public void setIdObject(Serializable idObject);

	@Column(name = "OBJECT_TYPE", length = 512)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "NB_RETRY", length = 2, nullable = false)
	public int getNbRetry();

	public void setNbRetry(int nbRetry);

	@Column(name = "SERVICE_CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getServiceCode();

	public void setServiceCode(String serviceCode);

}
