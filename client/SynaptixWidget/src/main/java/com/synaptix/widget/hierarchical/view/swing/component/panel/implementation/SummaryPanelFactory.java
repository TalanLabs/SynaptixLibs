package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;

public class SummaryPanelFactory<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalPanelFactory<HierarchicalSummaryRule<E, F, L>, E, F, L> {

	public SummaryPanelFactory() {
		super();
	}

	@Override
	protected HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> createHierarchicalPanel() {
		return new SummaryPanel<E, F, L>(super.configurationContext);
	}

	@Override
	protected FooterPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> createFooterPanel(final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		return new SummaryFooterPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected HeaderPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> createHeaderPanel(final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		return new SummaryHeaderPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected ContentPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> createContentPanel(final HierarchicalPanel<HierarchicalSummaryRule<E, F, L>, E, F, L> parent) {
		return new SummaryContentPanel<E, F, L>(super.configurationContext, parent);
	}

}
