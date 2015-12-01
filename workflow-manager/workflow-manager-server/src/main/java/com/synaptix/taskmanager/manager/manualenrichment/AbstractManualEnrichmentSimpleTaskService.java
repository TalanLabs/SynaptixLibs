package com.synaptix.taskmanager.manager.manualenrichment;

import java.net.URI;

import com.synaptix.taskmanager.manager.taskservice.AbstractSimpleTaskService;
import com.synaptix.taskmanager.manager.taskservice.TodoDescriptorBuilder;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractManualEnrichmentSimpleTaskService<F extends ITaskObject<?>> extends AbstractSimpleTaskService<F> {

	private final ITodoDescriptor todoDescriptor;

	public AbstractManualEnrichmentSimpleTaskService(String code, Class<F> objectType, String todoCode, URI todoUri) {
		super(code, objectType, ServiceNature.MANUAL_ENRICHMENT);

		if (todoCode != null && todoUri != null) {
			this.todoDescriptor = new TodoDescriptorBuilder(todoCode, todoUri).description(getTodoDescription()).createToTodoTask(true).build();
		} else {
			this.todoDescriptor = null;
		}
	}

	protected String getTodoDescription() {
		return null;
	}

	@Override
	public final ITodoDescriptor getTodoDescriptor() {
		return this.todoDescriptor;
	}
}
