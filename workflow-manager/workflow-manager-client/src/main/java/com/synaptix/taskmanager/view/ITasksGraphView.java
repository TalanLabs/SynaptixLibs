package com.synaptix.taskmanager.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;

public interface ITasksGraphView extends IView {

	public void setTasks(List<ITask> tasks, List<IAssoTaskPreviousTask> assoTaskPreviousTasks);

	public void showView();

}
