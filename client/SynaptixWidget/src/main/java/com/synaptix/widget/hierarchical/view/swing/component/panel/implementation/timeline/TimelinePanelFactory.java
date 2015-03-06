package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.timeline;

import org.joda.time.LocalDate;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.ValuePanelFactory;

public class TimelinePanelFactory<E extends IComponent, L extends IHierarchicalLine<E, LocalDate>> extends ValuePanelFactory<E, LocalDate, L> {

	public TimelinePanelFactory() {
		super();
	}

	@Override
	protected HierarchicalPanel<LocalDate, E, LocalDate, L> createHierarchicalPanel() {
		return new TimelinePanel<E, L>(super.configurationContext);
	}

	@Override
	protected HeaderPanel<LocalDate, E, LocalDate, L> createHeaderPanel(final HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		return new TimelineHeaderPanel<E, L>(super.configurationContext, parent);
	}

	@Override
	protected ContentPanel<LocalDate, E, LocalDate, L> createContentPanel(HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		return new TimelineContentPanel<E, L>(super.configurationContext, parent);
	}

	@Override
	protected FooterPanel<LocalDate, E, LocalDate, L> createFooterPanel(HierarchicalPanel<LocalDate, E, LocalDate, L> parent) {
		return new TimelineFooterPanel<E, L>(super.configurationContext, parent);
	}

}
