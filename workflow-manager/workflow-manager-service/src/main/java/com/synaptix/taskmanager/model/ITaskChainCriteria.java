package com.synaptix.taskmanager.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.service.model.ICancellableEntity;

@SynaptixComponent
@Cache(readOnly = true)
public interface ITaskChainCriteria<T extends Enum<?>> extends ITracable, ICancellableEntity {

	@BusinessKey
	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@BusinessKey
	@Column(name = "CURRENT_STATUS", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public T getCurrentStatus();

	public void setCurrentStatus(T currentStatus);

	@BusinessKey
	@Column(name = "NEXT_STATUS", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public T getNextStatus();

	public void setNextStatus(T nextStatus);

	@Column(name = "DESCRIPTION", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getDescription();

	public void setDescription(String description);

	@Collection(sqlTableName = "T_ASSO_TASK_CHAIN_CRIT_CHAIN", idSource = "ID_TASK_CHAIN_CRITERIA", idTarget = "ID_TASK_CHAIN", alias = "tc_", componentClass = ITaskChain.class)
	public List<ITaskChain> getTaskChains();

	public void setTaskChains(List<ITaskChain> taskChains);

	@Column(name = "GRAPH_RULE", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getGraphRule();

	public void setGraphRule(String graphRule);

	@Column(name = "GRAPH_RULE_READABLE", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getGraphRuleReadable();

	public void setGraphRuleReadable(String graphRuleReadable);

	@Column(name = "ID_TODO_FOLDER")
	public Serializable getIdTodoFolder();

	public void setIdTodoFolder(Serializable idTodoFolder);

	@Collection(sqlTableName = "T_TODO_FOLDER", idSource = "ID_TODO_FOLDER", alias = "tf_", componentClass = ITodoFolder.class)
	public ITodoFolder getTodoFolder();

	public void setTodoFolder(ITodoFolder todoFolder);
}
