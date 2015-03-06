package com.synaptix.service.hierarchical.model;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

/**
 * 
 * @param <E>
 *            title component
 * @param <F>
 *            data value class
 */
@SynaptixComponent
public interface IHierarchicalLine<E extends IComponent, F extends Serializable> extends IHierarchicalTitle<E>, IHierarchicalData<F> {

}
