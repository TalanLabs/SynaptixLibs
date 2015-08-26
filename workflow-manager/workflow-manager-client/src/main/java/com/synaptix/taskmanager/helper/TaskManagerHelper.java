package com.synaptix.taskmanager.helper;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.objecttype.IObjectTypeManager;

public class TaskManagerHelper {

	private static final String SEPARATOR = " - ";

	@Inject
	private static IObjectTypeManager objectTypeManager;

	private static String concat(String... values) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String value : values) {
			if (first) {
				first = false;
			} else if (StringUtils.isNotEmpty(value)) {
				sb.append(SEPARATOR);
			}
			if (value != null) {
				sb.append(value);
			}
		}
		return sb.toString();
	}

	public static IObjectTypeManager getObjectTypeManager() {
		return objectTypeManager;
	}

	public static GenericObjectToString<ITaskServiceDescriptor> createTaskServiceDescriptorGenericObjectToString() {
		return new GenericObjectToString<ITaskServiceDescriptor>() {
			@Override
			public String getString(ITaskServiceDescriptor value) {
				if (value != null) {
					return value.getCode();
				}
				return null;
			}
		};
	}

	public static GenericObjectToString<ITaskType> createTaskTypeGenericObjectToString() {
		return new GenericObjectToString<ITaskType>() {
			@Override
			public String getString(ITaskType value) {
				if (value != null) {
					return value.getCode();
				}
				return null;
			}
		};
	}

	public static GenericObjectToString<ITodoFolder> createTodoFolderGenericObjectToString() {
		return new GenericObjectToString<ITodoFolder>() {
			@Override
			public String getString(ITodoFolder value) {
				if (value != null) {
					return value.getMeaning();
				}
				return null;
			}
		};
	}

	public static GenericObjectToString<ITaskChain> createTaskChainGenericObjectToString() {
		return new GenericObjectToString<ITaskChain>() {
			@Override
			public String getString(ITaskChain value) {
				if (value != null) {
					return value.getCode();
				}
				return null;
			}
		};
	}

}
