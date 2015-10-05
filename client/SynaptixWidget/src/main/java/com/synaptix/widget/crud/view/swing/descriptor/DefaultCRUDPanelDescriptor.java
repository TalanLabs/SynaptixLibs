package com.synaptix.widget.crud.view.swing.descriptor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.Column;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractCloneAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.actions.view.swing.AbstractShowAction;
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

		if (getCRUDManagementController().hasAuthWrite()) {
			editAction = buildEditAction();
		} else {
			editAction = new ShowAction();
		}
		editAction.setEnabled(false);

		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);
	}

	/**
	 * Build edit action. Default is a simple "Edit..." button which calls showEntity(selectedComponent)
	 */
	protected Action buildEditAction() {
		return new EditAction();
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
							&& getDefaultDoubleClickAction() != null && getDefaultDoubleClickAction().isEnabled()) {

						int idx = getDefaultSearchComponentsPanel().getTable().getSelectedColumn();
						if (idx > -1) {
							Column column = getTablePageComponentsPanel().getComponentTableModel().getColumn(idx);
							if ((column != null) && (!column.isEditable())) {
								getDefaultDoubleClickAction().actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
							}
						}
					}
				}
			});
		}

		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		table.getActionMap().put("Enter", new AbstractAction() {

			private static final long serialVersionUID = 8505503582611662534L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				if ((getDefaultEnterAction() != null) && (getDefaultEnterAction().isEnabled())) {
					getDefaultEnterAction().actionPerformed(ae);
				}
			}
		});
		// table.addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyTyped(KeyEvent e) {
		// if ((!e.isConsumed()) && (e.getKeyCode() == KeyEvent.VK_ENTER) || (e.getKeyChar() == '\n')) {
		//
		// e.consume();
		// }
		// }
		// });
	}

	protected void updateEnabledActions() {
		if (getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
			cloneAction.setEnabled(getCRUDManagementController().hasAuthWrite());
			editAction.setEnabled(true);
			deleteAction.setEnabled(getCRUDManagementController().hasAuthWrite());
		} else {
			cloneAction.setEnabled(false);
			editAction.setEnabled(false);
			deleteAction.setEnabled(isMultiCancel() && getCRUDManagementController().hasAuthWrite());
		}
	}

	protected boolean isMultiCancel() {
		return false;
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

	protected Action getDefaultEnterAction() {
		return getEditAction();
	}

	@Override
	public List<G> getComponentList() {
		// no need to convert line (automatic ordering)
		return getTablePageComponentsPanel().getComponentTableModel().getComponentList();
	}

	@Override
	public void selectLine(int line) {
		// no need to convert line (automatic ordering)
		getTablePageComponentsPanel().getTable().getSelectionModel().setSelectionInterval(line, line);
		getTablePageComponentsPanel().getTable().scrollRectToVisible(getTablePageComponentsPanel().getTable().getCellRect(line, 0, true));
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

	private class ShowAction extends AbstractShowAction {

		private static final long serialVersionUID = 892920133520692619L;

		public ShowAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().showEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Default CRUD : consult, then button to edit
			getCRUDManagementController().showEntity(getSelectedComponent());
		}
	}

	private class EditAction extends AbstractEditAction {

		private static final long serialVersionUID = 892920133520692619L;

		public EditAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Default CRUD : consult, then button to edit
			// if (getCRUDManagementController().hasAuthWrite()) {
			// getCRUDManagementController().editEntity(getSelectedComponent());
			// } else {
			getCRUDManagementController().showEntity(getSelectedComponent());
			// }
		}
	}

	private class DeleteAction extends AbstractDeleteAction {

		private static final long serialVersionUID = -4405083037235887225L;

		public DeleteAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isMultiCancel()) {
				getCRUDManagementController().deleteEntities(getSelectedComponents());
			} else {
				getCRUDManagementController().deleteEntity(getSelectedComponent());
			}
		}
	}
}
