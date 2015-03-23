package com.synaptix.mybatis.dao.exceptions;

/**
 * Exception raised by a statement when trying to save an entity with a different version than the one given
 */
public class VersionConflictDaoException extends DaoException {

	private static final long serialVersionUID = -7876937198655312257L;

}
