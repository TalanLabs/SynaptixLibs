package com.synaptix.deployer.client.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.model.IDatabaseQuery;

public interface IDatabaseCheckerView extends IView {

	/**
	 * Sets the list of database queries
	 * 
	 * @param databaseQueryList
	 */
	public void setDatabaseQueryList(List<IDatabaseQuery> databaseQueryList);

	/**
	 * Shows the database query details
	 * 
	 * @param databaseQuery
	 */
	public void showDatabaseQuery(IDatabaseQuery databaseQuery);

	/**
	 * Shows the database query result
	 */
	public void showDatabaseQueryResult(boolean valid, String databaseQueryResult);

	/**
	 * Updates the tree
	 */
	public void updateTree();

}
