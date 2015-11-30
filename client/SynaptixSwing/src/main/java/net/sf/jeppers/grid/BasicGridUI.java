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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.UIResource;

/**
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class BasicGridUI extends GridUI {
	private static boolean installed = false;

	protected JGrid grid;
	protected CellRendererPane rendererPane;

	// Listeners that are attached to the JGrid
	protected FocusListener focusListener;
	protected MouseInputListener mouseInputListener;

	public void paint(Graphics g, JComponent c) {
		if (grid.getRowCount() <= 0 || grid.getColumnCount() <= 0) {
			return; // nothing to paint
		}

		Rectangle clip = g.getClipBounds();
		Point minLocation = clip.getLocation();
		Point maxLocation = new Point(clip.x + clip.width - 1, clip.y
				+ clip.height - 1);
		int rowMin = grid.rowAtPoint(minLocation);
		int rowMax = grid.rowAtPoint(maxLocation);
		// This should never happen.
		if (rowMin == -1) {
			rowMin = 0;
		}
		// If the spread does not have enough rows to fill the view we'll get
		// -1.
		// Replace this with the index of the last row.
		if (rowMax == -1) {
			rowMax = grid.getRowCount() - 1;
		}
		int colMin = grid.columnAtPoint(minLocation);
		int colMax = grid.columnAtPoint(maxLocation);
		// This should never happen.
		if (colMin == -1) {
			colMin = 0;
		}
		// If the spread does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (colMax == -1) {
			colMax = grid.getColumnCount() - 1;
		}

		// Paint cells
		paintCells(g, rowMin, rowMax, colMin, colMax);

		// Paint grid
		paintGrid(g, rowMin, rowMax, colMin, colMax);

		// Paint spans
		paintSpans(g, rowMin, rowMax, colMin, colMax);

		// Paint borders
		paintBorders(g, rowMin, rowMax, colMin, colMax);

		// Paint editor
		paintEditor(g);
	}

	protected void paintEditor(Graphics g) {
		Component component = grid.getEditorComponent();
		if (component == null) {
			return;
		}
		Rectangle cellBounds = grid.getCellBounds(grid.getEditingRow(), grid
				.getEditingColumn());
		component.setBounds(cellBounds);
		component.validate();
		component.requestFocus();
	}

	/**
	 * special paint handler for merged cell regions
	 */
	protected void paintSpans(Graphics g, int rowMin, int rowMax, int colMin,
			int colMax) {
		Rectangle clip = g.getClipBounds();
		Iterator cell = grid.getSpanModel().getSpanIterator();
		while (cell.hasNext()) {
			CellSpan span = (CellSpan) cell.next();
			Rectangle cellBounds = grid.getCellBounds(span.getRow(), span
					.getColumn());

			// Only paint cell if visible
			if (span.getLastRow() >= rowMin && span.getLastColumn() >= colMin
					&& span.getFirstRow() <= rowMax
					&& span.getFirstColumn() <= colMax) {
				paintCell(g, cellBounds, span.getRow(), span.getColumn());
				// Paint grid line around cell
				if (grid.getShowGrid()) {
					g.setColor(grid.getGridColor());
					g.drawRect(cellBounds.x, cellBounds.y, cellBounds.width,
							cellBounds.height);
				}
			}
		}
	}

	protected void paintGrid(Graphics g, int rowMin, int rowMax, int colMin,
			int colMax) {
		if (!grid.getShowGrid()) {
			return; // do nothing
		}

		int y1 = grid.getRowPosition(rowMin);
		int y2 = grid.getRowPosition(rowMax) + grid.getRowHeight(rowMax);
		int x1 = grid.getColumnPosition(colMin);
		int x2 = grid.getColumnPosition(colMax) + grid.getColumnWidth(colMax);

		g.setColor(grid.getGridColor());

		// Draw the horizontal lines
		for (int row = rowMin; row <= rowMax; row++) {
			int rowY = grid.getRowPosition(row);
			g.drawLine(x1, rowY, x2, rowY);
		}
		g.drawLine(x1, y2, x2, y2);

		// Draw the vertical gridlines
		for (int col = colMin; col <= colMax; col++) {
			int colX = grid.getColumnPosition(col);
			g.drawLine(colX, y1, colX, y2);
		}
		g.drawLine(x2, y1, x2, y2);
	}

	protected void paintCells(Graphics g, int rowMin, int rowMax, int colMin,
			int colMax) {
		rowMin = Math.max(0, rowMin - 1);
		rowMax = Math.min(grid.getRowCount() - 1, rowMax + 1);
		colMin = Math.max(0, colMin - 1);
		colMax = Math.min(grid.getColumnCount() - 1, colMax + 1);

		for (int row = rowMin; row <= rowMax; row++) {
			for (int column = colMin; column <= colMax; column++) {
				/* Paint cell if it is atomic */
				if (!grid.isCellSpan(row, column)) {
					Rectangle cellBounds = grid.getCellBounds(row, column);
					paintCell(g, cellBounds, row, column);
				}
			}
		}
	}

	/** Paint cell at (row, column) */
	protected void paintCell(Graphics g, Rectangle cellBounds, int row,
			int column) {
		if (grid.getEditingRow() == row && grid.getEditingColumn() == column) {
			return;
		}
		GridCellRenderer renderer = grid.getCellRenderer(row, column);
		Component rendererComp = grid.prepareRenderer(renderer, row, column);
		CellSpan span = grid.getSpanModel().getSpanOver(row, column);
		rendererPane.paintComponent(g, rendererComp, grid, cellBounds.x,
				cellBounds.y, cellBounds.width, cellBounds.height, true);
	}

	/*
	 * Borders are handled by the UI since they are shared between cells and
	 * therefore must overlay after the painting of the cells
	 */
	private void paintBorders(Graphics g, int rowMin, int rowMax, int colMin,
			int colMax) {
		// Include borders from adjacent rows/columns of the clip region
		rowMin = Math.max(0, rowMin - 1);
		rowMax = Math.min(grid.getRowCount() - 1, rowMax + 1);
		colMin = Math.max(0, colMin - 1);
		colMax = Math.min(grid.getColumnCount() - 1, colMax + 1);
		for (int row = rowMin; row <= rowMax; row++) {
			for (int column = colMin; column <= colMax; column++) {
				Rectangle cellRect = grid.getCellBounds(row, column);
				paintBorder(g, cellRect, row, column);
			}
		}
	}

	private void paintBorder(Graphics g, Rectangle cellRect, int row, int column) {
		if (grid.getSpanModel().isCellSpan(row, column)) {
			CellSpan span = grid.getSpanModel().getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
		}
		// Paint border
		javax.swing.border.Border border = grid.getStyleModel().getCellStyle(
				row, column).getBorder();
		Insets borderInsets = border.getBorderInsets(grid);
		Rectangle borderRect = new Rectangle(cellRect);
		int topOffset = (borderInsets.top >> 1);
		int leftOffset = (borderInsets.left >> 1);
		int bottomOffset = (borderInsets.bottom / 3) + topOffset;
		int rightOffset = (borderInsets.right / 3) + leftOffset;
		borderRect.x -= leftOffset;
		borderRect.y -= topOffset;
		borderRect.width += rightOffset;
		borderRect.height += bottomOffset;
		border.paintBorder(grid, g, borderRect.x, borderRect.y,
				borderRect.width, borderRect.height);
	}

	public Dimension getPreferredSize(JComponent c) {
		return getMaximumSize(c);
	}

	public Dimension getMinimumSize(JComponent c) {
		return getMaximumSize(c);
	}

	public Dimension getMaximumSize(JComponent c) {
		int lastRow = grid.getRowCount() - 1;
		int lastCol = grid.getColumnCount() - 1;

		Rectangle cellBounds = grid.getCellBounds(lastRow, lastCol);
		int width = cellBounds.x + cellBounds.width;
		int height = cellBounds.y + cellBounds.height;

		return new Dimension(width, height);
	}

	public void installUI(JComponent c) {
		grid = (JGrid) c;

		rendererPane = new CellRendererPane();
		grid.add(rendererPane);

		installDefaults();
		installListeners();
		installKeyboardActions();
	}

	protected void installDefaults() {
		Color defaultGridColor = UIManager.getColor("Table.gridColor");
		Color defaultForegroundColor = UIManager.getColor("Table.foreground");
		Color defaultBackgroundColor = UIManager.getColor("Table.background");
		Border defaultBorder = UIManager.getBorder("Table.scrollPaneBorder");
		Color defaultSelectionForeground = UIManager
				.getColor("Table.selectionForeground");
		Color defaultSelectionBackground = UIManager
				.getColor("Table.selectionBackground");
		Color defaultFocusCellForeground = UIManager
				.getColor("Table.focusCellForeground");
		Color defaultFocusCellBackground = new Color(153, 153, 204);
		Font defaultFont = UIManager.getFont("Table.font");
		Border defaultGridBorder = UIManager.getBorder("Table.border");
		InputMap inputMap = (InputMap) UIManager.get("Table.ancestorInputMap");
		if (!installed) {
			UIManager.getDefaults().put("Grid.gridColor", defaultGridColor);
			UIManager.getDefaults().put("Grid.foreground",
					defaultForegroundColor);
			UIManager.getDefaults().put("Grid.background",
					defaultBackgroundColor);
			UIManager.getDefaults().put("Grid.selectionForegroundColor",
					defaultSelectionForeground);
			UIManager.getDefaults().put("Grid.selectionBackgroundColor",
					defaultSelectionBackground);
			UIManager.getDefaults().put("Grid.focusForegroundColor",
					defaultFocusCellForeground);
			UIManager.getDefaults().put("Grid.focusBackgroundColor",
					defaultFocusCellBackground);
			UIManager.getDefaults().put("Grid.border", defaultGridBorder);
			UIManager.getDefaults().put("Grid.font", defaultFont);
			UIManager.getDefaults().put("Grid.scrollPaneBorder", defaultBorder);
			UIManager.getDefaults().put("Grid.ancestorInputMap", inputMap);
			installed = true;
		}
		Color foregroundColor = grid.getForeground();
		Color backgroundColor = grid.getBackground();
		Font font = grid.getFont();
		Border border = grid.getBorder();
		Color gridColor = grid.getGridColor();
		Color selectionForeground = grid.getSelectionForegroundColor();
		Color selectionBackground = grid.getSelectionBackgroundColor();
		Color focusForeground = grid.getFocusForegroundColor();
		Color focusBackground = grid.getFocusBackgroundColor();
		if (foregroundColor == null || foregroundColor instanceof UIResource)
			grid.setForeground(defaultForegroundColor);
		if (backgroundColor == null || backgroundColor instanceof UIResource)
			grid.setBackground(defaultBackgroundColor);
		if (font == null || font instanceof UIResource)
			grid.setFont(defaultFont);
		if (gridColor == null || gridColor instanceof UIResource)
			grid.setGridColor(defaultGridColor);
		if (border == null || border instanceof UIResource)
			grid.setBorder(defaultGridBorder);
		if (selectionForeground == null
				|| selectionForeground instanceof UIResource)
			grid.setSelectionForegroundColor(defaultSelectionForeground);
		if (selectionBackground == null
				|| selectionBackground instanceof UIResource)
			grid.setSelectionBackgroundColor(defaultSelectionBackground);
		if (focusForeground == null || focusForeground instanceof UIResource)
			grid.setFocusForegroundColor(defaultFocusCellForeground);
		if (focusBackground == null || focusBackground instanceof UIResource)
			grid.setFocusBackgroundColor(defaultFocusCellBackground);
	}

	/**
	 * Attaches listeners to the JGrid
	 */
	protected void installListeners() {
		mouseInputListener = createMouseInputListener();
		grid.addMouseListener(mouseInputListener);
		grid.addMouseMotionListener(mouseInputListener);
	}

	protected void installKeyboardActions() {
		ActionMap map = getActionMap();
		SwingUtilities.replaceUIActionMap(grid, map);
		InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		SwingUtilities.replaceUIInputMap(grid,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	}

	private InputMap getInputMap(int condition) {
		if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
			return (InputMap) UIManager.get("Grid.ancestorInputMap");
		} else {
			return null;
		}
	}

	private ActionMap getActionMap() {
		ActionMap actionMap = (ActionMap) UIManager.get("Grid.actionMap");
		if (actionMap == null) {
			actionMap = createActionMap();
			if (actionMap != null) {
				UIManager.put("Grid.actionMap", actionMap);
			}
		}
		return actionMap;
	}

	private ActionMap createActionMap() {
		ActionMap map = new ActionMapUIResource();

		map.put("selectNextColumn", new NavigationalAction(1, 0, false, false,
				false));
		map.put("selectPreviousColumn", new NavigationalAction(-1, 0, false,
				false, false));
		map.put("selectNextRow", new NavigationalAction(0, 1, false, false,
				false));
		map.put("selectPreviousRow", new NavigationalAction(0, -1, false,
				false, false));

		map.put("selectNextColumnExtendSelection", new NavigationalAction(1, 0,
				false, true, false));
		map.put("selectPreviousColumnExtendSelection", new NavigationalAction(
				-1, 0, false, true, false));
		map.put("selectNextRowExtendSelection", new NavigationalAction(0, 1,
				false, true, false));
		map.put("selectPreviousRowExtendSelection", new NavigationalAction(0,
				-1, false, true, false));

		map.put("scrollUpChangeSelection", new PagingAction(false, false, true,
				false));
		map.put("scrollDownChangeSelection", new PagingAction(false, true,
				true, false));
		map.put("selectFirstColumn",
				new PagingAction(false, false, false, true));
		map
				.put("selectLastColumn", new PagingAction(false, true, false,
						false));

		map.put("scrollUpExtendSelection", new PagingAction(true, false, true,
				false));
		map.put("scrollDownExtendSelection", new PagingAction(true, true, true,
				false));
		map.put("selectFirstColumnExtendSelection", new PagingAction(true,
				false, false, true));
		map.put("selectLastColumnExtendSelection", new PagingAction(true, true,
				false, false));

		map.put("selectFirstRow", new PagingAction(false, false, true, true));
		map.put("selectLastRow", new PagingAction(false, true, true, true));

		map.put("selectFirstRowExtendSelection", new PagingAction(true, false,
				true, true));
		map.put("selectLastRowExtendSelection", new PagingAction(true, true,
				true, true));

		map.put("selectNextColumnCell", new NavigationalAction(1, 0, true,
				false, true));
		map.put("selectPreviousColumnCell", new NavigationalAction(-1, 0, true,
				false, true));
		map.put("selectNextRowCell", new NavigationalAction(0, 1, true, false,
				true));
		map.put("selectPreviousRowCell", new NavigationalAction(0, -1, true,
				false, true));

		map.put("selectAll", new SelectAllAction());
		map.put("cancel", new CancelEditingAction());
		map.put("startEditing", new StartEditingAction());

		map.put("scrollLeftChangeSelection", new PagingAction(false, false,
				false, false));
		map.put("scrollRightChangeSelection", new PagingAction(false, true,
				false, false));
		map.put("scrollLeftExtendSelection", new PagingAction(true, false,
				false, false));
		map.put("scrollRightExtendSelection", new PagingAction(true, true,
				false, false));

		return map;
	}

	public void uninstallUI(JComponent c) {
		grid.remove(rendererPane);
	}

	public static javax.swing.plaf.ComponentUI createUI(JComponent c) {
		return new BasicGridUI();
	}

	//
	// The Spreadsheet's mouse and mouse motion listeners
	//

	/**
	 * This inner class is marked &quot;public&quot; due to a compiler bug. This
	 * class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of BasicSpreadsheetUI.
	 */
	public class MouseInputHandler implements MouseInputListener {
		// Component recieving mouse events during editing. May not be
		// editorComponent.
		private Component dispatchComponent;
		private boolean selectedOnPress;

		private void setDispatchComponent(MouseEvent e) {
			// Get location
			Point spreadPoint = e.getPoint();
			int row = grid.rowAtPoint(spreadPoint);
			int column = grid.columnAtPoint(spreadPoint);

			// Get editor component
			Component editorComponent = grid.getEditorComponent();

			// Get dispatchComponent
			Point editorPoint = SwingUtilities.convertPoint(grid, spreadPoint,
					editorComponent);
			dispatchComponent = SwingUtilities.getDeepestComponentAt(
					editorComponent, editorPoint.x, editorPoint.y);
		}

		/* Repost event to dispatchComponent */
		private boolean repostEvent(MouseEvent e) {
			if (dispatchComponent == null) {
				return false;
			}
			MouseEvent editorMouseEvent = SwingUtilities.convertMouseEvent(
					grid, e, dispatchComponent);
			dispatchComponent.dispatchEvent(editorMouseEvent);
			return true;
		}

		private boolean shouldIgnore(MouseEvent e) {
			return e.isConsumed()
					|| (!(SwingUtilities.isLeftMouseButton(e) && grid
							.isEnabled()));
		}

		private void setValueIsAdjusting(boolean flag) {
			grid.getSelectionModel().setValueIsAdjusting(flag);
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (e.isConsumed()) {
				selectedOnPress = false;
				return;
			}
			selectedOnPress = true;
			adjustFocusAndSelection(e);
		}

		private void adjustFocusAndSelection(MouseEvent e) {
			if (shouldIgnore(e)) {
				return;
			}

			Point p = e.getPoint();
			int row = grid.rowAtPoint(p);
			int column = grid.columnAtPoint(p);

			// The autoscroller can generate drag events outside range.
			if ((column == -1) || (row == -1)) {
				System.err.println("Out of bounds");
				return;
			}

			if (grid.editCellAt(row, column, e)) {
				setDispatchComponent(e);
				repostEvent(e);
			} else {
				grid.requestFocus();
			}

			GridCellEditor editor = grid.getCurrentCellEditor();
			if (editor == null || editor.shouldSelectCell(e)) {
				// Update selection model
				setValueIsAdjusting(true);
				grid.changeSelection(row, column, e.isControlDown(), e
						.isShiftDown());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (selectedOnPress) {
				if (shouldIgnore(e)) {
					return;
				}

				repostEvent(e);
				dispatchComponent = null;
				setValueIsAdjusting(false);
			} else {
				adjustFocusAndSelection(e);
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			if (shouldIgnore(e)) {
				return;
			}

			repostEvent(e);

			CellEditor editor = grid.getCurrentCellEditor();
			if (editor == null || editor.shouldSelectCell(e)) {
				Point p = e.getPoint();
				int row = grid.rowAtPoint(p);
				int column = grid.columnAtPoint(p);
				// The autoscroller can generate drag events outside the Table's
				// range.
				if ((column == -1) || (row == -1)) {
					return;
				}
				grid.changeSelection(row, column, false, true);
			}
		}
	}

	private static class NavigationalAction extends AbstractAction {

		private static final long serialVersionUID = -8068533198764648622L;

		protected int dx;
		protected int dy;
		protected boolean toggle;
		protected boolean extend;
		protected boolean inSelection;

		protected int anchorRow;
		protected int anchorColumn;
		protected int leadRow;
		protected int leadColumn;

		protected NavigationalAction(int dx, int dy, boolean toggle,
				boolean extend, boolean inSelection) {
			this.dx = dx;
			this.dy = dy;
			this.toggle = toggle;
			this.extend = extend;
			this.inSelection = inSelection;
		}

		private int clipToRange(int i, int a, int b) {
			return Math.min(Math.max(i, a), b - 1);
		}

		private void moveWithinGridRange(JGrid grid, int dx, int dy,
				boolean changeLead) {
			if (changeLead) {
				leadRow = clipToRange(leadRow + dy, 0, grid.getRowCount());
				leadColumn = clipToRange(leadColumn + dx, 0, grid
						.getColumnCount());
			} else {
				anchorRow = clipToRange(anchorRow + dy, 0, grid.getRowCount());
				anchorColumn = clipToRange(anchorColumn + dx, 0, grid
						.getColumnCount());
			}
		}

		private int selectionSpan(SelectionModel sm, int orientation) {
			if (orientation == SwingConstants.VERTICAL) {
				return sm.getLastSelectedRow() - sm.getFirstSelectedRow() + 1;
			} else {
				return sm.getLastSelectedColumn() - sm.getFirstSelectedColumn()
						+ 1;
			}
		}

		private int compare(int i, SelectionModel sm, int orientation) {
			int min = 0;
			int max = 0;
			if (orientation == SwingConstants.VERTICAL) {
				min = sm.getFirstSelectedRow();
				max = sm.getLastSelectedRow();
			} else {
				min = sm.getFirstSelectedColumn();
				max = sm.getLastSelectedColumn();
			}
			return compare(i, min, max + 1);
		}

		private int compare(int i, int a, int b) {
			return (i < a) ? -1 : (i >= b) ? 1 : 0;
		}

		private boolean moveWithinSelectedRange(JGrid grid, int dx, int dy,
				boolean ignoreCarry) {
			SelectionModel sm = grid.getSelectionModel();

			int newAnchorRow = anchorRow + dy;
			int newAnchorColumn = anchorColumn + dx;

			int rowSgn;
			int colSgn;
			int rowCount = selectionSpan(sm, SwingConstants.VERTICAL);
			int columnCount = selectionSpan(sm, SwingConstants.HORIZONTAL);

			boolean canStayInSelection = (rowCount * columnCount > 1);
			if (canStayInSelection) {
				rowSgn = compare(newAnchorRow, sm, SwingConstants.VERTICAL);
				colSgn = compare(newAnchorColumn, sm, SwingConstants.HORIZONTAL);
			} else {
				// If there is only one selected cell, there is no point
				// in trying to stay within the selected area. Move outside
				// the selection, wrapping at the table boundaries.
				rowCount = grid.getRowCount();
				columnCount = grid.getColumnCount();
				rowSgn = compare(newAnchorRow, 0, rowCount);
				colSgn = compare(newAnchorColumn, 0, columnCount);

			}

			anchorRow = newAnchorRow - rowCount * rowSgn;
			anchorColumn = newAnchorColumn - columnCount * colSgn;

			if (!ignoreCarry) {
				return moveWithinSelectedRange(grid, rowSgn, colSgn, true);
			}
			return canStayInSelection;
		}

		public void actionPerformed(ActionEvent e) {
			JGrid grid = (JGrid) e.getSource();
			SelectionModel sm = grid.getSelectionModel();
			anchorRow = sm.getAnchorRow();
			leadRow = sm.getLeadRow();
			anchorColumn = sm.getAnchorColumn();
			leadColumn = sm.getLeadColumn();

			int oldAnchorRow = anchorRow;
			int oldAnchorColumn = anchorColumn;

			if (grid.isEditing()
					&& !grid.getCurrentCellEditor().stopCellEditing()) {
				return;
			}

			if (!inSelection) {
				moveWithinGridRange(grid, dx, dy, extend);
				if (!extend) {
					grid
							.changeSelection(anchorRow, anchorColumn, false,
									extend);
				} else {
					grid.changeSelection(leadRow, leadColumn, false, extend);
				}
			} else {
				if (moveWithinSelectedRange(grid, dx, dy, false)) {
					grid.changeSelection(anchorRow, anchorColumn, true, true);
				} else {
					grid.changeSelection(anchorRow, anchorColumn, false, false);
				}
			}
		}
	}

	private static class PagingAction extends NavigationalAction {

		private boolean forwards;
		private boolean vertically;
		private boolean toLimit;

		private PagingAction(boolean extend, boolean forwards,
				boolean vertically, boolean toLimit) {
			super(0, 0, false, extend, false);
			this.forwards = forwards;
			this.vertically = vertically;
			this.toLimit = toLimit;
		}

		public void actionPerformed(ActionEvent e) {
			JGrid grid = (JGrid) e.getSource();
			if (toLimit) {
				if (vertically) {
					int rowCount = grid.getRowCount();
					this.dx = 0;
					this.dy = forwards ? rowCount : -rowCount;
				} else {
					int colCount = grid.getColumnCount();
					this.dx = forwards ? colCount : -colCount;
					this.dy = 0;
				}
			} else {
				if (!(grid.getParent().getParent() instanceof JScrollPane)) {
					return;
				}

				Dimension delta = grid.getParent().getSize();
				SelectionModel sm = grid.getSelectionModel();

				int start = 0;
				if (vertically) {
					start = (extend) ? sm.getLeadRow() : sm.getAnchorRow();
				} else {
					start = (extend) ? sm.getLeadColumn() : sm
							.getAnchorColumn();
				}

				if (vertically) {
					Rectangle r = grid.getCellBounds(start, 0);
					r.y += forwards ? delta.height : -delta.height;
					this.dx = 0;
					int newRow = grid.rowAtPoint(r.getLocation());
					if (newRow == -1 && forwards) {
						newRow = grid.getRowCount();
					}
					this.dy = newRow - start;
				} else {
					Rectangle r = grid.getCellBounds(0, start);
					r.x += forwards ? delta.width : -delta.width;
					int newColumn = grid.columnAtPoint(r.getLocation());
					if (newColumn == -1 && forwards) {
						newColumn = grid.getColumnCount();
					}
					this.dx = newColumn - start;
					this.dy = 0;
				}
			}
			super.actionPerformed(e);
		}
	}

	/**
	 * Action to invoke <code>selectAll</code> on the table.
	 */
	private static class SelectAllAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

		}
	}

	/**
	 * Action to invoke <code>removeEditor</code> on the table.
	 */
	private static class CancelEditingAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

		}
	}

	/**
	 * Action to start editing, and pass focus to the editor.
	 */
	private static class StartEditingAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			JGrid grid = (JGrid) e.getSource();
			if (!grid.hasFocus()) {
				CellEditor cellEditor = grid.getCurrentCellEditor();
				if (cellEditor != null && !cellEditor.stopCellEditing()) {
					return;
				}
				grid.requestFocus();
			}
			SelectionModel selectionModel = grid.getSelectionModel();
			int anchorRow = selectionModel.getAnchorRow();
			int anchorColumn = selectionModel.getAnchorColumn();
			grid.editCellAt(anchorRow, anchorColumn, null);
			Component editorComp = grid.getEditorComponent();
			if (editorComp != null) {
				editorComp.requestFocus();
			}
		}
	}

	//
	// Factory methods for the Listeners
	//

	/**
	 * Creates the mouse listener for the JTable.
	 */
	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}
}
