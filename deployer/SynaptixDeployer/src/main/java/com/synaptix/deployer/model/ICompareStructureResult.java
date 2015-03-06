package com.synaptix.deployer.model;

import java.util.List;
import java.util.Map;

import com.synaptix.common.model.Binome;
import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface ICompareStructureResult extends IComponent {

	public List<Binome<ISynaptixDatabaseSchema, String>> getMissingTableList();

	public void setMissingTableList(List<Binome<ISynaptixDatabaseSchema, String>> missingTableList);

	public List<ITableColumn> getTableColumnDb1();

	public void setTableColumnDb1(List<ITableColumn> tableColumnDb1);

	public List<ITableColumn> getTableColumnDb2();

	public void setTableColumnDb2(List<ITableColumn> tableColumnDb2);

	public Map<String, List<ITableColumn>> getDifferenceMapDb1();

	public void setDifferenceMapDb1(Map<String, List<ITableColumn>> differenceMapDb1);

	public Map<String, List<ITableColumn>> getDifferenceMapDb2();

	public void setDifferenceMapDb2(Map<String, List<ITableColumn>> differenceMapDb2);

	// public List<IDifference> getDifferenceList();

	// public void setDifferenceList(List<IDifference> differenceList);

}
