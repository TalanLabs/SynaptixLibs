package com.synaptix.taskmanager.manager.enrichment;

import com.synaptix.taskmanager.manager.taskservice.AbstractSimpleTaskService;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;


public abstract class AbstractEnrichmentSimpleTaskService<F extends ITaskObject<?>> extends AbstractSimpleTaskService<F> {

	public AbstractEnrichmentSimpleTaskService(String code, Class<F> objectType) {
		super(code, objectType, ServiceNature.ENRICHMENT);
	}
}
