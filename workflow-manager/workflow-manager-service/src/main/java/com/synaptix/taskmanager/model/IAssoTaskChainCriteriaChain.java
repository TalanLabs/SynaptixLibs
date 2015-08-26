package com.synaptix.taskmanager.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;

@Table(name = "T_ASSO_TASK_CHAIN_CRIT_CHAIN")
@SynaptixComponent
public interface IAssoTaskChainCriteriaChain extends IEntity {

	@Column(name = "ID_TASK_CHAIN_CRITERIA", nullable = false)
	@BusinessKey
	public Serializable getIdTaskChainCriteria();

	public void setIdTaskChainCriteria(Serializable idTaskChainCriteria);

	@Column(name = "ID_TASK_CHAIN", nullable = false)
	@BusinessKey
	public Serializable getIdTaskChain();

	public void setIdTaskChain(Serializable idTaskChain);

}
