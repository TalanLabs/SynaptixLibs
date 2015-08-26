package com.synaptix.taskmanager.service;

import com.synaptix.taskmanager.dao.mapper.TodoMapper;


public class TodoServerService extends AbstractSimpleService implements ITodoService {

	private TodoMapper getTodoMapper() {
		return getDaoSession().getMapper(TodoMapper.class);
	}

//	@Override
//	@Transactional
//	public List<ITodoFolderListView> findTodoFolders(Serializable idOwnerCenter) {
//		List<ITodoFolderListView> todoFolders = getDaoSession().getMapper(TodoMapper.class).findTodoFolders(idOwnerCenter, getUserSession().getCurrentIdUser());
//		return todoFolders != null ? todoFolders : Collections.<ITodoFolderListView> emptyList();
//	}

//	@Override
//	@Transactional
//	public List<ITodo> findExecutantTodosByIdUser(Serializable userId) {
//		return getTodoMapper().findExecutantTodosByIdUser(userId);
//	}
//
//	@Override
//	@Transactional
//	public List<ITodo> selectTodoList(Serializable idOwnerCenter, ITodoFolderListView todoFolder, boolean lastTodosFirst, String descrLike) {
//		return getTodoMapper().selectTodoList(idOwnerCenter, todoFolder, getUserSession().getCurrentIdUser(), lastTodosFirst, "%" + descrLike + "%");
//	}
//
//	@Override
//	@Transactional
//	public int countAllTodo(Serializable idOwnerCenter) {
//		return getTodoMapper().countAllTodo(idOwnerCenter, getUserSession().getCurrentIdUser());
//	}
//
//	@Transactional
//	@Override
//	public List<ITodo> selectTodoList(Serializable idContextCenter, ITodoFolderListView selectedTodoFolder, List<Serializable> idObjects) {
//		return getTodoMapper().selectTodoListForObjects(idContextCenter, selectedTodoFolder, getUserSession().getCurrentIdUser(), idObjects);
//	}
}
