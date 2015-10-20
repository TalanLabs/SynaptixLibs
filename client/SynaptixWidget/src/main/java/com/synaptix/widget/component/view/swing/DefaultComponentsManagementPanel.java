package com.synaptix.widget.component.view.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pushingpixels.flamingo.api.common.JCommandButton;

import com.synaptix.component.IComponent;
import com.synaptix.widget.component.controller.IComponentsManagementController;
import com.synaptix.widget.component.view.IComponentsManagementView;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.IRibbonContextView;
import com.synaptix.widget.core.view.swing.RibbonContext;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.view.descriptor.IDockableViewDescriptor;
import com.synaptix.widget.view.descriptor.IRibbonViewDescriptor;
import com.synaptix.widget.view.swing.descriptor.AbstractSearchViewDescriptor;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.event.DockableStateChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateChangeListener;

public class DefaultComponentsManagementPanel<E extends IComponent> extends DefaultSearchTablePageComponentsPanel<E> implements IComponentsManagementView<E>, IDockingContextView, IDockable,
		IRibbonContextView {

	private static final long serialVersionUID = 222892752441088360L;

	private DockKey dockKey;

	private SyDockingContext dockingContext;

	private String category;

	public DefaultComponentsManagementPanel(Class<E> componentClass, AbstractSearchViewDescriptor<E> viewDescriptor) {
		super(componentClass, viewDescriptor);
	}

	/**
	 * Get components management controller
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final IComponentsManagementController<E> getComponentsManagementController() {
		return (IComponentsManagementController<E>) getSearchComponentsContext();
	}

	private final boolean hasDockableViewDescriptor() {
		if (getViewDescriptor() != null && getViewDescriptor() instanceof IDockableViewDescriptor) {
			return true;
		}
		return false;
	}

	private final boolean hasRibbonViewDescriptor() {
		if (getViewDescriptor() != null && getViewDescriptor() instanceof IRibbonViewDescriptor) {
			return true;
		}
		return false;
	}

	@Override
	public void initializeDockingContext(SyDockingContext dockingContext) {
		if (hasDockableViewDescriptor()) {
			IDockableViewDescriptor desc = (IDockableViewDescriptor) getViewDescriptor();

			this.dockingContext = dockingContext;

			dockKey = new DockKey(desc.getIdDockable(), desc.getTitle());
			dockKey.setFloatEnabled(true);
			dockKey.setAutoHideEnabled(false);

			dockingContext.registerDockable(this);

			dockingContext.addDockableStateChangeListener(this, new DockableStateChangeListener() {
				@Override
				public void dockableStateChanged(DockableStateChangeEvent event) {
					if (event.getPreviousState() == null || event.getPreviousState().isClosed()) {
						if (((getSearchHeader() == null) || (getSearchHeader().isEnabledSearchAction())) && (viewDescriptor.isSearchAtOpening())) {
							getComponentsManagementController().searchComponents(getValueFilters());
						}
					}
				}
			});
		}
	}

	@Override
	public void initializeRibbonContext(RibbonContext ribbonContext) {
		if (hasRibbonViewDescriptor()) {
			IRibbonViewDescriptor desc = (IRibbonViewDescriptor) getViewDescriptor();
			RibbonData ribbonData = desc.getRibbonData();
			if (ribbonData != null) {
				JCommandButton cb = new JCommandButton(ribbonData.getTitle(), ribbonData.getIcon());
				cb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dockingContext.showDockable(getIdDockable());
					}
				});

				String ribbonTaskTitle = ribbonData.getRibbonTaskTitle();
				String ribbonBandTitle = ribbonData.getRibbonBandTitle();
				ribbonContext.addRibbonTask(ribbonTaskTitle, ribbonData.getRibbonTaskPriority()).addRibbonBand(ribbonBandTitle, ribbonData.getRibbonBandPriority())
						.addCommandeButton(cb, ribbonData.getPriority());
				category = ribbonData.getCategory();
			}
		}
	}

	@Override
	public DockKey getDockKey() {
		return dockKey;
	}

	public SyDockingContext getDockingContext() {
		return dockingContext;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public String getIdDockable() {
		if (hasDockableViewDescriptor()) {
			return ((IDockableViewDescriptor) getViewDescriptor()).getIdDockable();
		}
		return new StringBuilder().append(this.getClass().getName()).append("_").append(componentClass.getName()).toString();
	}

	public void setDockKey(DockKey dockKey) {
		this.dockKey = dockKey;
	}

	public void setDockingContext(SyDockingContext dockingContext) {
		this.dockingContext = dockingContext;
	}
}
