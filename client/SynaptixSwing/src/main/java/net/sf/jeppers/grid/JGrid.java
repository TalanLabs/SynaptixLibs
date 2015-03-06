/* 
 * Copyright (c) 2002, Cameron Zemek
 * 
 * This file is part of JGrid.
 * 
 * JGrid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * JGrid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jeppers.grid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

/**
 * The JGrid is used to display and edit regular two-dimensional grid of cells.
 * Unlike JTable which is fundamentally vertical in that the structure is
 * determined in the columns, while the rows contain the data. JGrid is
 * symmetrical with respect to the vertical and the horizontal orientations.
 * JGrid also allows for cells to be merged into bigger rectangular arrays,
 * called spans.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class JGrid extends JComponent implements Scrollable,
		CellEditorListener, GridModelListener {

	private static final long serialVersionUID = -9061123208700112637L;

	/* Default sizes */
	protected static final int DEFAULT_ROW_HEIGHT = 20;
	protected static final int DEFAULT_COLUMN_WIDTH = 75;

	protected RulerModel rowModel;
	protected RulerModel columnModel;
	protected GridModel gridModel;
	protected SelectionModel selectionModel;
	protected SpanModel spanModel;
	protected StyleModel styleModel;

	protected GridRepaintManager repaintMgr;

	// JavaBean fields
	private Color selectionBackgroundColor;
	private Color selectionForegroundColor;
	private Color focusBackgroundColor;
	private Color focusForegroundColor;
	private Color gridColor;
	private boolean showGrid = true;

	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID = "GridUI";

	// Install UI delegate
	static {
		UIManager.getDefaults().put(uiClassID,
				"net.sf.jeppers.grid.BasicGridUI");
	}

	/**
	 * Used by the <code>Scrollable</code> interface to determine the initial
	 * visible area.
	 */
	protected Dimension preferredViewportSize;

	//
	// Editing variables
	//

	/** Used to stop recusive call in processKeyBinding */
	private boolean reentrantCall;

	/** If editing, the <code>Component</code> that is handling the editing. */
	protected Component editorComp;

	/**
	 * The object that overwrites the screen real estate occupied by the current
	 * cell and allows the user to change its contents.
	 */
	protected GridCellEditor cellEditor;

	/** Identifies the column of the cell being edited. */
	protected int editingColumn = -1;

	/** Identifies the row of the cell being edited. */
	protected int editingRow = -1;

	public JGrid() {
		this(10, 10);
	}

	public JGrid(int rows, int columns) {
		gridModel = new DefaultGridModel(rows, columns);
		spanModel = new DefaultSpanModel();
		styleModel = new DefaultStyleModel();
		rowModel = new DefaultRulerModel(gridModel.getRowCount(),
				DEFAULT_ROW_HEIGHT, SwingConstants.VERTICAL);
		columnModel = new DefaultRulerModel(gridModel.getColumnCount(),
				DEFAULT_COLUMN_WIDTH, SwingConstants.HORIZONTAL);
		selectionModel = new DefaultSelectionModel();
		create(gridModel, spanModel, styleModel, rowModel, columnModel,
				selectionModel);
		updateUI();
	}

	public JGrid(GridModel gridModel, SpanModel spanModel, StyleModel styleModel) {
		rowModel = new DefaultRulerModel(gridModel.getRowCount(),
				DEFAULT_ROW_HEIGHT, SwingConstants.VERTICAL);
		columnModel = new DefaultRulerModel(gridModel.getColumnCount(),
				DEFAULT_COLUMN_WIDTH, SwingConstants.HORIZONTAL);
		selectionModel = new DefaultSelectionModel();
		create(gridModel, spanModel, styleModel, rowModel, columnModel,
				selectionModel);
		updateUI();
	}

	public JGrid(GridModel gridModel, SpanModel spanModel,
			StyleModel styleModel, RulerModel rowModel, RulerModel columnModel,
			SelectionModel selectionModel) {
		create(gridModel, spanModel, styleModel, rowModel, columnModel,
				selectionModel);
		updateUI();
	}

	public JGrid cloneView() {
		return new JGrid(gridModel, spanModel, styleModel, rowModel,
				columnModel, selectionModel);
	}

	protected void create(GridModel gridModel, SpanModel spanModel,
			StyleModel styleModel, RulerModel rowModel, RulerModel columnModel,
			SelectionModel selectionModel) {
		this.gridModel = gridModel;
		this.spanModel = spanModel;
		this.styleModel = styleModel;
		this.rowModel = rowModel;
		this.columnModel = columnModel;
		this.selectionModel = selectionModel;

		gridModel.addGridModelListener(this);
		repaintMgr = new GridRepaintManager(this);

		updateRepaintManager();

		// init GUI
		setOpaque(true);
	}

	protected void updateRepaintManager() {
		rowModel.addRulerModelListener(repaintMgr);
		columnModel.addRulerModelListener(repaintMgr);
		selectionModel.addSelectionModelListener(repaintMgr);
		spanModel.addSpanModelListener(repaintMgr);
		styleModel.addStyleModelListener(repaintMgr);
		gridModel.addGridModelListener(repaintMgr);
	}

	//
	// JavaBean properties
	//

	/**
	 * Returns the foreground color for cells with focus.
	 * 
	 * @return the <code>Color</code> used for the foreground of focused cells
	 */
	public Color getFocusForegroundColor() {
		return focusForegroundColor;
	}

	/**
	 * Sets the foreground color for cells with focus.
	 * 
	 * @param focusForegroundColor
	 *            the <code>Color</code> to use for foreground of focused cells
	 */
	public void setFocusForegroundColor(Color focusForegroundColor) {
		this.focusForegroundColor = focusForegroundColor;
		repaint();
	}

	/**
	 * Returns the background color for cells with focus.
	 * 
	 * @return the <code>Color</code> used for the background of focused cells
	 */
	public Color getFocusBackgroundColor() {
		return focusBackgroundColor;
	}

	/**
	 * Sets the background color for cells with focus.
	 * 
	 * @param focusBackgroundColor
	 *            the <code>Color</code> to use for background of focused cells
	 */
	public void setFocusBackgroundColor(Color focusBackgroundColor) {
		this.focusBackgroundColor = focusBackgroundColor;
		repaint();
	}

	/**
	 * Returns the foreground color for selected cells.
	 * 
	 * @return the <code>Color</code> used for the foreground of selected cells.
	 */
	public Color getSelectionForegroundColor() {
		return selectionForegroundColor;
	}

	/**
	 * Sets the foreground color for selected cells.
	 * 
	 * @param selectionForegroundColor
	 *            the <code>Color</code> used for the foreground of selected
	 *            cells
	 */
	public void setSelectionForegroundColor(Color selectionForegroundColor) {
		this.selectionForegroundColor = selectionForegroundColor;
		repaint();
	}

	/**
	 * Returns the background color for selected cells.
	 * 
	 * @return the <code>Color</code> used for the background of selected cells.
	 */
	public Color getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}

	/**
	 * Sets the background color for selected cells.
	 * 
	 * @param selectionBackgroundColor
	 *            the <code>Color</code> used for the background of selected
	 *            cells
	 */
	public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
		this.selectionBackgroundColor = selectionBackgroundColor;
		repaint();
	}

	/**
	 * Returns the color used to draw grid lines.
	 * 
	 * @return the <code>Color</code> used to draw grid lines
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Sets the color used to draw grid lines.
	 * 
	 * @param gridColor
	 *            the new <code>Color</code> of the grid lines
	 */
	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
		repaint();
	}

	/**
	 * Returns true if the grid draws grid lines.
	 * 
	 * @return true if the grid draws grid lines.
	 */
	public boolean getShowGrid() {
		return showGrid;
	}

	/**
	 * Sets wether grid lines should be drawn.
	 * 
	 * @param true if grid lines are to be drawn.
	 */
	public void setShowGrid(boolean show) {
		showGrid = show;
		repaint();
	}

	// end JavaBean properties

	/**
	 * Returns the number of rows in this grid's model.
	 * 
	 * @return the number of rows in this grid's model.
	 */
	public int getRowCount() {
		return gridModel.getRowCount();
	}

	/**
	 * Returns the number of columns in this grid's model.
	 * 
	 * @return the number of columns in this grid's model.
	 */
	public int getColumnCount() {
		return gridModel.getColumnCount();
	}

	/**
	 * Returns the height (in pixels) of <code>row</code>.
	 * 
	 * @param row
	 *            the row whose height is to be returned
	 * 
	 * @return the height (in pixels) of <code>row</code>
	 */
	public int getRowHeight(int row) {
		return rowModel.getSize(row);
	}

	/**
	 * Returns the width (in pixels) of <code>column</code>.
	 * 
	 * @param column
	 *            the column whose width is to be returned
	 * 
	 * @return the width (in pixels) of <code>column</code>
	 */
	public int getColumnWidth(int column) {
		return columnModel.getSize(column);
	}

	/**
	 * Sets the height for <code>row</code> to <code>height</code>.
	 * 
	 * @param row
	 *            the row whose height is to be changed
	 * @param height
	 *            new row height (in pixels)
	 */
	public void setRowHeight(int row, int height) {
		rowModel.setSize(row, height);
	}

	/**
	 * Sets the width for <code>column</code> to <code>width</code>.
	 * 
	 * @param column
	 *            the column whose width is to be changed
	 * @param width
	 *            new column width (in pixels)
	 */
	public void setColumnWidth(int column, int width) {
		columnModel.setSize(column, width);
	}

	/** Return the top vertical coordinate (in pixels) of row */
	public int getRowPosition(int row) {
		return rowModel.getPosition(row);
	}

	/** Return the left horizontal coordinate (in pixels) of column */
	public int getColumnPosition(int column) {
		return columnModel.getPosition(column);
	}

	/** Get cell bounds of (row, column) */
	public Rectangle getCellBounds(int row, int column) {
		int rowCount = 1;
		int columnCount = 1;

		if (isCellSpan(row, column)) {
			CellSpan span = spanModel.getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
			rowCount = span.getRowCount();
			columnCount = span.getColumnCount();
		}

		Rectangle cellBounds = new Rectangle();
		cellBounds.y = getRowPosition(row);
		cellBounds.x = getColumnPosition(column);

		// Height and width include spanned rows and columns
		for (int i = 0; i < rowCount; i++) {
			cellBounds.height += getRowHeight(row + i);
		}
		for (int j = 0; j < columnCount; j++) {
			cellBounds.width += getColumnWidth(column + j);
		}

		return cellBounds;
	}

	/** Return the row at the specified point */
	public int rowAtPoint(Point point) {
		return rowModel.getIndex(point.y);
	}

	/** Return the column at the specified point */
	public int columnAtPoint(Point point) {
		return columnModel.getIndex(point.x);
	}

	public boolean isCellSpan(int row, int column) {
		return spanModel.isCellSpan(row, column);
	}

	public GridCellRenderer getCellRenderer(int row, int column) {
		Class<?> type = gridModel.getClassAt(row, column);
		if (type == null) {
			Object o = gridModel.getValueAt(row, column);
			if (o != null) {
				type = o.getClass();
			}
		}
		return styleModel.getRenderer(type, row, column, this);
	}

	public GridCellEditor getCellEditor(int row, int column) {
		Class<?> type = gridModel.getClassAt(row, column);
		if (type == null) {
			Object o = gridModel.getValueAt(row, column);
			if (o != null) {
				type = o.getClass();
			}
		}
		return styleModel.getEditor(type, row, column, this);
	}

	/**
	 * Prepares the renderer for painting cell(row,column)
	 */
	public Component prepareRenderer(GridCellRenderer renderer, int row,
			int column) {
		Object value = gridModel.getValueAt(row, column);
		CellStyle style = styleModel.getCellStyle(row, column);
		boolean isSelected = isSelected(row, column);
		boolean hasFocus = (selectionModel.getAnchorRow() == row)
				&& (selectionModel.getAnchorColumn() == column)
				&& isFocusOwner();
		Component rendererComponent = renderer.getRendererComponent(row,
				column, value, style, isSelected, hasFocus, this);
		return rendererComponent;
	}

	/**
	 * Prepares the editor for cell(row, column)
	 */
	public Component prepareEditor(GridCellEditor editor, int row, int column) {
		Object value = gridModel.getValueAt(row, column);
		CellStyle style = styleModel.getCellStyle(row, column);
		boolean isSelected = isSelected(row, column);
		Component editorComponent = editor.getEditorComponent(row, column,
				value, style, isSelected, this);
		return editorComponent;
	}

	//
	// SelectionModel
	//

	public boolean isSelected(int row, int column) {
		return selectionModel.isSelected(row, column);
	}

	/**
	 * Retourne le mode de séléction
	 * 
	 * @return
	 */
	public SelectionModel.SelectionMode getSelectionMode() {
		return selectionModel.getSelectionMode();
	}

	/**
	 * Permet de changer le mode de selection
	 * 
	 * @param selectionMode
	 */
	public void setSelectionMode(SelectionModel.SelectionMode selectionMode) {
		selectionModel.setSelectionMode(selectionMode);
	}

	public void ensureCellInVisibleRect(int row, int column) {
		Rectangle cellRect = getCellBounds(row, column);
		scrollRectToVisible(cellRect);
	}

	public void changeSelection(int row, int column, boolean toggle,
			boolean extend) {
		if (isCellSpan(row, column)) {
			// translate cell coords
			CellSpan span = spanModel.getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
		}

		ensureCellInVisibleRect(row, column);

		SelectionModel.SelectionMode mode = selectionModel.getSelectionMode();

		if (!toggle && extend && mode != null
				&& mode == SelectionModel.SelectionMode.Multiple) {
			int topRow = selectionModel.getFirstSelectedRow();
			int leftColumn = selectionModel.getFirstSelectedColumn();
			int bottomRow = selectionModel.getLastSelectedRow();
			int rightColumn = selectionModel.getLastSelectedColumn();
			int anchorRow = selectionModel.getAnchorRow();
			int anchorColumn = selectionModel.getAnchorColumn();

			// Calculate new selectionRange
			if (row > anchorRow) {
				topRow = anchorRow;
				bottomRow = row;
			}
			if (row < anchorRow) {
				bottomRow = anchorRow;
				topRow = row;
			}
			if (row == anchorRow) {
				topRow = bottomRow = row;
			}
			if (column > anchorColumn) {
				leftColumn = anchorColumn;
				rightColumn = column;
			}
			if (column < anchorColumn) {
				rightColumn = anchorColumn;
				leftColumn = column;
			}
			if (column == anchorColumn) {
				leftColumn = rightColumn = column;
			}

			// Include spans into selection
			Iterator i = spanModel.getSpanIterator();
			while (i.hasNext()) {
				CellSpan span = (CellSpan) i.next();
				if (span.getLastRow() >= topRow
						&& span.getFirstRow() <= bottomRow
						&& span.getLastColumn() >= leftColumn
						&& span.getFirstColumn() <= rightColumn) {
					topRow = Math.min(topRow, span.getFirstRow());
					bottomRow = Math.max(bottomRow, span.getLastRow());
					leftColumn = Math.min(leftColumn, span.getFirstColumn());
					rightColumn = Math.max(rightColumn, span.getLastColumn());
				}
			}

			selectionModel.setSelectionRange(topRow, leftColumn, bottomRow,
					rightColumn);
			selectionModel.setLead(row, column);
		} else {
			selectionModel.setSelectionRange(row, column, row, column);
			selectionModel.setLead(row, column);
		}
	}

	//
	// Model management
	//
	public GridModel getGridModel() {
		return gridModel;
	}

	public void setGridModel(GridModel model) {
		gridModel.removeGridModelListener(this);
		gridModel.removeGridModelListener(repaintMgr);
		gridModel = model;
		gridModel.addGridModelListener(this);
		gridModel.addGridModelListener(repaintMgr);
		repaintMgr.resizeAndRepaint();
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SelectionModel model) {
		selectionModel.removeSelectionModelListener(repaintMgr);
		selectionModel = model;
		selectionModel.addSelectionModelListener(repaintMgr);
		repaintMgr.repaint();
	}

	public SpanModel getSpanModel() {
		return spanModel;
	}

	public void setSpanModel(SpanModel model) {
		spanModel.removeSpanModelListener(repaintMgr);
		spanModel = model;
		spanModel.addSpanModelListener(repaintMgr);
		repaintMgr.repaint();
	}

	public StyleModel getStyleModel() {
		return styleModel;
	}

	public void setStyleModel(StyleModel model) {
		styleModel.removeStyleModelListener(repaintMgr);
		styleModel = model;
		styleModel.addStyleModelListener(repaintMgr);
		repaintMgr.repaint();
	}

	public RulerModel getRowModel() {
		return rowModel;
	}

	public void setRowModel(RulerModel model) {
		rowModel.removeRulerModelListener(repaintMgr);
		rowModel = model;
		rowModel.addRulerModelListener(repaintMgr);
		repaintMgr.resizeAndRepaint();
	}

	public RulerModel getColumnModel() {
		return columnModel;
	}

	public void setColumnModel(RulerModel model) {
		columnModel.removeRulerModelListener(repaintMgr);
		columnModel = model;
		columnModel.addRulerModelListener(repaintMgr);
		repaintMgr.resizeAndRepaint();
	}

	/**
	 * Sync row and column sizes between models
	 */
	public void gridChanged(GridModelEvent event) {
		if (event.getType() == GridModelEvent.ROWS_INSERTED) {
			if (rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (columnModel instanceof ResizableGrid) {
				((ResizableGrid) columnModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (styleModel instanceof ResizableGrid) {
				((ResizableGrid) styleModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			repaintMgr.resizeAndRepaint();
		}
		if (event.getType() == GridModelEvent.ROWS_DELETED) {
			if (rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (columnModel instanceof ResizableGrid) {
				((ResizableGrid) columnModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (styleModel instanceof ResizableGrid) {
				((ResizableGrid) styleModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			repaintMgr.resizeAndRepaint();
		}
		if (event.getType() == GridModelEvent.COLUMNS_INSERTED) {
			if (rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).insertColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (columnModel instanceof ResizableGrid) {
				((ResizableGrid) columnModel).insertColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).insertColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (styleModel instanceof ResizableGrid) {
				((ResizableGrid) styleModel).insertColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			repaintMgr.resizeAndRepaint();
		}
		if (event.getType() == GridModelEvent.COLUMNS_DELETED) {
			if (rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).removeColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (columnModel instanceof ResizableGrid) {
				((ResizableGrid) columnModel).removeColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).removeColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (styleModel instanceof ResizableGrid) {
				((ResizableGrid) styleModel).removeColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			repaintMgr.resizeAndRepaint();
		}
	}

	//
	// Editing support
	//

	public boolean editCellAt(int row, int column) {
		return editCellAt(row, column, null);
	}

	/**
	 * Programmatically starts editing the cell at <code>row</code> and
	 * <code>column</code>, if the cell is editable.
	 * 
	 * @param row
	 *            the row to be edited
	 * @param column
	 *            the column to be edited
	 * @param e
	 *            event to pass into shouldSelectCell
	 * @exception IllegalArgumentException
	 *                If <code>row</code> or <code>column</code> is not in the
	 *                valid range
	 * @return false if for any reason the cell cannot be edited
	 */
	public boolean editCellAt(int row, int column, EventObject e) {
		if (cellEditor != null && !cellEditor.stopCellEditing()) {
			return false;
		}

		// Check out of bounds
		if (row < 0 || row >= getRowCount() || column < 0
				|| column >= getColumnCount()) {
			return false;
		}

		if (isCellSpan(row, column)) {
			// Translate cell coords to anchor of span
			CellSpan span = spanModel.getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
		}

		if (!gridModel.isCellEditable(row, column)) {
			return false;
		}

		GridCellEditor editor = getCellEditor(row, column);
		if (editor != null && editor.isCellEditable(e)) {
			editorComp = prepareEditor(editor, row, column);
			if (editorComp == null) {
				removeEditor();
				return false;
			}
			editorComp.setBounds(getCellBounds(row, column));
			add(editorComp);
			editorComp.validate();
			editorComp.requestFocus();

			cellEditor = editor;
			setEditingRow(row);
			setEditingColumn(column);
			editor.addCellEditorListener(this);
			return true;
		}
		return false;
	}

	/**
	 * Discards the editor object and frees the real estate it used for cell
	 * rendering.
	 */
	public void removeEditor() {
		if (cellEditor != null) {
			cellEditor.removeCellEditorListener(this);

			requestFocus();
			if (editorComp != null) {
				remove(editorComp);
			}

			Rectangle cellRect = getCellBounds(editingRow, editingColumn);

			cellEditor = null;
			setEditingColumn(-1);
			setEditingRow(-1);
			editorComp = null;

			repaint(cellRect);
		}
	}

	/**
	 * Sets the <code>editingColumn</code> variable.
	 * 
	 * @param aColumn
	 *            the column of the cell to be edited
	 * 
	 * @see #editingColumn
	 */
	public void setEditingColumn(int aColumn) {
		editingColumn = aColumn;
	}

	/**
	 * Sets the <code>editingRow</code> variable.
	 * 
	 * @param aRow
	 *            the row of the cell to be edited
	 * 
	 * @see #editingRow
	 */
	public void setEditingRow(int aRow) {
		editingRow = aRow;
	}

	/**
	 * Returns true if a cell is being edited.
	 * 
	 * @return true if the table is editing a cell
	 * @see #editingColumn
	 * @see #editingRow
	 */
	public boolean isEditing() {
		return (cellEditor == null) ? false : true;
	}

	/**
	 * Returns the component that is handling the editing session. If nothing is
	 * being edited, returns null.
	 * 
	 * @return Component handling editing session
	 */
	public Component getEditorComponent() {
		return editorComp;
	}

	/**
	 * Returns the index of the column that contains the cell currently being
	 * edited. If nothing is being edited, returns -1.
	 * 
	 * @return the index of the column that contains the cell currently being
	 *         edited; returns -1 if nothing being edited
	 * @see #editingRow
	 */
	public int getEditingColumn() {
		return editingColumn;
	}

	/**
	 * Returns the index of the row that contains the cell currently being
	 * edited. If nothing is being edited, returns -1.
	 * 
	 * @return the index of the row that contains the cell currently being
	 *         edited; returns -1 if nothing being edited
	 * @see #editingColumn
	 */
	public int getEditingRow() {
		return editingRow;
	}

	/**
	 * Return the current cell editor
	 */
	public GridCellEditor getCurrentCellEditor() {
		return cellEditor;
	}

	//
	// Implementing the CellEditorListener interface
	//

	/**
	 * Invoked when editing is finished. The changes are saved and the editor is
	 * discarded.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by JSpread.
	 * 
	 * @param e
	 *            the event received
	 * @see CellEditorListener
	 */
	public void editingStopped(ChangeEvent e) {
		// Take in the new value
		if (cellEditor != null) {
			Object value = cellEditor.getCellEditorValue();
			gridModel.setValueAt(value, editingRow, editingColumn);
			removeEditor();
		}
	}

	/**
	 * Invoked when editing is canceled. The editor object is discarded and the
	 * cell is rendered once again.
	 * 
	 * Application code will not use these methods explicitly, they are used
	 * internally by JSpread.
	 * 
	 * @param e
	 *            the event received
	 * @see CellEditorListener
	 */
	public void editingCanceled(ChangeEvent e) {
		removeEditor();
	}

	protected boolean processKeyBinding(KeyStroke keyStroke, KeyEvent keyEvent,
			int condition, boolean pressed) {
		if (reentrantCall) {
			return false;
		}
		reentrantCall = true;
		boolean retValue = super.processKeyBinding(keyStroke, keyEvent,
				condition, pressed);

		// Start editing when a key is typed
		if (!retValue && condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
				&& hasFocus()) {
			// We do not have a binding for the event.
			Component component = getEditorComponent();
			if (component == null) {
				// Only attempt to install the editor on a KEY_PRESSED,
				if (keyEvent == null
						|| keyEvent.getID() != KeyEvent.KEY_PRESSED) {
					reentrantCall = false;
					return false;
				}
				// Don't start when just a modifier is pressed
				int code = keyEvent.getKeyCode();
				if (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL
						|| code == KeyEvent.VK_ALT) {
					reentrantCall = false;
					return false;
				}
				// Try to install the editor
				int anchorRow = getSelectionModel().getAnchorRow();
				int anchorColumn = getSelectionModel().getAnchorColumn();
				if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) {
					if (!editCellAt(anchorRow, anchorColumn)) {
						reentrantCall = false;
						return false;
					}
				}
				component = getEditorComponent();
				if (component == null) {
					reentrantCall = false;
					return false;
				}
			}
			// If the editorComponent is a JComponent, pass the event to it.
			if (component instanceof JComponent) {
				((JComponent) component).dispatchEvent(keyEvent);
				retValue = keyEvent.isConsumed();
			}
		}
		reentrantCall = false;
		return retValue;
	}

	//
	// Scrollable Interface
	//

	/**
	 * Sets the preferred size of the viewport for this table.
	 * 
	 * @param size
	 *            a <code>Dimension</code> object specifying the
	 *            <code>preferredSize</code> of a <code>JViewport</code> whose
	 *            view is this spreadsheet
	 * @see Scrollable#getPreferredScrollableViewportSize
	 */
	public void setPreferredScrollableViewportSize(Dimension size) {
		preferredViewportSize = size;
	}

	/**
	 * Returns the preferred size of the viewport for this table.
	 * 
	 * @return a <code>Dimension</code> object containing the
	 *         <code>preferredSize</code> of the <code>JViewport</code> which
	 *         displays this table
	 * @see Scrollable#getPreferredScrollableViewportSize
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return preferredViewportSize;
	}

	/**
	 * Returns the scroll increment (in pixels) that completely exposes one new
	 * row or column (depending on the orientation).
	 * 
	 * This method is called each time the user requests a unit scroll.
	 * 
	 * @param visibleRect
	 *            the view area visible within the viewport
	 * @param orientation
	 *            either <code>SwingConstants.VERTICAL</code> or
	 *            <code>SwingConstants.HORIZONTAL</code>
	 * @param direction
	 *            less than zero to scroll up/left, greater than zero for
	 *            down/right
	 * @return the "unit" increment for scrolling in the specified direction
	 * @see Scrollable#getScrollableUnitIncrement
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return DEFAULT_COLUMN_WIDTH;
		} else {
			return DEFAULT_ROW_HEIGHT;
		}
	}

	/**
	 * Returns <code>visibleRect.height</code> or <code>visibleRect.width</code>
	 * , depending on this spreadsheet's orientation.
	 * 
	 * @return <code>visibleRect.height</code> or <code>visibleRect.width</code>
	 *         per the orientation
	 * @see Scrollable#getScrollableBlockIncrement
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	/**
	 * Returns false to indicate that the width of the viewport does not
	 * determine the width of the spreadsheet.
	 * 
	 * @return false
	 * @see Scrollable#getScrollableTracksViewportWidth
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * Returns false to indicate that the height of the viewport does not
	 * determine the height of the spreadsheet.
	 * 
	 * @return false
	 * @see Scrollable#getScrollableTracksViewportHeight
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	//
	// UI management
	//

	/**
	 * Returns the L&F object that renders this component.
	 * 
	 * @return SpreadsheetUI object
	 */
	public GridUI getUI() {
		return (GridUI) ui;
	}

	/**
	 * Sets the L&F object that renders this component.
	 * 
	 * @param ui
	 *            the SpreadsheetUI L&F object
	 * @see UIDefaults#getUI
	 */
	public void setUI(GridUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	/**
	 * Notification from the UIFactory that the L&F has changed.
	 * 
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		setUI((GridUI) UIManager.getUI(this));
		styleModel.updateUI();
		resizeAndRepaint();
	}

	/**
	 * Returns a string that specifies the name of the l&f class that renders
	 * this component.
	 * 
	 * @return String "SpreadsheetUI"
	 * 
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	public String getUIClassID() {
		return uiClassID;
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	//
	// Convenience methods
	//

	public Object getValueAt(int row, int column) {
		return gridModel.getValueAt(row, column);
	}

	public void setValueAt(Object value, int row, int column) {
		gridModel.setValueAt(value, row, column);
	}
}
