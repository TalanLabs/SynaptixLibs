package com.synaptix.taskmanager.model;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IAssoTaskPreviousTask extends IComponent {

	public Serializable getIdTask();

	public void setIdTask(Serializable idTask);

	public Serializable getIdPreviousTask();

	public void setIdPreviousTask(Serializable idPreviousTask);

}
