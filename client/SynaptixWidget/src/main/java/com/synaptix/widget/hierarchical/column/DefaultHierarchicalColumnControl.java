package com.synaptix.widget.hierarchical.column;

import java.io.Serializable;

import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.JSyHierarchicalPanel;

public final class DefaultHierarchicalColumnControl<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> implements IHierarchicalColumnControl<E, F, L> {

	private ConfigurationContext<E, F, L> configurationContext;

	private JSyHierarchicalPanel<E, F, L> syHierarchicalPanel;

	private boolean canHideColumns;

	@Override
	public final void install(JSyHierarchicalPanel<E, F, L> syHierarchicalPanel) {
		this.syHierarchicalPanel = syHierarchicalPanel;
	}

	@Override
	public final void install(ConfigurationContext<E, F, L> configurationContext) {
		this.configurationContext = configurationContext;
	}

	@Override
	public final void showColumnControlDialog() {
		if (canHideColumns) {
			HierarchicalVisibilityColumnsDialog<E, F, L> hierarchicalVisibilityColumnsDialog = new HierarchicalVisibilityColumnsDialog<E, F, L>(syHierarchicalPanel, configurationContext);
			hierarchicalVisibilityColumnsDialog.showDialog(configurationContext.getHierarchicalContext().getView());
		}
	}

	@Override
	public final boolean canHideColumns() {
		return canHideColumns;
	}

	@Override
	public final void setCanHideColumns(boolean canHideColumns) {
		this.canHideColumns = canHideColumns;
	}
}
