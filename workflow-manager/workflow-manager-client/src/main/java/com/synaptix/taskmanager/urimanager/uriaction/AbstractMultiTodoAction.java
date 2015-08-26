package com.synaptix.taskmanager.urimanager.uriaction;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.ITodo;

public abstract class AbstractMultiTodoAction implements ITodoAction {

	public AbstractMultiTodoAction() {
	}

	@Override
	public void execute(List<ITodo> todos, IView view) {

	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public boolean isMultiSelectionOk() {
		return true;
	}

}
