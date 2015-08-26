package com.synaptix.client.common.swing.view;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.taskmanager.controller.helper.INlsCRUDManagementController;
import com.synaptix.widget.crud.view.swing.descriptor.DefaultCRUDPanelDescriptor;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;


public class DefaultNlsCRUDPanelDescriptor<G extends IEntity & INlsMessage> extends DefaultCRUDPanelDescriptor<G> {

	private NlsMessageExtendedPanelDescriptor nlsMessageExtendedPanelDescriptor;

	public DefaultNlsCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, false);
	}

	public DefaultNlsCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns, boolean displayEditOnly) {
		super(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, displayEditOnly);
	}

	@Override
	public void create() {
		super.create();
		nlsMessageExtendedPanelDescriptor = new NlsMessageExtendedPanelDescriptor(getNlsCRUDManagementController());
	}

	/**
	 * Get NLS CRUD management controller
	 * 
	 * @return
	 */
	protected final INlsCRUDManagementController<G> getNlsCRUDManagementController() {
		return (INlsCRUDManagementController<G>) getCRUDManagementController();
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);

		nlsMessageExtendedPanelDescriptor.installToolBar(builder);
	}
}
