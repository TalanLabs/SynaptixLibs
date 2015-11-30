package com.synaptix.taskmanager.model;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.entity.IId;

/**
 * Created by E413544 on 16/04/2015.
 */
public class TasksLists {

	public List<IId> getIdTasksToRemove() {
		return idTasksToRemove;
	}

	public void setIdTasksToRemove(List<IId> idTasksToRemove) {
		this.idTasksToRemove = idTasksToRemove;
	}

	public List<ITask> getNewTasksToDo() {
		return newTasksToDo;
	}

	public void setNewTasksToDo(List<ITask> newTasksToDo) {
		this.newTasksToDo = newTasksToDo;
	}

	private List<IId> idTasksToRemove;
	private List<ITask> newTasksToDo;

	public TasksLists() {
		idTasksToRemove = new ArrayList<IId>();
		newTasksToDo = new ArrayList<ITask>();
	}
}
