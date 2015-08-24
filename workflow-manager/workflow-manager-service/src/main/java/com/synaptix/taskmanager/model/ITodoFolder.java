package com.synaptix.taskmanager.model;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_TODO_FOLDER")
public interface ITodoFolder extends ITracable, IEntity, INlsMessage {

	@Column(name = "COMMENTS", length = 512)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getComments();

	public void setComments(String comments);

	@Column(name = "CHECK_RESTRICTED", length = 1)
	@JdbcType(JdbcTypesEnum.CHAR)
	public boolean isRestricted();

	public void setRestricted(boolean restricted);

}
