/**
 * 
 */
package com.synaptix.redpepper.commons.init;

import java.io.Serializable;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.service.filter.RootNode;

/**
 * @author E413544
 * 
 */
public interface ITestManager {

	/**
	 * Empty the test database and cache.
	 */
	public abstract void initDb();

	/**
	 * Clean and close the test database.
	 */
	public abstract void closeDb();

	/**
	 * Get instance for a class.
	 * 
	 * @param <T>
	 * 
	 * @param serviceClass
	 * @return
	 */
	public abstract <T> T getClassInstance(Class<T> serviceClass);

	public abstract Serializable addEntity(IEntity entity);

	public abstract void beginTransaction();

	public abstract void endTransaction();

	public abstract IComponent selectOne(Class<? extends IComponent> componentClass, RootNode rootNode);

	/**
	 * @param tableName
	 * @param valueMap
	 * @return
	 * @throws Exception
	 */
	public abstract int insertType(String tableName, Map<String, String> valueMap) throws Exception;

	/**
	 * @param <E>
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public abstract <E extends IEntity> int insertEntity(E entity) throws Exception;

	public abstract void stopXMPP(boolean b);

	public abstract void startXMPP(boolean b);

}