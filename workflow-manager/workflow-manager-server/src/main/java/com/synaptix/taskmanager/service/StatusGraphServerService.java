package com.synaptix.taskmanager.service;

import java.util.List;

import com.google.inject.Inject;
import com.synaptix.service.IComponentService;
import com.synaptix.service.filter.builder.RootNodeBuilder;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.StatusGraphFields;

public class StatusGraphServerService extends AbstractSimpleService implements IStatusGraphService {

	@Inject
	private IComponentService componentService;

	@Override
	public List<IStatusGraph> findStatusGraphsBy(Class<? extends ITaskObject<?>> taskObjectClass) {
		RootNodeBuilder rootNodeBuilder = new RootNodeBuilder();
		rootNodeBuilder.addEqualsPropertyValue(StatusGraphFields.checkCancel().name(), false);
		rootNodeBuilder.addEqualsPropertyValue(StatusGraphFields.objectType().name(), taskObjectClass);
		return componentService.selectList(IStatusGraph.class, rootNodeBuilder.build(), null);
	}
}
