package com.synaptix.taskmanager.service;

public interface ITodoService {

//	public List<ITodoFolderListView> findTodoFolders(Serializable idOwnerCenter);

	/**
	 * Find all Todos which correspond to the executant third party
	 */
//	public List<ITodo> findExecutantTodosByIdUser(Serializable idUser);

	/**
	 * @param todoFolder
	 *            if null <=> see all, if no id <=> see without todo folder
	 */
//	public List<ITodo> selectTodoList(Serializable idOwnerCenter, ITodoFolderListView todoFolder, boolean lastTodosFirst, String uriLike);

	/**
	 * Count all todo for given center, grouped by id_task
	 */
//	public int countAllTodo(Serializable idOwnerCenter);

//	public List<ITodo> selectTodoList(Serializable idContextCenter, ITodoFolderListView selectedTodoFolder, List<Serializable> idObjects);

}
