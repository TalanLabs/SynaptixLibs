package com.synaptix.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IBusinessComponentExtension;
import com.synaptix.entity.extension.ICacheComponentExtension;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

@SynaptixComponent
public interface IEntity extends IComponent, IDatabaseComponentExtension, IBusinessComponentExtension, ICacheComponentExtension {

	@EqualsKey(nullEquals = false)
	@Id
	@Column(name = "ID", nullable = false)
	public IId getId();

	public void setId(IId id);

	@Version
	@Column(name = "VERSION", precision = 10, nullable = false)
	public Integer getVersion();

	public void setVersion(Integer version);

}
