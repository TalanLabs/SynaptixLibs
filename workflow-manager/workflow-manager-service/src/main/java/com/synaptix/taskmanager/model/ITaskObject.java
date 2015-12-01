package com.synaptix.taskmanager.model;

import javax.persistence.Column;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IId;
import com.synaptix.entity.IWithError;
import com.synaptix.entity.extension.JdbcTypesEnum;

/**
 * 
 * @param <E>
 *            Object statuses enumeration.
 */
@SynaptixComponent
public interface ITaskObject<E extends Enum<E>> extends IStatusEntity<E>, IWithError {

	@Column(name = "ID_CLUSTER")
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public IId getIdCluster();

	public void setIdCluster(IId idCluster);

}
