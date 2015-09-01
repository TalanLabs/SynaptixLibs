package com.synaptix.component.model;

import java.util.Set;

import com.synaptix.component.IComponent;

/**
 * Service Result as a component to use synaptix proxies, processors, ...
 *
 * @author Nicolas P
 */
public interface IServiceResultComponent<O extends Object> extends IComponent, IServiceWithErrorResult<O> {

	@Override
	@Computed(ServiceResultComponentComputed.class)
	public Set<IError> getErrors();

}
