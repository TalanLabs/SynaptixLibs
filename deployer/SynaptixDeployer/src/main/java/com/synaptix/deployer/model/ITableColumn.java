package com.synaptix.deployer.model;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface ITableColumn extends IComponent {

	@Column(name = "TABLE_NAME")
	@EqualsKey
	public String getTableName();

	public void setTableName(String tableName);

	@Column(name = "COLUMN_NAME")
	@EqualsKey
	public String getColumnName();

	public void setColumnName(String columnName);

	@Column(name = "DATA_TYPE")
	public String getType();

	public void setType(String type);

	@Column(name = "PRECISION")
	public Integer getPrecision();

	public void setPrecision(Integer precision);

	@Column(name = "DATA_LENGTH")
	public Integer getDataLength();

	public void setDataLength(Integer dataLength);

	@Column(name = "DATA_SCALE")
	public Integer getDataScale();

	public void setDataScale(Integer dataScale);

	@Column(name = "DATA_DEFAULT")
	public String getDataDefault();

	public void setDataDefault(String dataDefault);

	@Column(name = "NULLABLE")
	public Boolean getNullable();

	public void setNullable(Boolean nullable);

	@Column(name = "COLUMN_ID")
	public Integer getColumnId();

	public void setColumnId(Integer columnId);

}
