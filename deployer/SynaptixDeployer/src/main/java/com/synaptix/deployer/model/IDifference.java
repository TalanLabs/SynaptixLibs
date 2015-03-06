package com.synaptix.deployer.model;

import java.util.List;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IDifference extends IComponent {

	public String getTableName();

	public void setTableName(String tableName);

	public String getObjectName();

	public void setObjectName(String objectName);

	public List<IDifferenceAttribute> getDifferenceAttributeList();

	public void setDifferenceAttributeList(List<IDifferenceAttribute> differenceAttributeList);

}
