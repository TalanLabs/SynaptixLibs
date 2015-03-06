package com.synaptix.swing.table;

import java.io.Serializable;

import javax.swing.event.EventListenerList;

public abstract class AbstractSimpleSpecialTableModel extends AbstractSpecialTableModel implements SimpleSpecialTableModel, Serializable {

	private static final long serialVersionUID = 3033617032605045453L;

	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getColumn(columnIndex).getColumnClass();
	}

	@Override
	public String getColumnId(int column) {
		return getColumn(column).getId();
	}

	@Override
	public String getColumnName(int column) {
		return getColumn(column).getName();
	}

	@Override
	public Class<?> getSumColumnClass(int columnIndex) {
		return getColumn(columnIndex).getSumClass();
	}

	@Override
	public boolean isExistSumValue(int columnIndex) {
		return getColumn(columnIndex).isExistSumValue();
	}

	@Override
	public boolean isDefaultVisible(int columnIndex) {
		return getColumn(columnIndex).isDefaultVisible();
	}

	@Override
	public boolean isLock(int columnIndex) {
		return getColumn(columnIndex).isLock();
	}

	@Override
	public boolean isSearchable(int columnIndex) {
		return getColumn(columnIndex).isSearchable();
	}

	@Override
	public boolean isSortable(int columnIndex) {
		return getColumn(columnIndex).isSortable();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getColumn(columnIndex).isEditable();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getValueAt(rowIndex, getColumn(columnIndex).getId());
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		setValueAt(value, rowIndex, getColumn(columnIndex).getId());
	}

	@Override
	public void setValueAt(Object value, int rowIndex, String columnId) {
	}

	@Override
	public void setSumValueAt(Object value, int columnIndex) {
		setSumValueAt(value, getColumn(columnIndex).getId());
	}

	@Override
	public void setSumValueAt(Object aValue, String columnId) {
	}

	@Override
	public Object getSumValueAt(int columnIndex) {
		return getSumValueAt(getColumn(columnIndex).getId());
	}

	@Override
	public Object getSumValueAt(String columnId) {
		return null;
	}

	public int findColumnIndexById(String id) {
		int res = -1;
		int i = 0;
		while (i < getColumnCount() && res == -1) {
			Column c = getColumn(i);
			if (c.getId().equals(id)) {
				res = i;
			}
			i++;
		}
		return res;
	}
}
