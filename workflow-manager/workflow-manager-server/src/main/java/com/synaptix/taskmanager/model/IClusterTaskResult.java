package com.synaptix.taskmanager.model;

import java.io.Serializable;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

@SynaptixComponent
public interface IClusterTaskResult extends IComponent, IDatabaseComponentExtension {

	@Column(name = "ID_TASK")
	public Serializable getIdTask();

	public void setIdTask(Serializable idTask);

	@Column(name = "ID_CLUSTER", nullable = false)
	public Serializable getIdCluster();

	public void setIdCluster(Serializable idCluster);

}
