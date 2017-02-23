package com.synaptix.widget.component.view.swing.dialog.edit;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.utils.PopupMenuMouseListener;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractCloneAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.actions.view.swing.AbstractEditAction;
import com.synaptix.widget.component.controller.dialog.edit.ICRUDBeanExtensionDialogContext;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;

public abstract class AbstractCRUDBeanExtensionDialog<E, F extends IComponent> extends AbstractBeanExtensionDialog<E> {

	private static final long serialVersionUID = 490552283185192897L;

	private final Class<F> subEntityClass;

	private final ICRUDBeanExtensionDialogContext<F> crudBeanExtensionDialogContext;

	private final ConstantsWithLookingBundle constantsWithLookingBundle;

	private final String[] tableColumns;

	private Action addAction;

	private Action cloneAction;

	private Action editAction;

	private Action deleteAction;

	private JPopupMenu popupMenu;

	private DefaultComponentTableModel<F> defaultComponentTableModel;

	private JSyTable table;

	private ValidationListener validationListener;

	public AbstractCRUDBeanExtensionDialog(String title, Class<F> subEntityClass, ICRUDBeanExtensionDialogContext<F> crudBeanExtensionDialogContext,
			ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		super(title);

		this.subEntityClass = subEntityClass;
		this.crudBeanExtensionDialogContext = crudBeanExtensionDialogContext;
		this.constantsWithLookingBundle = constantsWithLookingBundle;
		this.tableColumns = tableColumns;
	}

	@Override
	protected final void initComponents() {
		addAction = new AddAction();
		addAction.setEnabled(false);
		cloneAction = new CloneAction();
		cloneAction.setEnabled(false);
		editAction = new EditAction();
		editAction.setEnabled(false);
		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);

		popupMenu = new JPopupMenu();
		popupMenu.add(cloneAction);
		popupMenu.add(editAction);
		popupMenu.addSeparator();
		popupMenu.add(deleteAction);

		defaultComponentTableModel = new DefaultComponentTableModel<F>(subEntityClass, constantsWithLookingBundle, tableColumns);
		table = new JSyTable(defaultComponentTableModel, this.getClass().getName() + "_" + getTitle());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					cloneAction.setEnabled(!readOnly && table.getSelectedRowCount() == 1);
					editAction.setEnabled(!readOnly && table.getSelectedRowCount() == 1);
					deleteAction.setEnabled(!readOnly && table.getSelectedRowCount() > 0);
				}
			}
		});
		table.addMouseListener(new PopupMenuMouseListener(popupMenu) {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && table.getSelectedRowCount() == 1) {
					editAction.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, ""));
				}
			}
		});

		validationListener = new ValidationListener(this);

		installTable(table);
	}

	protected void installTable(JSyTable table) {
	}

	/**
	 * Get the column by its column id in the given table
	 * 
	 * @param table
	 * @param columnId
	 * @return
	 */
	protected final SyTableColumn getTableColumn(JSyTable table, String columnId) {
		AbstractSimpleSpecialTableModel tableModel = (AbstractSimpleSpecialTableModel) table.getModel();
		return (SyTableColumn) table.getYColumnModel().getColumn(tableModel.findColumnIndexById(columnId), true);
	}

	protected final DefaultComponentTableModel<F> getDefaultComponentTableModel() {
		return defaultComponentTableModel;
	}

	protected final JSyTable getTable() {
		return table;
	}

	@Override
	protected final JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:250DLU:GROW(1.0)", //$NON-NLS-1$
				""); //$NON-NLS-1$
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(ToolBarFactory.buildToolBar(addAction, cloneAction, null, editAction, deleteAction), builder.getColumnCount());
		builder.appendRow(builder.getLineGapSpec());
		builder.nextLine(2);
		builder.appendRow("FILL:80DLU:GROW(1.0)");
		builder.append(new JSyScrollPane(table), builder.getColumnCount());
		return builder.getPanel();
	}

	@Override
	public final void openDialog() {
		validationListener.setEnabled(false);

		defaultComponentTableModel.setComponents(loadList());

		validationListener.setEnabled(true);

		addAction.setEnabled(!readOnly);

		updateValidation();
	}

	@Override
	public void setReadOnly(boolean readOnly){
		super.setReadOnly(readOnly);
		addAction.setEnabled(!readOnly);
		cloneAction.setEnabled(!readOnly);
		editAction.setEnabled(!readOnly);
		deleteAction.setEnabled(!readOnly);
	}

	protected abstract List<F> loadList();

	@Override
	public final void commit(E bean, Map<String, Object> valueMap) {
		List<F> res = new ArrayList<F>();
		if (defaultComponentTableModel.getComponentList() != null && !defaultComponentTableModel.getComponentList().isEmpty()) {
			res.addAll(defaultComponentTableModel.getComponentList());
		}
		saveList(res);
	}

	protected abstract void saveList(List<F> list);

	@Override
	protected void updateValidation(ValidationResult result) {
	}

	private final class AddAction extends AbstractAddAction {

		private static final long serialVersionUID = -7340117570424711750L;

		@Override
		public void actionPerformed(ActionEvent e) {
			crudBeanExtensionDialogContext.addEntity(AbstractCRUDBeanExtensionDialog.this, new IResultCallback<F>() {
				@Override
				public void setResult(F e) {
					List<F> res = defaultComponentTableModel.getComponentList();
					if (res == null) {
						res = new ArrayList<F>();
					}
					res.add(e);
					defaultComponentTableModel.setComponents(res);

					updateValidation();
				}
			});
		}
	}

	private final class EditAction extends AbstractEditAction {

		private static final long serialVersionUID = -7340117570424711750L;

		public EditAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			F sub = defaultComponentTableModel.getComponent(table.convertRowIndexToModel(table.getSelectedRow()));
			if (readOnly) {
				crudBeanExtensionDialogContext.showEntity(AbstractCRUDBeanExtensionDialog.this, sub);
			} else {
				crudBeanExtensionDialogContext.editEntity(AbstractCRUDBeanExtensionDialog.this, sub, new IResultCallback<F>() {
					@Override
					public void setResult(F e) {
						List<F> res = defaultComponentTableModel.getComponentList();
						res.set(res.indexOf(e), e);
						defaultComponentTableModel.setComponents(res);

						updateValidation();
					}
				});
			}
		}
	}

	private final class CloneAction extends AbstractCloneAction {

		private static final long serialVersionUID = -7340117570424711750L;

		public CloneAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().cloneEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			F sub = defaultComponentTableModel.getComponent(table.convertRowIndexToModel(table.getSelectedRow()));
			crudBeanExtensionDialogContext.cloneEntity(AbstractCRUDBeanExtensionDialog.this, sub, new IResultCallback<F>() {
				@Override
				public void setResult(F e) {
					List<F> res = defaultComponentTableModel.getComponentList();
					if (res == null) {
						res = new ArrayList<F>();
					}
					res.add(e);
					defaultComponentTableModel.setComponents(res);

					updateValidation();
				}
			});
		}
	}

	protected void deleteComponent(F component) {
	}

	private final class DeleteAction extends AbstractDeleteAction {

		private static final long serialVersionUID = 1269332096754128863L;

		public DeleteAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().deleteEllipsis());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<F> tts = new ArrayList<F>();
			for (int i : table.getSelectedRows()) {
				tts.add(defaultComponentTableModel.getComponent(table.convertRowIndexToModel(i)));
			}

			List<F> res = defaultComponentTableModel.getComponentList();
			for (F tt : tts) {
				deleteComponent(tt);
				res.remove(tt);
			}
			defaultComponentTableModel.setComponents(res);

			updateValidation();
		}
	}
}
