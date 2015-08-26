package com.synaptix.taskmanager.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;

public class TaskServiceDiscovery {

	private static final Log LOG = LogFactory.getLog(TaskServiceDiscovery.class);

	@Inject
	private Set<ITaskService> taskServices;

	private boolean initialized;

	private Map<String, ITaskService> taskServiceMap;

	private List<ITaskServiceDescriptor> taskServiceDescriptors;

	@Inject
	public TaskServiceDiscovery() {
		super();

		this.initialized = false;
	}

	private void doInitialized() {
		synchronized (this) {
			if (!initialized) {
				taskServiceMap = new HashMap<String, ITaskService>();
				taskServiceDescriptors = new ArrayList<ITaskServiceDescriptor>();

				for (ITaskService taskService : taskServices) {
					taskServiceMap.put(taskService.getCode(), taskService);

					ITaskServiceDescriptor taskServiceDescriptor = ComponentFactory.getInstance().createInstance(ITaskServiceDescriptor.class);
					taskServiceDescriptor.setCategory(taskService.getCategory());
					taskServiceDescriptor.setCode(taskService.getCode());
					taskServiceDescriptor.setDescription(taskService.getDescription());
					taskServiceDescriptor.setNature(taskService.getNature());
					taskServiceDescriptor.setObjectType(taskService.getObjectKinds());

					taskServiceDescriptors.add(taskServiceDescriptor);

					if (LOG.isDebugEnabled()) {
						LOG.debug("Add TaskService " + taskService.getCode() + " " + taskService.getClass());
					}
				}

				Collections.sort(taskServiceDescriptors, new Comparator<ITaskServiceDescriptor>() {
					@Override
					public int compare(ITaskServiceDescriptor o1, ITaskServiceDescriptor o2) {
						return o1.getCode().compareTo(o2.getCode());
					}
				});

				initialized = true;
			}
		}
	}

	/**
	 * Get a task service by code
	 */
	@SuppressWarnings("unchecked")
	public <G extends ITaskService> G getTaskService(String code) {
		doInitialized();
		if (!taskServiceMap.containsKey(code)) {
			throw new IllegalArgumentException("Code " + code + " does not exists in task service");
		}
		return (G) taskServiceMap.get(code);
	}

	/**
	 * Get all task services
	 */
	public Collection<? extends ITaskService> getAllTaskServices() {
		doInitialized();
		return Collections.unmodifiableCollection(taskServiceMap.values());
	}

	/**
	 * Get all task service descriptors
	 */
	public List<ITaskServiceDescriptor> getAllTaskServiceDescriptors() {
		doInitialized();
		return taskServiceDescriptors;
	}
}
