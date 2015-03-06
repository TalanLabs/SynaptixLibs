package com.synaptix.widget.calendarday;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.synaptix.swing.selection.DefaultXYSelectionModel;
import com.synaptix.swing.selection.XYSelectionModel;
import com.synaptix.swing.selection.XYSelectionModelEvent;
import com.synaptix.swing.selection.XYSelectionModelListener;
import com.synaptix.widget.calendarday.plaf.BasicCalendarDayUI;

public class JCalendarDay extends JComponent implements CalendarDayModelListener, XYSelectionModelListener, Scrollable {

	private static final long serialVersionUID = -6522817738052318881L;

	private static final String uiClassID = "CalendarDayUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicCalendarDayUI.class.getName());
	}

	public static final int HORIZONTAL_SELECTION_MODE = 0;

	public static final int VERTICAL_SELECTION_MODE = 1;

	public static final int RECTANGLE_SELECTION_MODE = 2;

	private int columnWidth = 90;

	private int columnHeaderHeight = 30;

	private int rowHeight = 20;

	private int rowHeaderWidth = 20;

	private CalendarDayCellRenderer cellRenderer;

	private CalendarDayModel model;

	private Color gridColor = Color.black;

	private boolean showGrid = true;

	private XYSelectionModel selectionModel;

	private int selectionMode = VERTICAL_SELECTION_MODE;

	private boolean showColumnHeaderInner = true;

	private JCalendarDayColumnHeader calendarDayColumnHeader;

	private boolean showRowHeaderInner = true;

	private JCalendarDayRowHeader calendarDayRowHeader;

	private int rowCount = -1;

	private Dimension preferredScrollableViewportSize;

	public JCalendarDay() {
		this(new DefaultYearCalendarDayModel());
	}

	public JCalendarDay(CalendarDayModel model) {
		super();

		this.cellRenderer = createCellRenderer();

		setModel(model);
		setSelectionModel(createSelectionModel());

		this.calendarDayColumnHeader = createCalendarDayColumnHeader();
		this.calendarDayRowHeader = createCalendarDayRowHeader();

		this.setFocusable(true);

		setPreferredScrollableViewportSize(new Dimension(450, 400));

		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int columnWidth) {
		int oldValue = this.columnWidth;
		this.columnWidth = columnWidth;
		firePropertyChange("columnWidth", oldValue, columnWidth);
	}

	public int getColumnHeaderHeight() {
		return columnHeaderHeight;
	}

	public void setColumnHeaderHeight(int columnHeaderHeight) {
		int oldValue = this.columnHeaderHeight;
		this.columnHeaderHeight = columnHeaderHeight;
		firePropertyChange("columnHeaderHeight", oldValue, columnHeaderHeight);
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		int oldValue = this.rowHeight;
		this.rowHeight = rowHeight;
		firePropertyChange("rowHeight", oldValue, rowHeight);
	}

	public int getRowHeaderWidth() {
		return rowHeaderWidth;
	}

	public void setRowHeaderWidth(int rowHeaderWidth) {
		int oldValue = this.rowHeaderWidth;
		this.rowHeaderWidth = rowHeaderWidth;
		firePropertyChange("rowHeaderWidth", oldValue, rowHeaderWidth);
	}

	protected CalendarDayCellRenderer createCellRenderer() {
		return new DefaultYearCalendarDayCellRenderer();
	}

	public CalendarDayCellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(CalendarDayCellRenderer cellRenderer) {
		CalendarDayCellRenderer oldValue = this.cellRenderer;
		this.cellRenderer = cellRenderer;
		firePropertyChange("cellRenderer", oldValue, cellRenderer);
	}

	public CalendarDayModel getModel() {
		return model;
	}

	public void setModel(CalendarDayModel model) {
		CalendarDayModel oldValue = this.model;
		if (this.model != null) {
			this.model.removeCalendarDayModelListener(this);
		}
		this.model = model;
		this.model.addCalendarDayModelListener(this);

		firePropertyChange("model", oldValue, model);
	}

	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		boolean oldValue = this.showGrid;
		this.showGrid = showGrid;
		firePropertyChange("showGrid", oldValue, showGrid);
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		Color oldValue = this.gridColor;
		this.gridColor = gridColor;
		firePropertyChange("gridColor", oldValue, gridColor);
	}

	protected XYSelectionModel createSelectionModel() {
		return new DefaultXYSelectionModel();
	}

	public XYSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(XYSelectionModel selectionModel) {
		XYSelectionModel oldValue = this.selectionModel;
		if (this.selectionModel != null) {
			this.selectionModel.removeXYSelectionModelListener(this);
		}
		this.selectionModel = selectionModel;
		this.selectionModel.addXYSelectionModelListener(this);
		firePropertyChange("selectionModel", oldValue, selectionModel);
	}

	public int getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(int selectionMode) {
		int oldValue = this.selectionMode;
		this.selectionMode = selectionMode;
		firePropertyChange("selectionMode", oldValue, selectionMode);
	}

	public boolean isShowColumnHeaderInner() {
		return showColumnHeaderInner;
	}

	public void setShowColumnHeaderInner(boolean showColumnHeaderInner) {
		boolean oldValue = this.showColumnHeaderInner;
		this.showColumnHeaderInner = showColumnHeaderInner;
		firePropertyChange("showColumnHeaderInner", oldValue, showColumnHeaderInner);
	}

	public boolean isShowRowHeaderInner() {
		return showRowHeaderInner;
	}

	public void setShowRowHeaderInner(boolean showRowHeaderInner) {
		boolean oldValue = this.showRowHeaderInner;
		this.showRowHeaderInner = showRowHeaderInner;
		firePropertyChange("showRowHeaderInner", oldValue, showRowHeaderInner);
	}

	protected JCalendarDayColumnHeader createCalendarDayColumnHeader() {
		return new JCalendarDayColumnHeader(this);
	}

	public JCalendarDayColumnHeader getCalendarDayColumnHeader() {
		return calendarDayColumnHeader;
	}

	public void setCalendarDayColumnHeader(JCalendarDayColumnHeader calendarDayColumnHeader) {
		JCalendarDayColumnHeader oldValue = this.calendarDayColumnHeader;
		this.calendarDayColumnHeader = calendarDayColumnHeader;
		firePropertyChange("calendarDayColumnHeader", oldValue, calendarDayColumnHeader);
	}

	protected JCalendarDayRowHeader createCalendarDayRowHeader() {
		return null;
	}

	public JCalendarDayRowHeader getCalendarDayRowHeader() {
		return calendarDayRowHeader;
	}

	public void setCalendarDayRowHeader(JCalendarDayRowHeader calendarDayRowHeader) {
		JCalendarDayRowHeader oldValue = this.calendarDayRowHeader;
		this.calendarDayRowHeader = calendarDayRowHeader;
		firePropertyChange("calendarDayRowHeader", oldValue, calendarDayRowHeader);
	}

	protected void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public int getColumnCount() {
		return model.getColumnCount();
	}

	public int getRowCount() {
		if (rowCount == -1) {
			rowCount = 0;
			for (int i = 0; i < getColumnCount(); i++) {
				rowCount = Math.max(model.getRowCount(i), rowCount);
			}
		}
		return rowCount;
	}

	@Override
	public void calendarDayChanged(CalendarDayModelEvent e) {
		rowCount = -1;
		selectionModel.setValueIsAdjusting(true);
		selectionModel.clearSelection();
		selectionModel.setAnchor(-1, -1);
		selectionModel.setLead(-1, -1);
		selectionModel.setValueIsAdjusting(false);
		resizeAndRepaint();
	}

	public int columnAtPoint(Point point) {
		int rowHeaderWidth = isShowRowHeaderInner() && getCalendarDayRowHeader() != null ? getRowHeaderWidth() : 0;
		int c = (point.x - rowHeaderWidth) / getColumnWidth();
		return c >= 0 && c < getColumnCount() ? c : -1;
	}

	public int rowAtPoint(Point point) {
		int columnHeaderHeight = isShowColumnHeaderInner() && getCalendarDayColumnHeader() != null ? getColumnHeaderHeight() : 0;
		int r = (point.y - columnHeaderHeight) / getRowHeight();
		return r >= 0 && r < getRowCount() ? r : -1;
	}

	public Rectangle getCellRect(int row, int column) {
		Rectangle r = new Rectangle();
		if (column >= 0 && column < model.getColumnCount()) {
			if (row >= 0 && row < model.getRowCount(column)) {
				int rowHeaderWidth = isShowRowHeaderInner() && getCalendarDayRowHeader() != null ? getRowHeaderWidth() : 0;
				int columnHeaderHeight = isShowColumnHeaderInner() && getCalendarDayColumnHeader() != null ? getColumnHeaderHeight() : 0;
				r.setBounds(column * getColumnWidth() + rowHeaderWidth, row * getRowHeight() + columnHeaderHeight, getColumnWidth(), getRowHeight());
			}
		}
		return r;
	}

	@Override
	public void selectionChanged(XYSelectionModelEvent e) {
		int firstX = limit(e.getFirstX(), 0, model.getColumnCount() - 1);
		int lastX = limit(e.getLastX(), 0, model.getColumnCount() - 1);
		Rectangle firstRowRect = getCellRect(0, firstX);
		for (int i = firstX; i <= lastX; i++) {
			Rectangle lastRowRect = getCellRect(model.getRowCount(i) - 1, i);
			firstRowRect = firstRowRect.union(lastRowRect);
		}
		// int lastY = model.getRowCount(lastX) - 1;
		// Rectangle firstRowRect = getCellRect(firstY, firstX);
		// Rectangle lastRowRect = getCellRect(lastY, lastX);
		// Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
		repaint(firstRowRect);
	}

	private int limit(int i, int a, int b) {
		return Math.min(b, Math.max(i, a));
	}

	public void addNotify() {
		super.addNotify();
		configureEnclosingScrollPane();
	}

	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(getCalendarDayColumnHeader());
				scrollPane.setRowHeaderView(getCalendarDayRowHeader());

				setShowColumnHeaderInner(false);
				setShowRowHeaderInner(false);
			}
		}
	}

	public void removeNotify() {
		unconfigureEnclosingScrollPane();
		super.removeNotify();
	}

	protected void unconfigureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(null);
				scrollPane.setRowHeaderView(null);

				setShowColumnHeaderInner(true);
				setShowRowHeaderInner(true);
			}
		}
	}

	public void setPreferredScrollableViewportSize(Dimension preferredScrollableViewportSize) {
		this.preferredScrollableViewportSize = preferredScrollableViewportSize;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return preferredScrollableViewportSize;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		int rowHeight = getRowHeight();
		int columnWidth = getColumnWidth();
		int columnHeaderHeight = isShowColumnHeaderInner() && getCalendarDayColumnHeader() != null ? getColumnHeaderHeight() : 0;
		int rowHeaderWidth = isShowRowHeaderInner() && getCalendarDayRowHeader() != null ? getRowHeaderWidth() : 0;
		int row = (visibleRect.y - columnHeaderHeight) / rowHeight;
		int column = (visibleRect.x - rowHeaderWidth) / columnWidth;
		int realX = column * columnWidth + rowHeaderWidth;
		int realY = row * rowHeight + columnHeaderHeight;
		if (orientation == SwingConstants.VERTICAL) {
			if (visibleRect.y == realY) {
				return rowHeight;
			} else {
				if (direction < 0) {
					return visibleRect.y - realY;
				} else {
					return (realY + rowHeight) - visibleRect.y;
				}
			}
		} else {
			if (visibleRect.x == realX) {
				return columnWidth;
			} else {
				if (direction < 0) {
					return visibleRect.x - realX;
				} else {
					return (realX + columnWidth) - visibleRect.x;
				}
			}
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void changeSelection(int row, int column, boolean toggle, boolean extend) {
		CalendarDayModel model = getModel();
		XYSelectionModel selectionModel = getSelectionModel();
		if (extend) {
			int anchorX = selectionModel.getAnchorX();
			if (anchorX == -1) {
				anchorX = 0;
			}
			int anchorY = selectionModel.getAnchorY();
			if (anchorY == -1) {
				anchorY = 0;
			}

			if (row != -1 && column != -1 && row < model.getRowCount(column)) {
				int selectionMode = getSelectionMode();
				if (selectionMode == JCalendarDay.VERTICAL_SELECTION_MODE) {
					if (toggle) {
						selectionModel.addSelectionRange(anchorX, anchorY, anchorX, anchorX == column ? row : (anchorX < column ? model.getRowCount(anchorX) - 1 : 0));
					} else {
						selectionModel.setSelectionRange(anchorX, anchorY, anchorX, anchorX == column ? row : (anchorX < column ? model.getRowCount(anchorX) - 1 : 0));
					}
					if (anchorX != column) {
						for (int i = Math.min(anchorX, column) + 1; i <= Math.max(anchorX, column) - 1; i++) {
							selectionModel.addSelectionRange(i, 0, i, model.getRowCount(i) - 1);
						}
						selectionModel.addSelectionRange(column, row, column, anchorX < column ? 0 : model.getRowCount(anchorX) - 1);
					}
				} else if (selectionMode == JCalendarDay.HORIZONTAL_SELECTION_MODE) {
					int columnCount = model.getColumnCount();

					int i1 = Math.min(anchorX, anchorY == row ? column : (anchorY < row ? columnCount - 1 : 0));
					int i2 = Math.max(anchorX, anchorY == row ? column : (anchorY < row ? columnCount - 1 : 0));
					boolean first = true;
					for (int i = i1; i <= i2; i++) {
						int maxRow = model.getRowCount(i);
						if (anchorY < maxRow) {
							if (!first || toggle) {
								selectionModel.addSelectionRange(i, anchorY, i, anchorY);
							} else {
								selectionModel.setSelectionRange(i, anchorY, i, anchorY);
								first = false;
							}
						}
					}

					if (anchorY != row) {
						for (int j = Math.min(anchorY, row) + 1; j <= Math.max(anchorY, row) - 1; j++) {
							for (int i = 0; i < columnCount; i++) {
								int maxRow = model.getRowCount(i);
								if (j < maxRow) {
									selectionModel.addSelectionRange(i, j, i, j);
								}
							}
						}

						int i3 = Math.min(column, anchorY > row ? columnCount - 1 : 0);
						int i4 = Math.max(column, anchorY > row ? columnCount - 1 : 0);
						for (int i = i3; i <= i4; i++) {
							int maxRow = model.getRowCount(i);
							if (row < maxRow) {
								selectionModel.addSelectionRange(i, row, i, row);
							}
						}
					}
				} else if (selectionMode == JCalendarDay.RECTANGLE_SELECTION_MODE) {
					boolean first = true;
					for (int i = Math.min(anchorX, column); i <= Math.max(anchorX, column); i++) {
						for (int j = Math.min(anchorY, row); j <= Math.max(anchorY, row); j++) {
							if (j < model.getRowCount(i)) {
								if (!first || toggle) {
									selectionModel.addSelectionRange(i, j, i, j);
								} else {
									selectionModel.setSelectionRange(i, j, i, j);
									first = false;
								}
							}
						}
					}
				}
				selectionModel.setAnchor(anchorX, anchorY);
			}
		} else {
			if (toggle) {
				if (row != -1 && column != -1 && row < model.getRowCount(column)) {
					if (selectionModel.isSelected(column, row)) {
						selectionModel.removeSelectionRange(column, row, column, row);
					} else {
						selectionModel.addSelectionRange(column, row, column, row);
					}
				}
			} else {
				if (row != -1 && column != -1 && row < model.getRowCount(column)) {
					selectionModel.setSelectionRange(column, row, column, row);
				} else {
					selectionModel.clearSelection();
				}
			}
		}
	}
}
