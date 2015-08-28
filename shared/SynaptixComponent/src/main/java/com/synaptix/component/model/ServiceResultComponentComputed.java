package com.synaptix.component.model;

import java.util.Collections;
import java.util.Set;

public class ServiceResultComponentComputed {

	public Set<IError> getErrors(IServiceResultComponent<Object> serviceResultComponent) {
		return Collections.unmodifiableSet(serviceResultComponent.getErrorSet());
	}
}