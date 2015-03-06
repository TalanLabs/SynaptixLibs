package com.synaptix.deployer.model;

import java.util.List;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IDifferentTable extends IComponent {

	public String getTableName();

	public void setTableName(String tableName);

	public List<ITableColumn> getDifferentTableColumnList();

	public void setDifferentTableColumnList(List<ITableColumn> differentTableColumnList);

}
