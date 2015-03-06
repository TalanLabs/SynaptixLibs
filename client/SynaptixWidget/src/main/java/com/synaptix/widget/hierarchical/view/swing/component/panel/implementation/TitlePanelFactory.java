package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.view.swing.component.panel.ContentPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.FooterPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;

public class TitlePanelFactory<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HierarchicalPanelFactory<IField, E, F, L> {

	public TitlePanelFactory() {
		super();
	}

	@Override
	protected HierarchicalPanel<IField, E, F, L> createHierarchicalPanel() {
		return new TitlePanel<E, F, L>(super.configurationContext);
	}

	@Override
	protected FooterPanel<IField, E, F, L> createFooterPanel(final HierarchicalPanel<IField, E, F, L> parent) {
		return new TitleFooterPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected HeaderPanel<IField, E, F, L> createHeaderPanel(final HierarchicalPanel<IField, E, F, L> parent) {
		return new TitleHeaderPanel<E, F, L>(super.configurationContext, parent);
	}

	@Override
	protected ContentPanel<IField, E, F, L> createContentPanel(final HierarchicalPanel<IField, E, F, L> parent) {
		return new TitleContentPanel<E, F, L>(super.configurationContext, parent);
	}

}
