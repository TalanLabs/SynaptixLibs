package com.synaptix.taskmanager.model;

import javax.persistence.Column;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.JdbcTypesEnum;

/**
 * 
 * @param <E>
 *            Object statuses enumeration.
 */
@SynaptixComponent
public interface IStatusEntity<E extends Enum<E>> extends IEntity {

	@Column(name = "STATUS", length = 16, nullable = false)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public E getStatus();

	/**
	 * When an entity is used with the task manager, this method should never be called directly. Instead, use setNextStatus().
	 */
	public void setStatus(E status);

}
