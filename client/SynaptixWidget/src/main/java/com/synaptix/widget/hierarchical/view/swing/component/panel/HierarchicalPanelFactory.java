package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;

public abstract class HierarchicalPanelFactory<U, E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	protected ConfigurationContext<E, F, L> configurationContext;

	public HierarchicalPanelFactory() {
	}

	public void install(ConfigurationContext<E, F, L> configurationContext) {
		this.configurationContext = configurationContext;
	}

	public HierarchicalPanel<U, E, F, L> buildHierarchicalPanel(final PropertyChangeListener changeListener) {
		final HierarchicalPanel<U, E, F, L> panel = createHierarchicalPanel();
		fillHierarchicalPanel(panel);
		initializePanel(panel, changeListener);
		return panel;
	}

	private void fillHierarchicalPanel(final HierarchicalPanel<U, E, F, L> parent) {
		parent.setHeader(buildHeaderPanel(parent));
		parent.setContent(buildContentPanel(parent));
		parent.setFooter(buildFooterPanel(parent));
	}

	private void initializePanel(final HierarchicalPanel<U, E, F, L> panel, final PropertyChangeListener changeListener) {
		panel.addPropertyChangeListener(changeListener);
		panel.initialize();
	}

	private FooterPanel<U, E, F, L> buildFooterPanel(final HierarchicalPanel<U, E, F, L> parent) {
		final FooterPanel<U, E, F, L> footer = createFooterPanel(parent);
		footer.addPropertyChangeListener(parent);
		return footer;
	}

	private HeaderPanel<U, E, F, L> buildHeaderPanel(final HierarchicalPanel<U, E, F, L> parent) {
		final HeaderPanel<U, E, F, L> header = createHeaderPanel(parent);
		header.addPropertyChangeListener(parent);
		return header;
	}

	private ContentPanel<U, E, F, L> buildContentPanel(final HierarchicalPanel<U, E, F, L> parent) {
		final ContentPanel<U, E, F, L> content = createContentPanel(parent);
		content.addPropertyChangeListener(parent);
		return content;
	}

	protected abstract HierarchicalPanel<U, E, F, L> createHierarchicalPanel();

	protected abstract FooterPanel<U, E, F, L> createFooterPanel(final HierarchicalPanel<U, E, F, L> parent);

	protected abstract HeaderPanel<U, E, F, L> createHeaderPanel(final HierarchicalPanel<U, E, F, L> parent);

	protected abstract ContentPanel<U, E, F, L> createContentPanel(final HierarchicalPanel<U, E, F, L> parent);

}
