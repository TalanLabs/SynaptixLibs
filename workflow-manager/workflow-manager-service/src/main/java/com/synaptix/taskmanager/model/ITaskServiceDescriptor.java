package com.synaptix.taskmanager.model;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.taskmanager.model.domains.ServiceNature;

@SynaptixComponent
public interface ITaskServiceDescriptor extends IComponent {

	@EqualsKey
	public String getCode();

	public void setCode(String code);

	public String getDescription();

	public void setDescription(String description);

	public String getCategory();

	public void setCategory(String category);

	public Class<? extends ITaskObject<?>> getObjectType();

	public void setObjectType(Class<? extends ITaskObject<?>> objectType);

	public ServiceNature getNature();

	public void setNature(ServiceNature nature);

	public boolean isCheckGenericEvent();

	public void setCheckGenericEvent(boolean checkGenericEvent);
}
