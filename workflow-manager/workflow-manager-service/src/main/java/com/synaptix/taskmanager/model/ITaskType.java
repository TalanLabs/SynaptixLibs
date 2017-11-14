package com.synaptix.taskmanager.model;

import javax.persistence.Column;
import javax.persistence.Table;

import org.joda.time.Duration;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IId;
import com.synaptix.entity.INlsMessage;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.service.model.ICancellableEntity;
import com.synaptix.taskmanager.model.domains.ServiceNature;

@SynaptixComponent
@Table(name = "T_TASK_TYPE")
@Cache(readOnly = true, links = { IAssoTaskChainType.class })
public interface ITaskType extends ITracable, ICancellableEntity, INlsMessage {

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

	@Column(name = "NATURE", length = 240, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public ServiceNature getNature();

	public void setNature(ServiceNature nature);

	@Column(name = "SERVICE_CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getServiceCode();

	public void setServiceCode(String serviceCode);

	@Column(name = "DESCRIPTION", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getDescription();

	public void setDescription(String description);

	@Column(name = "CHECK_SKIPPABLE", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	public boolean isCheckSkippable();

	public void setCheckSkippable(boolean checkSkippable);

	@Column(name = "EXECUTANT_ROLE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getExecutantRole();

	public void setExecutantRole(String executantRole);

	@Column(name = "MANAGER_ROLE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getManagerRole();

	public void setManagerRole(String managerRole);

	@Column(name = "TODO_MANAGER_DURATION")
	public Duration getTodoManagerDuration();

	public void setTodoManagerDuration(Duration todoManagerDuration);

	@Column(name = "ID_TODO_FOLDER")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public IId getIdTodoFolder();

	public void setIdTodoFolder(IId idTodoFolder);

	@Column(name = "RESULT_DEPTH", nullable = false, length = 1)
	@JdbcType(JdbcTypesEnum.INTEGER)
	@DefaultValue("0")
	public int getResultDepth();

	public void setResultDepth(int resultDepth);

	@Collection(sqlTableName = "T_TODO_FOLDER", idSource = "ID_TODO_FOLDER", alias = "tf_", componentClass = ITodoFolder.class)
	public ITodoFolder getTodoFolder();

	public void setTodoFolder(ITodoFolder todoFolder);

	@Column(name = "EVENT_TYPE", length = 3)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getEventType();

	public void setEventType(String eventType);

	@Column(name = "ID_EVENT_ACTION")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public IId getIdEventAction();

	public void setIdEventAction(IId idEventAction);

	@Column(name = "CHECK_IF_EVENT_EXISTS")
	@DefaultValue("'0'")
	public boolean isCheckIfEventExists();

	public void setCheckIfEventExists(boolean checkIfEventExists);

	@Column(name = "CHECK_EVENT_EXPORT_DATA")
	@DefaultValue("'0'")
	public boolean isCheckEventExportData();

	public void setCheckEventExportData(boolean checkEventExportData);

	@Column(name = "CHECK_STOP_TASK ")
	@DefaultValue("'0'")
	public boolean isCheckStopTask();

	public void setCheckStopTask(boolean checkStopTask);
}
