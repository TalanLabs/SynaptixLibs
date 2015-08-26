package com.synaptix.taskmanager.urimanager.uriaction;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.ITodo;

public interface ITodoAction {

	public void execute(List<ITodo> todos, IView view);

	public String getText();

	/**
	 * True if action executable on multi selection
	 * 
	 * @return boolean
	 */
	public boolean isMultiSelectionOk();

}
