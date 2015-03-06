package com.synaptix.deployer.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.synaptix.deployer.model.IConstraint;
import com.synaptix.deployer.model.ITableColumn;
import com.synaptix.deployer.model.SqlObject;

public interface DeployerMapper {

	@ResultType(String.class)
	@Select("select SYS_CONTEXT ('USERENV', 'DB_NAME') from dual")
	public String getDbName();

	public List<ITableColumn> getTableAndColumns(@Param("dbName") String dbName);

	public List<IConstraint> getConstraints(@Param("dbName") String dbName);

	public void callDeactivateConstraint();

	public List<String> getDDL(@Param("dbName") String dbName, @Param("sqlObjects") SqlObject... sqlObjects);

	public List<String> getConstraintDDL(@Param("dbName") String dbName);

}
