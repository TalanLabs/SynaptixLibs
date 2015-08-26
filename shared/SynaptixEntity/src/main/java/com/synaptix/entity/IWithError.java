package com.synaptix.entity;

import java.util.List;

import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IWithError extends IEntity {

	@Collection(sqlTableName = "T_ERROR", idSource = "ID", idTarget = "ID_OBJECT", alias = "e_", componentClass = IErrorEntity.class)
	public List<IErrorEntity> getErrorEntities();

	public void setErrorEntities(List<IErrorEntity> errors);
}
