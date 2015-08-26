package com.synaptix.taskmanager.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.service.model.ICancellableEntity;

@SynaptixComponent
@Table(name = "T_TASK_CHAIN")
@Cache(readOnly = true, links = { IAssoTaskChainType.class })
public interface ITaskChain extends ITracable, ICancellableEntity {

	@BusinessKey
	@Column(name = "CODE", length = 240, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCode();

	public void setCode(String code);

	@BusinessKey
	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "DESCRIPTION", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getDescription();

	public void setDescription(String description);

	@Collection(sqlTableName = "T_ASSO_TASK_CHAIN_TYPE", idSource = "ID_TASK_CHAIN", idTarget = "ID_TASK_TYPE", alias = "tt_", componentClass = ITaskType.class)
	public List<ITaskType> getTaskTypes();

	public void setTaskTypes(List<ITaskType> taskTypes);

	@Column(name = "GRAPH_RULE", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getGraphRule();

	public void setGraphRule(String graphRule);

	@Column(name = "GRAPH_RULE_READABLE", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getGraphRuleReadable();

	public void setGraphRuleReadable(String graphRuleReadable);

}
