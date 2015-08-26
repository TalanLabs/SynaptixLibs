package com.synaptix.taskmanager.model;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;
import com.synaptix.taskmanager.model.domains.TodoOwner;
import com.synaptix.taskmanager.model.domains.TodoStatus;

@SynaptixComponent
@Table(name = "T_TODO")
public interface ITodo extends ITracable, IEntity {

	@Column(name = "ID_TASK")
	public Serializable getIdTask();

	public void setIdTask(Serializable idTask);

	@Collection(sqlTableName = "T_TASK", idSource = "ID_TASK", alias = "t_", componentClass = ITask.class)
	public ITask getTask();

	public void setTask(ITask task);

	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	@Column(name = "ID_OBJECT", nullable = false)
	public Serializable getIdObject();

	public void setIdObject(Serializable idObject);

	@Column(name = "OWNER", length = 240, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public TodoOwner getOwner();

	public void setOwner(TodoOwner owner);

//	@Column(name = "ID_OWNER_CENTER")
//	public Serializable getIdOwnerCenter();
//
//	public void setIdOwnerCenter(Serializable idOwnerCenter);

//	@Collection(sqlTableName = "T_CENTER", idSource = "ID_OWNER_CENTER", alias = "oc_", componentClass = ICenter.class)
//	public ICenter getOwnerCenter();
//
//	public void setOwnerCenter(ICenter ownerCenter);
//
//	@Column(name = "ID_OWNER_THIRD_PARTY", length = 512)
//	@JdbcType(JdbcTypesEnum.VARCHAR)
//	public Serializable getIdOwnerThirdParty();
//
//	public void setIdOwnerThirdParty(Serializable idOwnerThirdParty);
//
//	@Collection(sqlTableName = "T_THIRD_PARTY", idSource = "ID_OWNER_THIRD_PARTY", alias = "cotp_", componentClass = IThirdParty.class)
//	public IThirdParty getOwnerThirdParty();
//
//	public void setOwnerThirdParty(IThirdParty ownerThirdParty);
//
//		@Column(name = "ID_CONTACT_CENTER")
//	public Serializable getIdContactCenter();
//
//	public void setIdContactCenter(Serializable idContactCenter);
//
//	@Collection(sqlTableName = "T_CENTER", idSource = "ID_CONTACT_CENTER", alias = "coc_", componentClass = ICenter.class)
//	public ICenter getContactCenter();
//
//	public void setContactCenter(ICenter contactCenter);
//
//	@Column(name = "ID_CONTACT_THIRD_PARTY", length = 512)
//	@JdbcType(JdbcTypesEnum.VARCHAR)
//	public Serializable getIdContactThirdParty();
//
//	public void setIdContactThirdParty(Serializable idContactThirdParty);
//
//	@Collection(sqlTableName = "T_THIRD_PARTY", idSource = "ID_CONTACT_THIRD_PARTY", alias = "cotp_", componentClass = IThirdParty.class)
//	public IThirdParty getContactThirdParty();
//
//	public void setContactThirdParty(IThirdParty contactThirdParty);

	@Column(name = "URI", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public URI getUri();

	public void setUri(URI uri);

	@Column(name = "CODE", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCode();

	public void setCode(String code);

	@Column(name = "STATUS", length = 32)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public TodoStatus getStatus();

	public void setStatus(TodoStatus status);

	@Column(name = "DESCRIPTION", length = 512)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getDescription();

	public void setDescription(String description);

	@Column(name = "ID_TODO_FOLDER")
	public Serializable getIdTodoFolder();

	public void setIdTodoFolder(Serializable idTodoFolder);

	@Collection(sqlTableName = "T_TODO_FOLDER", idSource = "ID_TODO_FOLDER", alias = "tf_", componentClass = ITodoFolder.class)
	public ITodoFolder getTodoFolder();

	public void setTodoFolder(ITodoFolder todoFolder);

}
