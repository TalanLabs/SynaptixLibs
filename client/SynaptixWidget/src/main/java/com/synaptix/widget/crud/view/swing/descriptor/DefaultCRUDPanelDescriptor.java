package com.synaptix.widget.crud.view.swing.descriptor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.swing.JSyTable;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractCloneAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.component.view.swing.descriptor.DefaultComponentsManagementPanelDescriptor;
import com.synaptix.widget.crud.controller.ICRUDManagementController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.descriptor.IDockableViewDescriptor;
import com.synaptix.widget.view.descriptor.IRibbonViewDescriptor;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;

public class DefaultCRUDPanelDescriptor<G extends IEntity> extends DefaultComponentsManagementPanelDescriptor<G> implements ICRUDManagementViewDescriptor<G>, IDockableViewDescriptor,
		IRibbonViewDescriptor {

	private final boolean displayEditOnly;

	private Action addAction;

	private Action cloneAction;

	private Action editAction;

	private Action deleteAction;

	public DefaultCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, false);
	}

	public DefaultCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns, boolean displayEditOnly) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, null, displayEditOnly);
	}

	public DefaultCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns, String[] defaultHideTableColumn) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, defaultHideTableColumn, false);
	}

	public DefaultCRUDPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns, String[] defaultHideTableColumns,
			boolean displayEditOnly) {
		super(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, defaultHideTableColumns);

		this.displayEditOnly = displayEditOnly;
	}

	@Override
	public void create() {
		super.create();

		initActions();
	}

	/**
	 * Get a CRUD management controller
	 * 
	 * @return
	 */
	protected final ICRUDManagementController<G> getCRUDManagementController() {
		return (ICRUDManagementController<G>) getComponentsManagementController();
	}

	private void initActions() {
		addAction = new AddAction();
		addAction.setEnabled(getCRUDManagementController().hasAuthWrite());

		cloneAction = new CloneAction();
		cloneAction.setEnabled(false);

		editAction = new EditAction();
		editAction.setEnabled(false);

		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);
	}

	@Override
	public void installPopupMenu(IPopupMenuActionsBuilder builder) {
		super.installPopupMenu(builder);
		if (displayEditOnly) {
			builder.addAction(editAction);
		} else {
			builder.addAction(cloneAction);
			builder.addSeparator();
			builder.addAction(editAction);
			builder.addAction(deleteAction);
		}
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);
		if (displayEditOnly) {
			builder.addAction(editAction);
		} else {
			builder.addAction(addAction);
			builder.addAction(cloneAction);
			builder.addSeparator();
			builder.addAction(editAction);
			builder.addAction(deleteAction);
		}
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updateEnabledActions();
				}
			}
		});

		if (getDefaultDoubleClickAction() != null) {
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1
							&& getDefaultDoubleClickAction() != null) {
						getDefaultDoubleClickAction().actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
					}
				}
			});
		}
	}

	protected void updateEnabledActions() {
		if (getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
			cloneAction.setEnabled(getCRUDManagementController().hasAuthWrite());
			editAction.setEnabled(getCRUDManagementController().hasAuthWrite());
			deleteAction.setEnabled(getCRUDManagementController().hasAuthWrite());
		} else {
			cloneAction.setEnabled(false);
			editAction.setEnabled(false);
			deleteAction.setEnabled(false);
		}
	}

	/**
	 * Get default double click action
	 * 
	 * @return
	 */
	protected Action getDefaultDoubleClickAction() {
		return editAction;
	}

	protected final Action getAddAction() {
		return addAction;
	}

	protected final Action getCloneAction() {
		return cloneAction;
	}

	protected final Action getEditAction() {
		return editAction;
	}

	protected final Action getDuplicateAction() {
		return cloneAction;
	}

	protected final Action getDeleteAction() {
		return deleteAction;
	}

	private class AddAction extends AbstractAddAction {

		private static final long serialVersionUID = -2209334623415724937L;

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDManagementController().addEntity();
		}
	}

	private class CloneAction extends AbstractCloneAction {

		private static final long serialVersionUID = 892920133520692619L;

		public CloneAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().cloneEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDManagementController().cloneEntity(getSelectedComponent());
		}
	}

	private class EditAction extends AbstractEditAction {

		private static final long serialVersionUID = 892920133520692619L;

		public EditAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getCRUDManagementController().hasAuthWrite()) {
				getCRUDManagementController().editEntity(getSelectedComponent());
			} else {
				getCRUDManagementController().showEntity(getSelectedComponent());
			}
		}
	}

	private class DeleteAction extends AbstractDeleteAction {

		private static final long serialVersionUID = -4405083037235887225L;

		public DeleteAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDManagementController().deleteEntity(getSelectedComponent());
		}
	}
}
