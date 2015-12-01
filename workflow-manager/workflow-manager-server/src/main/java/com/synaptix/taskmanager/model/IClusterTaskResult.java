package com.synaptix.taskmanager.model;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IId;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

@SynaptixComponent
public interface IClusterTaskResult extends IComponent, IDatabaseComponentExtension {

	@Column(name = "ID_TASK")
	public IId getIdTask();

	public void setIdTask(IId idTask);

	@Column(name = "ID_CLUSTER", nullable = false)
	public IId getIdCluster();

	public void setIdCluster(IId idCluster);

}
