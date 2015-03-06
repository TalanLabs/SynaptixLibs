package com.synaptix.widget.hierarchical.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.column.DefaultHierarchicalColumnControl;
import com.synaptix.widget.hierarchical.column.IHierarchicalColumnControl;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;
import com.synaptix.widget.hierarchical.writer.DefaultHierarchicalExportWriter;
import com.synaptix.widget.hierarchical.writer.IExportableTable;
import com.synaptix.widget.hierarchical.writer.IHierarchicalExportWriter;
import com.synaptix.widget.view.ISynaptixViewFactory;

public class DefaultHierarchicalContext<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> implements IHierarchicalContext<E, F, L> {

	private final IView view;

	private final ISynaptixViewFactory viewFactory;

	private final Map<HierarchicalPanelKind, HierarchicalPanelFactory<?, E, F, L>> factories;

	private boolean built;

	private IHierarchicalExportWriter<E, F, L> hierarchicalExportWriter;

	private Boolean showSummaryLine;

	private Boolean showSummaryPanel;

	private boolean exportEnabled;

	private ConfigurationContext<E, F, L> configurationContext;

	private IHierarchicalColumnControl<E, F, L> columnControl;

	public DefaultHierarchicalContext(IView view, ISynaptixViewFactory viewFactory) {
		super();

		this.view = view;
		this.viewFactory = viewFactory;
		this.factories = new HashMap<HierarchicalPanelKind, HierarchicalPanelFactory<?, E, F, L>>();

		this.built = false;

		setColumnControl(new DefaultHierarchicalColumnControl<E, F, L>());
		setExportWriter(new DefaultHierarchicalExportWriter<E, F, L>());
	}

	@Override
	public IView getView() {
		return view;
	}

	@Override
	public ISynaptixViewFactory getViewFactory() {
		return viewFactory;
	}

	@Override
	public ConfigurationContext<E, F, L> buildConfigurationContext(String title, ConstantsWithLookingBundle translationBundle, Class<L> modelClass) {
		built = true;
		configurationContext = new ConfigurationContext<E, F, L>(this, title, translationBundle, modelClass);
		for (Entry<HierarchicalPanelKind, HierarchicalPanelFactory<?, E, F, L>> entry : factories.entrySet()) {
			configurationContext.setFactoryForPanelKind(entry.getKey(), entry.getValue());
		}
		configurationContext.setExportWriter(hierarchicalExportWriter);
		if (showSummaryLine != null) {
			configurationContext.setShowSummaryLine(showSummaryLine);
		}
		if (showSummaryPanel != null) {
			configurationContext.setShowSummaryPanel(showSummaryPanel);
		}
		if (columnControl != null) {
			configurationContext.setColumnControl(columnControl);
		}
		configurationContext.setExportEnabled(exportEnabled);
		return configurationContext;
	}

	@Override
	public void setFactoryForPanelKind(HierarchicalPanelKind panelKind, HierarchicalPanelFactory<?, E, F, L> factory) {
		checkBuilt();
		if (factory != null) {
			factories.put(panelKind, factory);
		}
	}

	@Override
	public void setExportWriter(IHierarchicalExportWriter<E, F, L> hierarchicalExportWriter) {
		checkBuilt();
		this.hierarchicalExportWriter = hierarchicalExportWriter;
	}

	@Override
	public void setShowSummaryLine(boolean showSummaryLine) {
		checkBuilt();
		this.showSummaryLine = showSummaryLine;
	}

	@Override
	public void setShowSummaryPanel(boolean showSummaryPanel) {
		checkBuilt();
		this.showSummaryPanel = showSummaryPanel;
	}

	/**
	 * Default column control is {@link DefaultHierarchicalColumnControl}
	 */
	@Override
	public void setColumnControl(IHierarchicalColumnControl<E, F, L> columnControl) {
		checkBuilt();
		this.columnControl = columnControl;
	}

	@Override
	public void setExportEnabled(boolean exportEnabled) {
		this.exportEnabled = exportEnabled;
	}

	private void checkBuilt() {
		if (built) {
			throw new RuntimeException("The configuration context has already been built");
		}
	}

	@Override
	public void export(IExportableTable[][] exportTableParts) {
		if (configurationContext.hasExportWriter()) {
			configurationContext.getExportWriter().export(this, exportTableParts, configurationContext.getObjectsToStringByClass());
		}
	}

	@Override
	public void showColumnControlDialog() {
		if (configurationContext.hasColumnControl()) {
			configurationContext.getColumnControl().showColumnControlDialog();
		}
	}
}
