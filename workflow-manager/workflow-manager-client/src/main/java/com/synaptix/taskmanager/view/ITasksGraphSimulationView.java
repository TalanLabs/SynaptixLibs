package com.synaptix.taskmanager.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskManagerContext;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITasksGraphSimulationView extends IView {

	public void updateSimulation(List<IStatusGraph> statusGraphs, List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias, ITaskManagerContext context,
			Class<? extends ITaskObject<?>> objectType);

	public void showView();

}
