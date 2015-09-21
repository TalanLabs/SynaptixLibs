package com.synaptix.widget.crud.view.swing.descriptor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.entity.IEntity;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.utils.PopupMenuMouseListener;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractCloneAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.crud.controller.ICRUDWithChildrenManagementController;
import com.synaptix.widget.crud.view.descriptor.ICRUDWithChildrenManagementViewDescriptor;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.helper.PopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.ToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;

public class DefaultCRUDWithChildrenPanelDescriptor<G extends IEntity, C extends IEntity> extends DefaultCRUDPanelDescriptor<G> implements ICRUDWithChildrenManagementViewDescriptor<G, C> {

	protected final Class<C> childComponentClass;

	protected final ConstantsWithLookingBundle childConstantsWithLookingBundle;

	protected final String[] childTableColumns;

	private DefaultComponentTableModel<C> childComponentTableModel;

	private JSyTable childTable;

	private Action addChildAction;

	private Action duplicateChildAction;

	private Action editChildAction;

	private Action deleteChildAction;

	private JPopupMenu childPopupMenu;

	private G selectedParentComponent;

	private JComponent childToolBarComponents;

	public DefaultCRUDWithChildrenPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns,
			Class<C> childComponentClass, ConstantsWithLookingBundle childConstantsWithLookingBundle, String[] childTableColumns) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, null, childComponentClass, childConstantsWithLookingBundle, childTableColumns, false);
	}

	public DefaultCRUDWithChildrenPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns,
			Class<C> childComponentClass, ConstantsWithLookingBundle childConstantsWithLookingBundle, String[] childTableColumns, boolean displayEditOnly) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, null, childComponentClass, childConstantsWithLookingBundle, childTableColumns, displayEditOnly);
	}

	public DefaultCRUDWithChildrenPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns,
			String[] defaultHiddenColumns, Class<C> childComponentClass, ConstantsWithLookingBundle childConstantsWithLookingBundle, String[] childTableColumns) {
		this(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, null, childComponentClass, childConstantsWithLookingBundle, childTableColumns, false);
	}

	public DefaultCRUDWithChildrenPanelDescriptor(RibbonData ribbonData, ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns,
			String[] defaultHiddenColumns, Class<C> childComponentClass, ConstantsWithLookingBundle childConstantsWithLookingBundle, String[] childTableColumns, boolean displayEditOnly) {
		super(ribbonData, constantsWithLookingBundle, filterColumns, tableColumns, defaultHiddenColumns, displayEditOnly);

		this.childComponentClass = childComponentClass;
		this.childConstantsWithLookingBundle = childConstantsWithLookingBundle;
		this.childTableColumns = childTableColumns;
	}

	@SuppressWarnings("unchecked")
	protected final ICRUDWithChildrenManagementController<G, C> getCRUDWithChildrenManagementController() {
		return (ICRUDWithChildrenManagementController<G, C>) getCRUDManagementController();
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
						getCRUDWithChildrenManagementController().loadChildren(getSelectedComponent());

						addChildAction.setEnabled(getCRUDWithChildrenManagementController().hasAuthWriteChildren());
					} else {
						getCRUDWithChildrenManagementController().loadChildren(null);

						addChildAction.setEnabled(false);
					}
				}
			}
		});
	}

	@Override
	protected JComponent createDetailComponent() {
		addChildAction = new AddChildAction();
		addChildAction.setEnabled(false);

		duplicateChildAction = new CloneChildAction();
		duplicateChildAction.setEnabled(false);

		editChildAction = new EditChildAction();
		editChildAction.setEnabled(false);

		deleteChildAction = new DeleteChildAction();
		deleteChildAction.setEnabled(false);

		ToolBarActionsBuilder toolBarActionBuilder = new ToolBarActionsBuilder();
		installChildToolBar(toolBarActionBuilder);
		childToolBarComponents = toolBarActionBuilder.build();

		PopupMenuActionsBuilder popupMenuActionsBuilder = new PopupMenuActionsBuilder();
		installChildPopupMenu(popupMenuActionsBuilder);
		childPopupMenu = popupMenuActionsBuilder.build();

		childComponentTableModel = new DefaultComponentTableModel<C>(childComponentClass, childConstantsWithLookingBundle, childTableColumns);
		childTable = new JSyTable(childComponentTableModel, buildChildTableName());
		installChildTable(childTable);

		return buildChildContents();
	}

	protected String buildChildTableName() {
		return new StringBuilder().append(this.getClass().getName()).append("_").append(getSearchComponentsContext().getClass().getName()).append("_child_").append(childComponentClass.getName())
				.append("_").append(Arrays.toString(childTableColumns)).toString();
	}

	protected void installChildToolBar(IToolBarActionsBuilder builder) {
		builder.addAction(addChildAction);
		builder.addAction(duplicateChildAction);
		builder.addSeparator();
		builder.addAction(editChildAction);
		builder.addAction(deleteChildAction);
	}

	protected void installChildPopupMenu(IPopupMenuActionsBuilder builder) {
		builder.addAction(duplicateChildAction);
		builder.addSeparator();
		builder.addAction(editChildAction);
		builder.addAction(deleteChildAction);
	}

	private JComponent buildChildContents() {
		if (childToolBarComponents != null) {
			FormLayout layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:100DLU:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			CellConstraints cc = new CellConstraints();
			builder.add(childToolBarComponents, cc.xy(1, 1));
			builder.add(new JSyScrollPane(childTable), cc.xy(1, 3));
			return builder.getPanel();
		} else {
			FormLayout layout = new FormLayout("FILL:default:GROW(1.0)", "FILL:100DLU:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout/* , new FormDebugPanel() */);
			CellConstraints cc = new CellConstraints();
			builder.add(new JSyScrollPane(childTable), cc.xy(1, 1));
			return builder.getPanel();
		}
	}

	protected void installChildTable(JSyTable table) {
		childTable.setShowTableLines(true);
		childTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		childTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (childTable.getSelectedRowCount() == 1) {
						duplicateChildAction.setEnabled(getCRUDWithChildrenManagementController().hasAuthWriteChildren());
						editChildAction.setEnabled(getCRUDWithChildrenManagementController().hasAuthWriteChildren());
						deleteChildAction.setEnabled(getCRUDWithChildrenManagementController().hasAuthWriteChildren());
					} else {
						duplicateChildAction.setEnabled(false);
						editChildAction.setEnabled(false);
						deleteChildAction.setEnabled(false);
					}
				}
			}
		});

		childTable.addMouseListener(new PopupMenuMouseListener(childPopupMenu) {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && childTable.getSelectedRowCount() == 1) {
					editChildAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});
	}

	protected final Action getAddChildAction() {
		return addChildAction;
	}

	protected final Action getEditChildAction() {
		return editChildAction;
	}

	protected final Action getDuplicateChildAction() {
		return duplicateChildAction;
	}

	protected final Action getDeleteChildAction() {
		return deleteChildAction;
	}

	protected final C getSelectedChildComponent() {
		if (childTable.getSelectedRowCount() == 1) {
			return childComponentTableModel.getComponent(childTable.convertRowIndexToModel(childTable.getSelectedRow()));
		}
		return null;
	}

	protected final List<C> getSelectedChildComponents() {
		if (childTable.getSelectedRowCount() < 1) {
			return null;
		}
		List<C> res = new ArrayList<C>();
		for (int i = 0; i < childTable.getSelectedRowCount(); i++) {
			res.add(childComponentTableModel.getComponent(childTable.convertRowIndexToModel(i)));
		}
		return res;
	}

	public G getSelectedParentComponent() {
		return selectedParentComponent;
	}

	@Override
	public void setChildComponents(G parentComponent, List<C> childComponents) {
		this.selectedParentComponent = parentComponent;
		childComponentTableModel.setComponents(childComponents);
	}

	private class AddChildAction extends AbstractAddAction {

		private static final long serialVersionUID = -2209334623415724937L;

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDWithChildrenManagementController().addChildEntity(getSelectedComponent());
		}
	}

	private class CloneChildAction extends AbstractCloneAction {

		private static final long serialVersionUID = 892920133520692619L;

		public CloneChildAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().cloneEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDWithChildrenManagementController().cloneChildEntity(getSelectedComponent(), getSelectedChildComponent());
		}
	}

	private class EditChildAction extends AbstractEditAction {

		private static final long serialVersionUID = 892920133520692619L;

		public EditChildAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getCRUDWithChildrenManagementController().hasAuthWriteChildren()) {
				getCRUDWithChildrenManagementController().editChildEntity(getSelectedComponent(), getSelectedChildComponent());
			} else {
				getCRUDWithChildrenManagementController().showChildEntity(getSelectedComponent(), getSelectedChildComponent());
			}
		}
	}

	private class DeleteChildAction extends AbstractDeleteAction {

		private static final long serialVersionUID = -4405083037235887225L;

		public DeleteChildAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			getCRUDWithChildrenManagementController().deleteChildEntity(getSelectedComponent(), getSelectedChildComponent());
		}
	}
}
