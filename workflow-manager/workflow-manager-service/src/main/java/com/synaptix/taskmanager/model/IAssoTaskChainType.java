package com.synaptix.taskmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;

@Table(name = "T_ASSO_TASK_CHAIN_TYPE")
@SynaptixComponent
@Cache(readOnly = true, links = { ITaskChain.class, ITaskType.class })
public interface IAssoTaskChainType extends IEntity {

	@Column(name = "ID_TASK_CHAIN", nullable = false)
	@BusinessKey
	public Serializable getIdTaskChain();

	public void setIdTaskChain(Serializable idTaskChain);

	@Column(name = "ID_TASK_TYPE", nullable = false)
	@BusinessKey
	public Serializable getIdTaskType();

	public void setIdTaskType(Serializable idTaskType);

}
