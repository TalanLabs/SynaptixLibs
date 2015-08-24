package com.synaptix.taskmanager.urimanager.uriaction;

import java.awt.Component;
import java.net.URI;
import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.taskmanager.model.ITodo;

public interface IURIClientManager {

	/**
	 * Accept uri
	 * 
	 * @param uri
	 * @return
	 */
	public boolean acceptUri(URI uri);

	/**
	 * Process default action for todo
	 * 
	 * @param parentView
	 * @param todo
	 */
	public void processDefaultAction(IView parentView, ITodo todo);

	public List<ITodoAction> getActions(ITodo todo);

	/**
	 * Builds a JPanel with the details of the object linked to the todo.
	 */
	public void getDetailsPanel(ITodo todo, IView view, IResultCallback<Component> callback);

}
