package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.timeline;

import java.awt.Font;

import org.joda.time.LocalDate;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.panel.implementation.ValuePanel;

public class TimelinePanel<E extends IComponent, L extends IHierarchicalLine<E, LocalDate>> extends ValuePanel<E, LocalDate, L> {

	private static final long serialVersionUID = -1393481797674499412L;

	public TimelinePanel(final ConfigurationContext<E, LocalDate, L> configurationContext) {
		super(configurationContext);
	}

	@Override
	protected int getColumnWidth() {
		return 45;
	}

	@Override
	public Font getColumnTitleFont() {
		return super.configurationContext.getSmallEmphaseFont();
	}
}
