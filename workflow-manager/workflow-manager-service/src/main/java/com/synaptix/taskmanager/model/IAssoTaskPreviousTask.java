package com.synaptix.taskmanager.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IId;

@SynaptixComponent
public interface IAssoTaskPreviousTask extends IComponent {

	public IId getIdTask();

	public void setIdTask(IId idTask);

	public IId getIdPreviousTask();

	public void setIdPreviousTask(IId idPreviousTask);

}
