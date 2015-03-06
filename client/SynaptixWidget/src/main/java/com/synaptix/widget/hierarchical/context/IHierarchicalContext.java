package com.synaptix.widget.hierarchical.context;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.column.IHierarchicalColumnControl;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;
import com.synaptix.widget.hierarchical.writer.IExportableTable;
import com.synaptix.widget.hierarchical.writer.IHierarchicalExportWriter;
import com.synaptix.widget.view.ISynaptixViewFactory;

public interface IHierarchicalContext<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public IView getView();

	public ISynaptixViewFactory getViewFactory();

	public void setFactoryForPanelKind(final HierarchicalPanelKind panelKind, final HierarchicalPanelFactory<?, E, F, L> factory);

	public void setExportWriter(IHierarchicalExportWriter<E, F, L> hierarchicalExportWriter);

	public void setShowSummaryLine(boolean showSummaryLine);

	public void setShowSummaryPanel(boolean showSummaryPanel);

	public void setColumnControl(IHierarchicalColumnControl<E, F, L> columnControl);

	/**
	 * Builds a configuration context. Can be called only once
	 * 
	 * @param title
	 * @param translationBundle
	 * @param modelClass
	 * @return
	 */
	public ConfigurationContext<E, F, L> buildConfigurationContext(final String title, final ConstantsWithLookingBundle translationBundle, final Class<L> modelClass);

	public void export(IExportableTable[][] exportTableParts);

	public void showColumnControlDialog();

	public void setExportEnabled(boolean exportEnabled);

}
