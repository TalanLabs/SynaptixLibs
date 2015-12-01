package com.synaptix.entity;

import javax.persistence.Column;
import javax.persistence.Table;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
@Table(name = "T_ERROR")
public interface IErrorEntity extends IEntity {

	@Column(name = "CODE", length = 40)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getCode();

	void setCode(String code);

	@Column(name = "TYPE", length = 1)
	@JdbcType(JdbcTypesEnum.CHAR)
	String getType();

	void setType(String type);

	@Column(name = "LABEL", length = 255)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getLabel();

	void setLabel(String label);

	@Column(name = "ID_OBJECT")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	IId getIdObject();

	void setIdObject(IId idObject);

	@Column(name = "OBJECT_TYPE", length = 512, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	Class<? extends IWithError> getObjectType();

	void setObjectType(Class<? extends IWithError> objectType);

	@Column(name = "ATTRIBUTE", length = 127)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getAttribute();

	void setAttribute(String attribute);

	@Column(name = "VALUE", length = 255)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getValue();

	void setValue(String value);

	@Column(name = "ID_TASK")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	IId getIdTask();

	void setIdTask(IId idTask);

	String getCodeMeaning();

	void setCodeMeaning(String codeMeaning);

	String getAttributeMeaning();

	void setAttributeMeaning(String attributeMeaning);

}
