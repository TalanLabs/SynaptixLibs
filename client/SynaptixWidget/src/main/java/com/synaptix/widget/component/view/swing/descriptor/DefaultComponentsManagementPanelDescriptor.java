package com.synaptix.widget.component.view.swing.descriptor;

import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.widget.component.controller.IComponentsManagementController;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.component.view.swing.DefaultComponentsManagementPanel;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.view.descriptor.IDockableViewDescriptor;
import com.synaptix.widget.view.descriptor.IRibbonViewDescriptor;
import com.vlsolutions.swing.docking.DockKey;

public class DefaultComponentsManagementPanelDescriptor<E extends IComponent> extends DefaultSearchPanelDescriptor<E> implements IDockableViewDescriptor, IRibbonViewDescriptor,
		IComponentsManagementViewDescriptor<E> {

	private final RibbonData ribbonData;

	public DefaultComponentsManagementPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, null);
	}

	public DefaultComponentsManagementPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns,
			String[] defaultHideTableColumns) {
		super(constantsWithLookingBundle, filterColumns, tableColumns, defaultHideTableColumns);

		this.ribbonData = ribbonData;
	}

	@SuppressWarnings("unchecked")
	protected final IComponentsManagementController<E> getComponentsManagementController() {
		return (IComponentsManagementController<E>) getSearchComponentsContext();
	}

	@Override
	public String getIdDockable() {
		return this.getClass().getName();
	}

	@Override
	public String getTitle() {
		return ribbonData.getTitle();
	}

	@Override
	public final RibbonData getRibbonData() {
		return ribbonData;
	}

	public final DefaultComponentsManagementPanel<E> getDefaultComponentsManagementPanel() {
		return (DefaultComponentsManagementPanel<E>) getDefaultSearchComponentsPanel();
	}

	public final DockKey getDockKey() {
		return getDefaultComponentsManagementPanel().getDockKey();
	}

	public final SyDockingContext getDockingContext() {
		return getDefaultComponentsManagementPanel().getDockingContext();
	}

	/**
	 * Show dockable
	 */
	public final void showDockable() {
		getDefaultComponentsManagementPanel().getDockingContext().showDockable(getDefaultComponentsManagementPanel().getDockKey().getKey());
	}

	/**
	 * Start search with current filter
	 */
	public final void search() {
		getDefaultSearchComponentsPanel().getSearchComponentsContext().searchComponents(getValueFilters());
	}
}
