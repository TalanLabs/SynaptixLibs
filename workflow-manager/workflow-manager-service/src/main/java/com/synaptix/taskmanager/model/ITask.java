package com.synaptix.taskmanager.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.service.model.ICancellableEntity;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;

@SynaptixComponent
@Table(name = "T_TASK")
public interface ITask extends ITracable, ICancellableEntity {

	@Column(name = "STATUS", length = 240, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public TaskStatus getTaskStatus();

	public void setTaskStatus(TaskStatus taskStatus);

	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "ID_TASK_TYPE")
	public Serializable getIdTaskType();

	public void setIdTaskType(Serializable idTaskType);

	@Collection(sqlTableName = "T_TASK_TYPE", idSource = "ID_TASK_TYPE", alias = "tt_", componentClass = ITaskType.class)
	public ITaskType getTaskType();

	public void setTaskType(ITaskType taskType);

	@Column(name = "ID_CLUSTER", nullable = false)
	public Serializable getIdCluster();

	public void setIdCluster(Serializable idCluster);

	@Column(name = "ID_OBJECT", nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Serializable getIdObject();

	public void setIdObject(Serializable idObject);

	@Column(name = "NATURE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public ServiceNature getNature();

	public void setNature(ServiceNature nature);

	@Column(name = "SERVICE_CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getServiceCode();

	public void setServiceCode(String serviceCode);

	@Column(name = "CHECK_SKIPPABLE", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckSkippable();

	public void setCheckSkippable(boolean checkSkippable);

	@Column(name = "CHECK_ERROR", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckError();

	public void setCheckError(boolean checkError);

	@Column(name = "ERROR_MESSAGE", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getErrorMessage();

	public void setErrorMessage(String errorMessage);

	@Column(name = "EXECUTANT_ROLE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getExecutantRole();

	public void setExecutantRole(String executantRole);

	@Column(name = "MANAGER_ROLE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getManagerRole();

	public void setManagerRole(String managerRole);

	@Column(name = "ID_PARENT")
	public Serializable getIdParentTask();

	public void setIdParentTask(Serializable idParentTask);

	@Column(name = "NEXT_STATUS", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getNextStatus();

	public void setNextStatus(String nextStatus);

	@Column(name = "CHECK_GROUP", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckGroup();

	public void setCheckGroup(boolean checkGroup);

	@Column(name = "ID_PREVIOUS_UPDATE_STATUS_TASK")
	public Serializable getIdPreviousUpdateStatusTask();

	public void setIdPreviousUpdateStatusTask(Serializable idPreviousUpdateStatusTask);

	@Column(name = "CHECK_TODO_EXECUTANT_CREATED", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckTodoExecutantCreated();

	public void setCheckTodoExecutantCreated(boolean checkTodoExecutantCreated);

	@Column(name = "CHECK_TODO_MANAGER_CREATED", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckTodoManagerCreated();

	public void setCheckTodoManagerCreated(boolean checkTodoManagerCreated);

	@Column(name = "FIRST_ERROR_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public LocalDateTime getFirstErrorDate();

	public void setFirstErrorDate(LocalDateTime firstErrorDateDate);

	@Column(name = "TODO_MANAGER_DURATION")
	public Duration getTodoManagerDuration();

	public void setTodoManagerDuration(Duration todoManagerDuration);

	@Column(name = "START_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public Date getStartDate();

	public void setStartDate(Date startDate);

	@Column(name = "END_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public Date getEndDate();

	public void setEndDate(Date endDate);

	/**
	 * Set to true when the user has manually validated the task.
	 */
	@Column(name = "CHECK_USER_VALIDATION", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean isCheckUserValidation();

	public void setCheckUserValidation(boolean checkUserValidation);

	@Column(name = "RESULT_STATUS", length = 20)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getResultStatus();

	public void setResultStatus(String resultStatus);

	@Column(name = "RESULT_DESC", length = 1024)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getResultDesc();

	public void setResultDesc(String resultDesc);

	@Column(name = "RESULT_DETAIL", length = 2000)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getResultDetail();

	public void setResultDetail(String resultDetail);

}
