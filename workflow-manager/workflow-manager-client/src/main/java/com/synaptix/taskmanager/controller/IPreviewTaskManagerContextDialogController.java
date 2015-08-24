package com.synaptix.taskmanager.controller;

import com.synaptix.client.view.IView;

public interface IPreviewTaskManagerContextDialogController {

	public Object newContext(IView parentView);

	/**
	 * Show dialog and edit context
	 * 
	 * @param parentView
	 * @return
	 */
	public Object edit(IView parentView, Object taskManagerContext);

}
