package com.synaptix.taskmanager.objecttype;

import com.synaptix.taskmanager.controller.IPreviewTaskManagerContextDialogController;
import com.synaptix.taskmanager.model.ITaskObject;

public interface IObjectType<E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> {

	public Class<E> getStatusClass();

	public Class<F> getTaskObjectClass();

	public String getStatusMeaning(E value);

	public String getName();

	public String getShortName();

	public Class<ManagerRole> getManagerRoleClass();

	public String getManagerRoleMeaning(ManagerRole value);

	public Class<ExecutantRole> getExecutantRoleClass();

	public String getExecutantRoleMeaning(ExecutantRole value);

	public String getTodoMeaning(String value);

	/**
	 * Create a preview context template editor dialog controller
	 * 
	 * @return
	 */
	public IPreviewTaskManagerContextDialogController newPreviewContextTemplateEditorDialogController();

}
