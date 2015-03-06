package com.synaptix.deployer.model;

import java.util.List;

import com.synaptix.common.model.Binome;
import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface ICompareConstraintResult extends IComponent {

	public List<Binome<ISynaptixDatabaseSchema, IConstraint>> getMissingConstraintList();

	public void setMissingConstraintList(List<Binome<ISynaptixDatabaseSchema, IConstraint>> missingConstraintList);

	public List<IDifference> getDifferenceList();

	public void setDifferenceList(List<IDifference> differenceList);

}
