package com.synaptix.taskmanager.urimanager.uriaction;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.ITodo;

public abstract class AbstractTodoAction implements ITodoAction {

	@Override
	public void execute(List<ITodo> todos, IView view) {
		execute(todos.get(0), view);
	}

	public abstract void execute(ITodo todo, IView view);

	@Override
	public String getText() {
		return null;
	}

	@Override
	public boolean isMultiSelectionOk() {
		return false;
	}

}
