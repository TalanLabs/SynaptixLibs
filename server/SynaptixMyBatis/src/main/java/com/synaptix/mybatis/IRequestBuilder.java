package com.synaptix.mybatis;

/**
 * 
 * @author Nicolas P
 * 
 */
public interface IRequestBuilder {

	/**
	 * Builds a request using a callback
	 * 
	 * @param sqlName
	 * @param prefix
	 *            prefix name in map ex : valueFilterMap
	 * @return
	 */
	public String getSqlChunk(String sqlName, String prefix);

}
