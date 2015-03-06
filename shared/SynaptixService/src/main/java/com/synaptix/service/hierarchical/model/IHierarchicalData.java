package com.synaptix.service.hierarchical.model;

import java.io.Serializable;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IHierarchicalData<F extends Serializable> extends IComponent {

	public Map<F, Serializable> getValuesMap();

	public void setValuesMap(final Map<F, ? extends Serializable> valuesMap);

}
