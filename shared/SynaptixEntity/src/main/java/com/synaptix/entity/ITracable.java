package com.synaptix.entity;

import java.util.Date;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
public interface ITracable extends IComponent, IDatabaseComponentExtension {

	@Column(name = "CREATED_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public Date getCreatedDate();

	public void setCreatedDate(Date createdDate);

	@Column(name = "CREATED_BY", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCreatedBy();

	public void setCreatedBy(String createdBy);

	@Column(name = "UPDATED_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public Date getUpdatedDate();

	public void setUpdatedDate(Date updatedDate);

	@Column(name = "UPDATED_BY", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getUpdatedBy();

	public void setUpdatedBy(String updatedBy);

}
