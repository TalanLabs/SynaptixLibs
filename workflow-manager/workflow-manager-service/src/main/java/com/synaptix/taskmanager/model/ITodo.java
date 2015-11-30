package com.synaptix.taskmanager.model;

import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.taskmanager.model.domains.TodoOwner;
import com.synaptix.taskmanager.model.domains.TodoStatus;

/**
 * Todo object that will appear in a todo list when a manual action is needed or when a task failed.
 * This interface must be extended to add fields specific to your project, such as the owner of the todo (person or group responsible for
 * prcessing the todo).
 */
@SynaptixComponent
@Table(name = "T_TODO")
public interface ITodo extends ITracable, IEntity {

	@Column(name = "ID_TASK")
	IId getIdTask();

	void setIdTask(IId idTask);

	@Collection(sqlTableName = "T_TASK", idSource = "ID_TASK", alias = "t_", componentClass = ITask.class)
	ITask getTask();

	void setTask(ITask task);

	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	Class<? extends ITaskObject<?>> getObjectType();

	void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "ID_OBJECT", nullable = false)
	IId getIdObject();

	void setIdObject(IId idObject);

	@Column(name = "OWNER", length = 240, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	TodoOwner getOwner();

	void setOwner(TodoOwner owner);

	@Column(name = "URI", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	URI getUri();

	void setUri(URI uri);

	@Column(name = "CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getCode();

	void setCode(String code);

	@Column(name = "STATUS", length = 32)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	TodoStatus getStatus();

	void setStatus(TodoStatus status);

	@Column(name = "DESCRIPTION", length = 512)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getDescription();

	void setDescription(String description);

	@Column(name = "ID_TODO_FOLDER")
	IId getIdTodoFolder();

	void setIdTodoFolder(IId idTodoFolder);

	@Collection(sqlTableName = "T_TODO_FOLDER", idSource = "ID_TODO_FOLDER", alias = "tf_", componentClass = ITodoFolder.class)
	ITodoFolder getTodoFolder();

	void setTodoFolder(ITodoFolder todoFolder);

}
