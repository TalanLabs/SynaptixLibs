package com.synaptix.swing.selection;

public interface XYSelectionModel {

	public static int SINGLE_SELECTION = 0;

	public static int MULTI_SELECTION = 1;

	public void addXYSelectionModelListener(XYSelectionModelListener l);

	public void removeXYSelectionModelListener(XYSelectionModelListener l);

	public boolean isSelected(int x, int y);

	public int getAnchorX();

	public int getAnchorY();

	public int getLeadX();

	public int getLeadY();

	public void setAnchor(int x, int y);

	public void setLead(int x, int y);

	public void clearSelection();

	public boolean isSelectionEmpty();

	public void setSelectionRange(int x0, int y0, int x1, int y1);

	public void addSelectionRange(int x0, int y0, int x1, int y1);

	public void removeSelectionRange(int x0, int y0, int x1, int y1);

	public void setValueIsAdjusting(boolean isAdjusting);

	public boolean getValueIsAdjusting();

	public int getMinSelectionX();

	public int getMinSelectionY();

	public int getMaxSelectionX();

	public int getMaxSelectionY();

	public int getSelectionCount();

	/**
	 * SINGLE_SELECTION or MULTI_SELECTION
	 * 
	 * @param selectionMode
	 */
	public void setSelectionMode(int selectionMode);

	/**
	 * Returns the current selection mode.
	 *
	 * @return the current selection mode
	 * @see #setSelectionMode
	 */
	public int getSelectionMode();

}
