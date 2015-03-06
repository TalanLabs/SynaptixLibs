package com.synaptix.widget.grid;

public interface ValueSelectionModel {

	public void addValueSelectionListener(ValueSelectionListener x);

	public void removeValueSelectionListener(ValueSelectionListener x);

	public Object getSelectionCell();

	public Object[] getSelectionCells();

	public void setSelectionCells(Object... values);

	public void addSelectionCells(Object... values);

	public void removeSelectionCells(Object... values);

	public void clearSelection();

	public boolean isSelectedCell(Object values);

	public int getSelectionCount();

	public boolean isSelectionEmpty();

	public void setValueIsAdjusting(boolean valueIsAdjusting);

	public boolean getValueIsAdjusting();

}
