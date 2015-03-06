package com.synaptix.gwt.shared.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PropertyChangeCapableDtoImpl implements IPropertyChangeCapableDto {

	protected transient final List<PropertyChangeListenerDto> PropertyChangeListenerDtos = new ArrayList<PropertyChangeListenerDto>();

	protected transient final Map<String, List<PropertyChangeListenerDto>> PropertyChangeListenerDtoMap = new HashMap<String, List<PropertyChangeListenerDto>>();

	@Override
	public void addPropertyChangeListenerDto(PropertyChangeListenerDto l) {
		PropertyChangeListenerDtos.add(l);
	}

	@Override
	public void removePropertyChangeListenerDto(PropertyChangeListenerDto l) {
		PropertyChangeListenerDtos.remove(l);
	}

	@Override
	public void addPropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l) {
		List<PropertyChangeListenerDto> ls = PropertyChangeListenerDtoMap.get(propertyName);
		if (ls == null) {
			ls = new ArrayList<PropertyChangeListenerDto>();
			PropertyChangeListenerDtoMap.put(propertyName, ls);
		}
		ls.add(l);
	}

	@Override
	public void removePropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l) {
		List<PropertyChangeListenerDto> ls = PropertyChangeListenerDtoMap.get(propertyName);
		if (ls != null) {
			ls.remove(l);
			if (ls.isEmpty()) {
				PropertyChangeListenerDtoMap.remove(propertyName);
			}
		}
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEventDto evt = new PropertyChangeEventDto(this, propertyName, oldValue, newValue);
		for (PropertyChangeListenerDto l : PropertyChangeListenerDtos) {
			l.propertyChange(evt);
		}
		List<PropertyChangeListenerDto> ls = PropertyChangeListenerDtoMap.get(propertyName);
		if (ls != null) {
			for (PropertyChangeListenerDto l : ls) {
				l.propertyChange(evt);
			}
		}
	}
}
