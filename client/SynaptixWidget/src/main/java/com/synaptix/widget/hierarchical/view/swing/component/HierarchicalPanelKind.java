package com.synaptix.widget.hierarchical.view.swing.component;

import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;


/**
 * Defines the columns displayed in this component. Those should be used with {@link #setFactoryForPanelKind(HierarchicalPanelKind, HierarchicalPanelFactory)}.
 */
public enum HierarchicalPanelKind {
	TITLE, VALUE, SUMMARY;
}