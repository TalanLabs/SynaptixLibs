package com.synaptix.taskmanager.service;

import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.model.ITodo;

/**
 * Services for todo management that must be implemented by your project.
 */
public interface ITodoService {
	/**
	 * Creates a todo for the owner and contact in parameter.
	 * Only fields specific to your project must be filled.
	 *
	 * @param ownerEntity   Owner of the todo: generally a user or a group that will see the todo and have to do it.
	 * @param contactEntity Contact, user or group that the owner can contact if needed.
	 * @return ITodo, does not need to be saved, it will be completed and saved by the task manager.
	 */
	ITodo createTodo(IEntity ownerEntity, IEntity contactEntity);
}
