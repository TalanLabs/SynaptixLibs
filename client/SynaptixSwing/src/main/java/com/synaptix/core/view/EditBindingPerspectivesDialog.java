package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.Perspective;
import com.synaptix.core.CoreMessages;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSpecialTableModel;
import com.synaptix.swing.widget.AbstractAcceptAction;
import com.synaptix.swing.widget.AbstractCancelAction;

public class EditBindingPerspectivesDialog extends JPanel {

	private static final long serialVersionUID = -8579355607228946686L;

	public final static int ACCEPT_OPTION = 0;

	public final static int CANCEL_OPTION = 1;

	private final static String TEXT_TITLE = CoreMessages.getString("EditBindingPerspectivesDialog.1"); //$NON-NLS-1$

	private final static String TEXT_SUBTITLE = CoreMessages.getString("EditBindingPerspectivesDialog.2"); //$NON-NLS-1$

	private JDialogModel dialog;

	private int returnValue;

	private Action acceptAction;

	private Action cancelAction;

	private PerspectivesTableModel perspectivesTableModel;

	private JSyTable perspectivesTable;

	private JTextField bindingField;

	private Action deleteAction;

	private JLabel perspectiveNameLabel;

	public EditBindingPerspectivesDialog() {
		super(new BorderLayout());

		initActions();
		initComponents();

		dialog = null;
		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();

		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);
	}

	private void createComponents() {
		perspectivesTableModel = new PerspectivesTableModel();
		perspectivesTable = new JSyTable(perspectivesTableModel);
		bindingField = new JTextField();
		perspectiveNameLabel = new JLabel();
	}

	private void initComponents() {
		createComponents();

		perspectivesTable.setShowTableLines(true);
		perspectivesTable.getSelectionModel().addListSelectionListener(new MyListSelectionListener());

		bindingField.addKeyListener(new MyKeyListener());
		bindingField.setEnabled(false);
		bindingField.setEditable(false);
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:100DLU:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,CENTER:75DLU:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(new JSyScrollPane(perspectivesTable), cc.xywh(3, 1, 6, 2));
		builder.addLabel(CoreMessages.getString("EditBindingPerspectivesDialog.4"), cc.xy(1, 1)); //$NON-NLS-1$
		builder.addLabel(CoreMessages.getString("EditBindingPerspectivesDialog.5"), cc.xy(1, 6)); //$NON-NLS-1$
		builder.add(bindingField, cc.xy(3, 6));
		builder.add(new JButton(deleteAction), cc.xy(5, 6));
		builder.addLabel(CoreMessages.getString("EditBindingPerspectivesDialog.6"), cc.xy(1, 4)); //$NON-NLS-1$
		builder.add(perspectiveNameLabel, cc.xy(3, 4));
		return builder.getPanel();
	}

	public int showDialog(Component parent, List<Perspective> perspectives) {
		returnValue = CANCEL_OPTION;

		List<Perspective> ps = new ArrayList<Perspective>();
		for (Perspective perspective : perspectives) {
			ps.add(perspective.clone());
		}

		perspectivesTableModel.setPerspectives(ps);

		dialog = new JDialogModel(parent, TEXT_TITLE, TEXT_SUBTITLE, this, new Action[] { acceptAction, cancelAction }, cancelAction);

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public List<Perspective> getPerspectives() {
		return perspectivesTableModel.getPerspectives();
	}

	private String keyStrokeToString(KeyStroke keyStroke) {
		StringBuilder sb = new StringBuilder();
		if (keyStroke.getModifiers() != 0) {
			sb.append(InputEvent.getModifiersExText(keyStroke.getModifiers()));
			sb.append("+"); //$NON-NLS-1$
		}
		sb.append(KeyEvent.getKeyText(keyStroke.getKeyCode()));
		return sb.toString();
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = -3550314575995085886L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 5222391683335543617L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = 1471023304063769196L;

		public DeleteAction() {
			super(CoreMessages.getString("EditBindingPerspectivesDialog.10")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			bindingField.setText(null);

			int i = perspectivesTable.convertRowIndexToModel(perspectivesTable.getSelectedRow());
			Perspective perspective = perspectivesTableModel.getRow(i);
			perspective.setBinding(null);

			perspectivesTableModel.updateRow(i);
		}
	}

	private final class MyListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				perspectiveNameLabel.setEnabled(false);
				bindingField.setEnabled(false);
				deleteAction.setEnabled(false);

				if (perspectivesTable.getSelectedRowCount() == 1) {
					int i = perspectivesTable.convertRowIndexToModel(perspectivesTable.getSelectedRow());
					Perspective perspective = perspectivesTableModel.getRow(i);

					perspectiveNameLabel.setEnabled(true);
					bindingField.setEnabled(true);
					deleteAction.setEnabled(true);

					perspectiveNameLabel.setText(perspective.getName());
					bindingField.setText(perspective.getBinding() != null ? keyStrokeToString(KeyStroke.getKeyStroke(perspective.getBinding())) : null);
				}
			}
		}
	}

	private final class MyKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH) {
			} else {
				KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
				bindingField.setText(keyStrokeToString(keyStroke));

				int i = perspectivesTable.convertRowIndexToModel(perspectivesTable.getSelectedRow());
				Perspective perspective = perspectivesTableModel.getRow(i);
				perspective.setBinding(keyStroke.toString());

				perspectivesTableModel.updateRow(i);

				e.consume();
			}
		}
	}

	private final class PerspectivesTableModel extends AbstractSpecialTableModel {

		private static final long serialVersionUID = -1203678415057881853L;

		private String[] colomns = { CoreMessages.getString("EditBindingPerspectivesDialog.6"), CoreMessages.getString("EditBindingPerspectivesDialog.5") }; //$NON-NLS-1$ //$NON-NLS-2$

		private List<Perspective> perspectives;

		public void setPerspectives(List<Perspective> perspectives) {
			this.perspectives = perspectives;

			fireTableDataChanged();
		}

		public List<Perspective> getPerspectives() {
			return perspectives;
		}

		public void updateRow(int rowIndex) {
			fireTableRowsUpdated(rowIndex, rowIndex);
		}

		public Perspective getRow(int rowIndex) {
			return perspectives.get(rowIndex);
		}

		@Override
		public int getColumnCount() {
			return colomns.length;
		}

		@Override
		public String getColumnId(int columnIndex) {
			return getColumnName(columnIndex);
		}

		@Override
		public String getColumnName(int column) {
			return colomns[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isLock(int columnIndex) {
			return true;
		}

		@Override
		public int getRowCount() {
			if (perspectives != null) {
				return perspectives.size();
			}
			return 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object res = null;

			Perspective perspective = perspectives.get(rowIndex);
			switch (columnIndex) {
			case 0:
				res = perspective.getName();
				break;
			case 1:
				res = perspective.getBinding() != null ? keyStrokeToString(KeyStroke.getKeyStroke(perspective.getBinding())) : null;
				break;
			}

			return res;
		}
	}
}
