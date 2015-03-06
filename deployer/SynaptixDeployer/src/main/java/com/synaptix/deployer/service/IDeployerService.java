package com.synaptix.deployer.service;

import java.util.List;

import com.synaptix.deployer.model.ICompareConstraintResult;
import com.synaptix.deployer.model.ICompareStructureResult;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public interface IDeployerService {

	/**
	 * Get the name of the database
	 * 
	 * @param database
	 * @return
	 */
	public String getDbName(ISynaptixDatabaseSchema database);

	/**
	 * Compare structure of two databases
	 * 
	 * @param database1
	 * @param database2
	 * @param ignoreTableList
	 * @return
	 */
	public ICompareStructureResult compareStructure(ISynaptixDatabaseSchema database1, ISynaptixDatabaseSchema database2, List<String> ignoreTableList);

	/**
	 * Compare constraints of two databases
	 * 
	 * @param database1
	 * @param database2
	 * @param ignoreTableList
	 * @return
	 */
	public ICompareConstraintResult compareConstraint(ISynaptixDatabaseSchema database1, ISynaptixDatabaseSchema database2, List<String> ignoreTableList);

	/**
	 * Get DDL
	 */
	public List<String> getDDL(ISynaptixDatabaseSchema database, String login, char[] password);

}
