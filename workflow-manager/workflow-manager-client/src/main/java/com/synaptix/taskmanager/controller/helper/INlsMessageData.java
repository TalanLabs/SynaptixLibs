package com.synaptix.taskmanager.controller.helper;

import java.io.Serializable;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

@SynaptixComponent
public interface INlsMessageData extends IComponent, IDatabaseComponentExtension {

	@Column(name = "OBJECT_TYPE", nullable = false)
	public Class<?> getObjectType();

	public void setObjectType(Class<?> objectType);

	@Column(name = "ID_OBJECT", nullable = false)
	@EqualsKey
	public Serializable getIdObject();

	public void setIdObject(Serializable idObject);

	@Column(name = "DEFAULT_MEANING", length = 240)
	public String getDefaultMeaning();

	public void setDefaultMeaning(String defaultMeaning);

	@Column(name = "MY_MEANING", length = 240)
	public String getMyMeaning();

	public void setMyMeaning(String myMeaning);

	@Column(name = "MEANING", length = 240)
	public String getMeaning();

	public void setMeaning(String meaning);

	@Column(name = "COLUMN_NAME", length = 240)
	public String getColumnName();

	public void setColumnName(String columnName);
}
