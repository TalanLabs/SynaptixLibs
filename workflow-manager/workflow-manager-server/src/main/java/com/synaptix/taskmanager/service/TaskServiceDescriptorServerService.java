package com.synaptix.taskmanager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.service.ServiceException;
import com.synaptix.service.model.ISortOrder;
import com.synaptix.taskmanager.manager.TaskServiceDiscovery;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.TaskServiceDescriptorFields;


public class TaskServiceDescriptorServerService extends AbstractSimpleService implements ITaskServiceDescriptorService {

	private final TaskServiceDiscovery taskServiceDiscovery;

	@Inject
	public TaskServiceDescriptorServerService(TaskServiceDiscovery taskServiceDiscovery) {
		super();

		this.taskServiceDiscovery = taskServiceDiscovery;
	}

	@Override
	public int countPagination(Map<String, Object> valueFilterMap) throws ServiceException {
		return filters(taskServiceDiscovery.getAllTaskServiceDescriptors(), valueFilterMap).size();
	}

	@Override
	public List<ITaskServiceDescriptor> selectPagination(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) throws ServiceException {
		return sort(filters(taskServiceDiscovery.getAllTaskServiceDescriptors(), valueFilterMap), orders);
	}

	private List<ITaskServiceDescriptor> filters(List<ITaskServiceDescriptor> taskServiceDescriptors, Map<String, Object> valueFilterMap) {
		List<ITaskServiceDescriptor> res = new ArrayList<ITaskServiceDescriptor>();
		if (taskServiceDescriptors != null && !taskServiceDescriptors.isEmpty()) {
			for (ITaskServiceDescriptor taskServiceDescriptor : taskServiceDescriptors) {
				boolean add = true;
				Iterator<Entry<String, Object>> it = valueFilterMap.entrySet().iterator();
				while (it.hasNext() && add) {
					Entry<String, Object> entry = it.next();
					if (entry.getValue() != null && StringUtils.isNotEmpty(entry.getValue().toString())) {
						if (taskServiceDescriptor.straightGetPropertyNames().contains(entry.getKey())) {
							Object value = taskServiceDescriptor.straightGetProperty(entry.getKey());
							if (value == null || !StringUtils.containsIgnoreCase(value.toString(), entry.getValue().toString())) {
								add = false;
							}
						}
					}
				}

				if (add) {
					res.add(taskServiceDescriptor);
				}
			}
		}
		return res;
	}

	private List<ITaskServiceDescriptor> sort(List<ITaskServiceDescriptor> taskServiceDescriptors, List<ISortOrder> orders) {
		if (orders != null && !orders.isEmpty()) {
			for (int i = orders.size() - 1; i >= 0; i--) {
				final ISortOrder sortOrder = orders.get(i);
				Collections.sort(taskServiceDescriptors, new Comparator<ITaskServiceDescriptor>() {
					public int compare(ITaskServiceDescriptor o1, ITaskServiceDescriptor o2) {
						int res = 0;
						if (o1.straightGetPropertyNames().contains(sortOrder.getPropertyName())) {
							Object value1 = o1.straightGetProperty(sortOrder.getPropertyName());
							Object value2 = o2.straightGetProperty(sortOrder.getPropertyName());
							if (value1 == null && value2 == null) {
								res = 0;
							} else if (value1 != null && value2 == null) {
								res = 1;
							} else if (value1 == null) {
								res = -1;
							} else {
								res = value1.toString().compareTo(value2.toString());
							}
						}
						return sortOrder.isAscending() ? res : -res;
					}
				});
			}
		}
		return taskServiceDescriptors;
	}

	@Override
	public ITaskServiceDescriptor findTaskServiceDescriptorByCode(String serviceCode) {
		return ComponentHelper.findComponentBy(taskServiceDiscovery.getAllTaskServiceDescriptors(), TaskServiceDescriptorFields.code().name(), serviceCode);
	}
}
