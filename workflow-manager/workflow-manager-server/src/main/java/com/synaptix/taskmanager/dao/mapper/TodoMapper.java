package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.ITodoFolderListView;


public interface TodoMapper {

	void deleteTasksTodo(@Param("idTask") Serializable idTask);

	void deleteTasksTodos(@Param("idList") List<Serializable> idTaskList);

	List<ITodo> selectTodoList(@Param("idOwnerCenter") Serializable idOwnerCenter, @Param("todoFolder") ITodoFolderListView todoFolder, @Param("idUser") Serializable idUser,
			@Param("lastTodosFirst") boolean lastTodosFirst, @Param("descrLike") String descrLike);

	void updateDescription(@Param("description") String objectDescription, @Param("idObject") Serializable idObject);

	void deleteTaskErrors(@Param("idTasks") List<Serializable> idTasks);

	List<ITodo> selectTodoListForObjects(@Param("idContextCenter") Serializable idContextCenter, @Param("todoFolder") ITodoFolderListView todoFolder, @Param("idUser") Serializable idUser,
			@Param("idObjects") List<Serializable> idObjects);

	/**
	 * Set todo status on todos with task id = idTask
	 */
	void updatePendingTodos(@Param("idTask") Serializable idTask);

	void deleteObjectTodos(@Param("idObject") Serializable id);
}
