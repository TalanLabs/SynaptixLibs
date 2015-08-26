package com.synaptix.taskmanager.view.descriptor;

import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;

public interface ITaskChainsManagementViewDescriptor extends ICRUDManagementViewDescriptor<ITaskChain> {

	public void setTaskChain(ITaskChain taskChain);

}
