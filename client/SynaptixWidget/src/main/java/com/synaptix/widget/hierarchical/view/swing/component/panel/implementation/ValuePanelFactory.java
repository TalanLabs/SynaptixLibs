package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;

public class ValuePanelFactory<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalPanelFactory<F, E, F, L> {

	public ValuePanelFactory() {
		super();
	}

	@Override
	protected HierarchicalPanel<F, E, F, L> createHierarchicalPanel() {
		return new ValuePanel<E, F, L>(super.configurationContext);
	}

	@Override
	protected FooterPanel<F, E, F, L> createFooterPanel(final HierarchicalPanel<F, E, F, L> parent) {
		return new ValueFooterPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected HeaderPanel<F, E, F, L> createHeaderPanel(final HierarchicalPanel<F, E, F, L> parent) {
		return new ValueHeaderPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected ContentPanel<F, E, F, L> createContentPanel(final HierarchicalPanel<F, E, F, L> parent) {
		return new ValueContentPanel<E, F, L>(super.configurationContext, parent);
	}
}
