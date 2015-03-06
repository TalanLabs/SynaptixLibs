package com.synaptix.deployer.model;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IConstraint extends IComponent {

	@Column(name = "TABLE_NAME")
	public String getTableName();

	public void setTableName(String tableName);

	@Column(name = "CONSTRAINT_NAME")
	public String getConstraintName();

	public void setConstraintName(String constraintName);

	@Column(name = "CONSTRAINT_TYPE")
	public ConstraintTypeEnum getConstraintType();

	public void setConstraintType(ConstraintTypeEnum constraintType);

	@Column(name = "SEARCH_CONDITION")
	public String getSearchCondition();

	public void setSearchCondition(String searchCondition);

	@Column(name = "R_COLUMNS")
	public String getColumns();

	public void setColumns(String columns);

}
