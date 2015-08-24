package com.synaptix.taskmanager.model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;

@SynaptixComponent
public interface ITodoFolderListView extends IEntity {

	public String getMeaning();

	public void setMeaning(String meaning);

	/**
	 * Number of todos in the folder
	 */
	public int getTodoNumber();

	public void setTodoNumber(int todoNumber);

	/**
	 * True if this is the calculated folder containing all todos.
	 */
	public boolean isTotal();

	public void setTotal(boolean total);

}
