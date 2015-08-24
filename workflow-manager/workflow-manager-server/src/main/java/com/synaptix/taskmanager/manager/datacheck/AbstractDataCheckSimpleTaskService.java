package com.synaptix.taskmanager.manager.datacheck;

import java.net.URI;

import com.synaptix.taskmanager.manager.taskservice.AbstractSimpleTaskService;
import com.synaptix.taskmanager.manager.taskservice.TodoDescriptorBuilder;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;


public abstract class AbstractDataCheckSimpleTaskService<F extends ITaskObject<?>> extends AbstractSimpleTaskService<F> {

	private final ITodoDescriptor todoDescriptor;

	public AbstractDataCheckSimpleTaskService(String code, Class<F> objectType) {
		this(code, objectType, null, null);
	}

	public AbstractDataCheckSimpleTaskService(String code, Class<F> objectType, String todoCode, URI todoUri) {
		super(code, objectType, ServiceNature.DATA_CHECK);

		if (todoCode != null && todoUri != null) {
			this.todoDescriptor = new TodoDescriptorBuilder(todoCode, todoUri).description(getTodoDescription()).createToTodoTask(false).build();
		} else {
			this.todoDescriptor = null;
		}
	}

	protected String getTodoDescription() {
		return null;
	}

	@Override
	public ITodoDescriptor getTodoDescriptor() {
		return this.todoDescriptor;
	}

}
