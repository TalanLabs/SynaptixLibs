package com.synaptix.widget.hierarchical.column;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.JSyHierarchicalPanel;

public interface IHierarchicalColumnControl<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public void install(JSyHierarchicalPanel<E, F, L> syHierarchicalPanel);

	public void install(ConfigurationContext<E, F, L> configurationContext);

	public void showColumnControlDialog();

	public void setCanHideColumns(boolean canHideColumns);

	public boolean canHideColumns();

}
