package com.synaptix.widget.hierarchical.writer;

import java.io.Serializable;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.hierarchical.context.IHierarchicalContext;

public interface IHierarchicalExportWriter<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public void export(IHierarchicalContext<E, F, L> hierarchicalExportContext, IExportableTable[][] exportTableParts, Map<Class<?>, GenericObjectToString<?>> objectsToStringByClassMap);

}
