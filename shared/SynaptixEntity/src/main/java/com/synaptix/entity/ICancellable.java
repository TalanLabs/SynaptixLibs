package com.synaptix.entity;

import java.util.Date;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
public interface ICancellable extends IComponent, IDatabaseComponentExtension {

	@Column(name = "CHECK_CANCEL", nullable = false)
	@JdbcType(JdbcTypesEnum.CHAR)
	@DefaultValue("'0'")
	public boolean getCheckCancel();

	public void setCheckCancel(boolean checkCancel);

	@Column(name = "CANCEL_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	public Date getCancelDate();

	public void setCancelDate(Date cancelDate);

	@Column(name = "CANCEL_BY", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	public String getCancelBy();

	public void setCancelBy(String cancelBy);

}
