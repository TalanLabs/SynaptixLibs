package com.synaptix.taskmanager.model;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;

@Table(name = "T_ASSO_TASK_CHAIN_CRIT_CHAIN")
@SynaptixComponent
public interface IAssoTaskChainCriteriaChain extends IEntity {

	@Column(name = "ID_TASK_CHAIN_CRITERIA", nullable = false)
	@BusinessKey
	public IId getIdTaskChainCriteria();

	public void setIdTaskChainCriteria(IId idTaskChainCriteria);

	@Column(name = "ID_TASK_CHAIN", nullable = false)
	@BusinessKey
	public IId getIdTaskChain();

	public void setIdTaskChain(IId idTaskChain);

}
