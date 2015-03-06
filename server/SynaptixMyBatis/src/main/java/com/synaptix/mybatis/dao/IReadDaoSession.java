package com.synaptix.mybatis.dao;

/**
 * A light dao session with no commit which is only used for read access
 * 
 */
public interface IReadDaoSession {

	/**
	 * Begin sql session
	 */
	public void begin();

	/**
	 * End sql session, rollback if not commit
	 */
	public void end();

	/**
	 * Get a mapper class
	 * 
	 * @param type
	 * @return
	 */
	public <T> T getMapper(Class<T> type);

}
