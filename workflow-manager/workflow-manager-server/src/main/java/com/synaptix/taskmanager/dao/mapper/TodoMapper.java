package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.ITodoFolderListView;


public interface TodoMapper {

	void deleteTasksTodo(@Param("idTask") IId idTask);

	void deleteTasksTodos(@Param("idList") List<IId> idTaskList);

	List<ITodo> selectTodoList(@Param("idOwnerCenter") IId idOwnerCenter, @Param("todoFolder") ITodoFolderListView todoFolder, @Param("idUser") IId idUser,
			@Param("lastTodosFirst") boolean lastTodosFirst, @Param("descrLike") String descrLike);

	void updateDescription(@Param("description") String objectDescription, @Param("idObject") IId idObject);

	void deleteTaskErrors(@Param("idTasks") List<IId> idTasks);

	List<ITodo> selectTodoListForObjects(@Param("idContextCenter") IId idContextCenter, @Param("todoFolder") ITodoFolderListView todoFolder, @Param("idUser") IId idUser,
			@Param("idObjects") List<IId> idObjects);

	/**
	 * Set todo status on todos with task id = idTask
	 */
	void updatePendingTodos(@Param("idTask") IId idTask);

	void deleteObjectTodos(@Param("idObject") IId id);
}
