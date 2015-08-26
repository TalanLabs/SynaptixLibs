package com.synaptix.taskmanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E413544 on 16/04/2015.
 */
public class TasksLists {

	public List<Serializable> getIdTasksToRemove() {
		return idTasksToRemove;
	}

	public void setIdTasksToRemove(List<Serializable> idTasksToRemove) {
		this.idTasksToRemove = idTasksToRemove;
	}

	public List<ITask> getNewTasksToDo() {
		return newTasksToDo;
	}

	public void setNewTasksToDo(List<ITask> newTasksToDo) {
		this.newTasksToDo = newTasksToDo;
	}

	private List<Serializable> idTasksToRemove;
	private List<ITask> newTasksToDo;

	public TasksLists() {
		idTasksToRemove = new ArrayList<Serializable>();
		newTasksToDo = new ArrayList<ITask>();
	}
}
