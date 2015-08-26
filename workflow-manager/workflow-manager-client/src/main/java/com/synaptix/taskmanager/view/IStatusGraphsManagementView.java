package com.synaptix.taskmanager.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.model.IStatusGraph;

public interface IStatusGraphsManagementView extends IView {

	public void refresh();

	public void setStatusGraphs(List<IStatusGraph> statusGraphs);

}
