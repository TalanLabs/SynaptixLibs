package com.synaptix.swing.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

import com.synaptix.swing.utils.SwingComponentFactory;

public class DateHourCellEditor extends AbstractCellEditor implements
		TableCellEditor, TreeCellEditor {

	private static final long serialVersionUID = -4991375217294058886L;

	private JFormattedTextField editorComponent;

	public DateHourCellEditor() {
		this(true);
	}

	public DateHourCellEditor(boolean defaultNull) {
		super();

		editorComponent = SwingComponentFactory
				.createDateHourField(false, true);
		editorComponent.setBorder(null);
		editorComponent.addActionListener(new MyActionListener());
	}

	private final class MyActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			stopCellEditing();
		}
	}

	public Object getCellEditorValue() {
		return editorComponent.getValue();
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

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
		String stringValue = tree.convertValueToText(value, isSelected,
				expanded, leaf, row, false);
		editorComponent.setValue(stringValue);
		return editorComponent;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		editorComponent.setValue(value);
		return editorComponent;
	}
}
