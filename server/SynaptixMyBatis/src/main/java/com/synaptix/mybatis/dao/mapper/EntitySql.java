package com.synaptix.mybatis.dao.mapper;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.helper.DeleteMappedStatement;
import com.synaptix.mybatis.helper.InsertMappedStatement;
import com.synaptix.mybatis.helper.UpdateMappedStatement;

public class EntitySql {

	private final SqlSessionManager sqlSessionManager;

	private InsertMappedStatement insertMappedStatement;

	private UpdateMappedStatement updateMappedStatement;

	private DeleteMappedStatement deleteMappedStatement;

	@Inject
	public EntitySql(SqlSessionManager sqlSessionManager) {
		super();

		this.sqlSessionManager = sqlSessionManager;
	}

	@Inject
	public void setInsertMappedStatement(InsertMappedStatement insertMappedStatement) {
		this.insertMappedStatement = insertMappedStatement;
	}

	@Inject
	public void setUpdateMappedStatement(UpdateMappedStatement updateMappedStatement) {
		this.updateMappedStatement = updateMappedStatement;
	}

	@Inject
	public void setDeleteMappedStatement(DeleteMappedStatement deleteMappedStatement) {
		this.deleteMappedStatement = deleteMappedStatement;
	}

	/**
	 * Insert a entity table
	 * 
	 * @param entity
	 * @return
	 */
	public <E extends IEntity> int insertEntity(E entity) {
		Class<E> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
		MappedStatement mappedStatement = insertMappedStatement.getInsertMappedStatement(entityClass);
		return sqlSessionManager.insert(mappedStatement.getId(), entity);
	}

	/**
	 * Update a entity table, not update nls column
	 * 
	 * @param entity
	 * @return
	 */
	public <E extends IEntity> int updateEntity(E entity) {
		return updateEntity(entity, false);
	}

	/**
	 * Update a entity table and update nls
	 * 
	 * @param entity
	 * @param updateNlsColumn
	 * @return
	 */
	public <E extends IEntity> int updateEntity(E entity, boolean updateNlsColumn) {
		Class<E> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
		MappedStatement mappedStatement = updateMappedStatement.getUpdateMappedStatement(entityClass, updateNlsColumn);
		return sqlSessionManager.update(mappedStatement.getId(), entity);
	}

	/**
	 * Delete entity table
	 * 
	 * @param entity
	 * @return
	 */
	public <E extends IEntity> int deleteEntity(E entity) {
		Class<E> entityClass = ComponentFactory.getInstance().getComponentClass(entity);
		MappedStatement mappedStatement = deleteMappedStatement.getDeleteMappedStatement(entityClass);
		return sqlSessionManager.delete(mappedStatement.getId(), entity);
	}
}
