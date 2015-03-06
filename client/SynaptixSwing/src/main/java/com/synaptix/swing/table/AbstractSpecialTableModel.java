package com.synaptix.swing.table;

import java.io.Serializable;
import java.util.EventListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class AbstractSpecialTableModel implements SpecialTableModel,
		Serializable {

	private static final long serialVersionUID = -8330832493260081216L;

	protected EventListenerList listenerList = new EventListenerList();

	public String getColumnName(int column) {
		String result = ""; //$NON-NLS-1$
		for (; column >= 0; column = column / 26 - 1) {
			result = (char) ((char) (column % 26) + 'A') + result;
		}
		return result;
	}

	public int findColumn(String columnName) {
		for (int i = 0; i < getColumnCount(); i++) {
			if (columnName.equals(getColumnName(i))) {
				return i;
			}
		}
		return -1;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	public boolean isSearchable(int columnIndex) {
		return true;
	}

	public boolean isSortable(int columnIndex) {
		return true;
	}

	public boolean isDefaultVisible(int columnIndex) {
		return true;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	public void setSumValueAt(Object aValue, int columnIndex) {
	}

	public boolean isExistSumValue(int columnIndex) {
		return false;
	}

	public Object getSumValueAt(int columnIndex) {
		return null;
	}

	public Class<?> getSumColumnClass(int columnIndex) {
		return null;
	}

	public boolean isLock(int columnIndex) {
		return false;
	}

	//
	// Managing Listeners
	//

	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}

	public TableModelListener[] getTableModelListeners() {
		return (TableModelListener[]) listenerList
				.getListeners(TableModelListener.class);
	}

	//
	// Fire methods
	//

	public void fireTableDataChanged() {
		fireTableChanged(new TableModelEvent(this));
	}

	public void fireTableStructureChanged() {
		fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}

	public void fireTableRowsInserted(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}

	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
	}

	public void fireTableCellUpdated(int row, int column) {
		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	public void fireTableChanged(TableModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TableModelListener.class) {
				((TableModelListener) listeners[i + 1]).tableChanged(e);
			}
		}
	}

	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}
}
