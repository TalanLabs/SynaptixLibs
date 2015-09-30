package com.synaptix.taskmanager.delegate;

import java.util.List;

import com.google.inject.Inject;
import com.synaptix.mybatis.delegate.ComponentServiceDelegate;
import com.synaptix.service.filter.builder.RootNodeBuilder;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.StatusGraphFields;
import com.synaptix.taskmanager.service.AbstractSimpleService;
import com.synaptix.taskmanager.service.IStatusGraphService;

public class StatusGraphServiceDelegate extends AbstractSimpleService implements IStatusGraphService {

	@Inject
	private ComponentServiceDelegate componentServiceDelegate;

	@Override
	public List<IStatusGraph> findStatusGraphsBy(Class<? extends ITaskObject<?>> taskObjectClass) {
		RootNodeBuilder rootNodeBuilder = new RootNodeBuilder();
		rootNodeBuilder.addEqualsPropertyValue(StatusGraphFields.checkCancel().name(), false);
		rootNodeBuilder.addEqualsPropertyValue(StatusGraphFields.objectType().name(), taskObjectClass);
		return componentServiceDelegate.selectList(IStatusGraph.class, rootNodeBuilder.build(), null);
	}
}
