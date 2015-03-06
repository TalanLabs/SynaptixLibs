package com.synaptix.deployer.client.view;

import com.synaptix.client.view.IView;
import com.synaptix.common.model.Binome;
import com.synaptix.deployer.client.compare.CompareNode;
import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public interface ICompareView extends IView {

	/**
	 * Sets compare result
	 * 
	 * @param db1
	 * @param db2
	 * @param result
	 */
	public void setCompareResult(ISynaptixDatabaseSchema db1, ISynaptixDatabaseSchema db2, Binome<ICompareStructureResult, ICompareConstraintResult> result);

	/**
	 * Explorer a node
	 * 
	 * @param node
	 */
	public void exploreNode(CompareNode node);

}
