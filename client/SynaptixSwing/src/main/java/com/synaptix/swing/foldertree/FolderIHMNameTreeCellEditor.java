package com.synaptix.swing.foldertree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

class FolderIHMNameTreeCellEditor<E extends IFolder> extends AbstractCellEditor
		implements TreeCellEditor {

	private static final long serialVersionUID = -6447741139006078789L;

	private JTextField textField;

	public FolderIHMNameTreeCellEditor() {
		super();

		textField = new JTextField();
		// textField.setBorder(null);
		textField.addActionListener(new MyActionListener());
	}

	public Object getCellEditorValue() {
		return textField.getText();
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	@SuppressWarnings("unchecked")
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		FolderIHM<E> c = (FolderIHM<E>) value;
		textField.setText(c.getName());
		return textField;
	}

	private final class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			stopCellEditing();
		}
	}
}
