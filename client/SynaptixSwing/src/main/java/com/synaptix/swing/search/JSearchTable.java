package com.synaptix.swing.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.Column;
import com.synaptix.swing.table.TableRowRenderer;

public class JSearchTable extends JPanel implements ISearchTable {

	private static final long serialVersionUID = 8285829081267888610L;

	private MySyTableModel tableModel;

	private JSyTable table;

	private JSyScrollPane scrollPane;

	private Action doubleClickAction;

	private JPopupMenu popupMenu;

	private String save;

	private SearchTableModel model;

	private ISearchTableRowHighlight searchTableRowHighlight;

	public JSearchTable(SearchTableModel model, String save) {
		super(new BorderLayout());

		this.model = model;
		this.save = save;

		doubleClickAction = null;

		initComponent();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponent() {
		tableModel = new MySyTableModel();
		table = new JSyTable(tableModel, save + "Table"); //$NON-NLS-1$
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setShowTableLines(true);
		table.addMouseListener(new TableMouseListener());
		table.setTableRowRenderer(new MyTableRowRenderer());

		scrollPane = new JSyScrollPane(table);
		scrollPane.addMouseListener(new ScrollPaneMouseListener());
	}

	private JComponent buildContents() {
		return scrollPane;
	}

	public void setSearchTableRowHighlight(
			ISearchTableRowHighlight searchTableRowHighlight) {
		this.searchTableRowHighlight = searchTableRowHighlight;
	}

	public ISearchTableRowHighlight getSearchTableRowHighlight() {
		return searchTableRowHighlight;
	}

	public void clearTable() {
		tableModel.clear();
	}

	public void setResult(Result result) {
		tableModel.setResult(result);
	}

	public JSyTable getTable() {
		return table;
	}

	public void setSelectionMode(int selectionMode) {
		table.setSelectionMode(selectionMode);
	}

	public int getSelectionMode() {
		return table.getSelectionModel().getSelectionMode();
	}

	public int getSelectedRow() {
		return table.convertRowIndexToModel(table.getSelectedRow());
	}

	public int[] getSelectedRows() {
		int[] res = new int[table.getSelectedRowCount()];
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			res[i] = table.convertRowIndexToModel(rows[i]);
		}
		return res;
	}

	public int getSelectedRowCount() {
		return table.getSelectedRowCount();
	}

	public List<Object> getRowAt(int row) {
		return tableModel.getRowAt(row);
	}

	public Result getResult() {
		return tableModel.getResult();
	}

	public void addListSelectionListener(ListSelectionListener x) {
		table.getSelectionModel().addListSelectionListener(x);
	}

	public void removeListSelectionListener(ListSelectionListener x) {
		table.getSelectionModel().removeListSelectionListener(x);
	}

	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	private final class MySyTableModel extends AbstractSimpleSpecialTableModel {

		private static final long serialVersionUID = -878825691309640954L;

		private Result result;

		public MySyTableModel() {
			result = null;
		}

		public void clear() {
			result = null;
			fireTableDataChanged();
		}

		public void setResult(Result result) {
			this.result = result;
			fireTableDataChanged();
		}

		public Result getResult() {
			return result;
		}

		public int getColumnCount() {
			return model.getColumnCount();
		}

		public Column getColumn(int index) {
			return model.getColumn(index);
		}

		public int getRowCount() {
			if (result != null) {
				return result.getRowCount();
			}
			return 0;
		}

		public List<Object> getRowAt(int rowIndex) {
			List<Object> row = new ArrayList<Object>();
			for (int i = 0; i < result.getColumnCount(); i++) {
				row.add(result.getValue(rowIndex, i));
			}
			return row;
		}

		public Object getValueAt(int rowIndex, String columnId) {
			if (result != null) {
				int index = findResultColumnIndexById(columnId);
				if (index >= 0) {
					return result.getValue(rowIndex, index);
				}
			}
			return null;
		}

		public Object getSumValueAt(String columnId) {
			if (result != null) {
				int index = findResultColumnIndexById(columnId);
				if (index >= 0) {
					return result.getSumValueAt(index);
				}
			}
			return null;
		}

		private int findResultColumnIndexById(String id) {
			int res = -1;
			int i = 0;
			while (i < result.getColumnCount() && res == -1) {
				if (id.equals(result.getColumnId(i))) {
					res = i;
				}
				i++;
			}
			return res;
		}
	}

	private final class ScrollPaneMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			table.getSelectionModel().clearSelection();
		}
	}

	private final class TableMouseListener extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
				if (!table.getSelectionModel().isSelectionEmpty()) {
					if (doubleClickAction != null
							&& doubleClickAction.isEnabled()) {
						doubleClickAction.actionPerformed(new ActionEvent(this,
								0, "doubleClick")); //$NON-NLS-1$
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && popupMenu != null) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && popupMenu != null) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class MyTableRowRenderer implements TableRowRenderer {

		@Override
		public Component getTableRowRenderer(Component c, JTable table,
				boolean isSelected, boolean hasFocus, int viewRowIndex,
				int viewColIndex) {

			if (searchTableRowHighlight != null
					&& tableModel.getResult() != null) {
				int rowModel = table.convertRowIndexToModel(viewRowIndex);
				int columnModel = table.convertColumnIndexToModel(viewColIndex);

				Color foreground = searchTableRowHighlight.getForegroundColor(
						JSearchTable.this, tableModel.getResult(), isSelected,
						hasFocus, rowModel, viewRowIndex, columnModel,
						viewColIndex);
				if (foreground != null) {
					c.setForeground(foreground);
				} else {
					if (isSelected) {
						c.setForeground(table.getSelectionForeground());
					} else {
						c.setForeground(table.getForeground());
					}
				}

				Color background = searchTableRowHighlight.getBackgroundColor(
						JSearchTable.this, tableModel.getResult(), isSelected,
						hasFocus, rowModel, viewRowIndex, columnModel,
						viewColIndex);
				if (background != null) {
					c.setBackground(background);
				} else {
					if (isSelected) {
						c.setBackground(table.getSelectionBackground());
					} else {
						c.setBackground(table.getBackground());
					}
				}

				Font font = searchTableRowHighlight.getFont(JSearchTable.this,
						tableModel.getResult(), isSelected, hasFocus, rowModel,
						viewRowIndex, columnModel, viewColIndex);
				if (font != null) {
					c.setFont(font);
				} else {
					c.setFont(table.getFont());
				}
			}

			return c;
		}
	}
}
