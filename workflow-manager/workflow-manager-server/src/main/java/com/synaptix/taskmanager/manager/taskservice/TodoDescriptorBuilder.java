package com.synaptix.taskmanager.manager.taskservice;

import java.net.URI;

public class TodoDescriptorBuilder {

	private final TodoDescriptorImpl todoDescriptorImpl;

	public TodoDescriptorBuilder(String code, URI uri) {
		super();
		todoDescriptorImpl = new TodoDescriptorImpl();
		todoDescriptorImpl.code = code;
		todoDescriptorImpl.uri = uri;
	}

	public TodoDescriptorBuilder description(String description) {
		todoDescriptorImpl.description = description;
		return this;
	}

	/**
	 * If createToTodoTask is set to true, the todo will be created when the task is created.
	 */
	public TodoDescriptorBuilder createToTodoTask(boolean createToTodoTask) {
		todoDescriptorImpl.createToTodoTask = createToTodoTask;
		return this;
	}

	public ITaskService.ITodoDescriptor build() {
		return todoDescriptorImpl;
	}

	public static final class TodoDescriptorImpl implements ITaskService.ITodoDescriptor {

		private URI uri;

		private String code;

		private String description;

		private boolean createToTodoTask;

		@Override
		public URI getUri() {
			return uri;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public boolean isCreateToTodoTask() {
			return createToTodoTask;
		}
	}
}
