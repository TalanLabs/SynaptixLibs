package com.synaptix.mybatis.dao;

public interface IDaoSessionExt extends IDaoSession {

	/**
	 * Set check super transaction in session
	 * 
	 * @param superTransaction
	 */
	public void setCheckSuperTransactionInSession(boolean checkSuperTransaction);

	public boolean isCheckSuperTransactionInSession();

	/**
	 * Set check throw exception for no data in session, insert, update, cancel
	 * 
	 * @param throwExceptionForNoData
	 */
	public void setCheckVersionConflictDaoExceptionInSession(boolean checkThrowExceptionForNoData);

	public boolean isCheckThrowExceptionForNoDataInSession();

}
