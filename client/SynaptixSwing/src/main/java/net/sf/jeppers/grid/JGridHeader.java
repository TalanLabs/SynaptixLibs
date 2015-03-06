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

import java.awt.Component;

import javax.swing.SwingConstants;

/**
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class JGridHeader extends JGrid {

	private static final long serialVersionUID = -1031972919067101001L;

	private static final String uiClassID = "GridHeaderUI";

	private int orientation;

	private JGrid viewport;

	public JGridHeader(JGrid viewport, int orientation) {
		this(viewport, orientation, 1, 1);
	}

	public JGridHeader(JGrid viewport, int orientation, int rows, int columns) {
		super(rows, columns);

		this.viewport = viewport;
		this.orientation = orientation;

		spanModel = new DefaultSpanModel();
		styleModel = new DefaultStyleModel();
		((DefaultStyleModel) styleModel).setDefaultRenderer(String.class,
				new GridHeaderRenderer());
		selectionModel = new DefaultSelectionModel();

		viewport.getGridModel().addGridModelListener(new MyGridModelListener());

		if (orientation == SwingConstants.HORIZONTAL) {
			columnModel = viewport.getColumnModel();
			gridModel = new ColumnHeaderGridModel(columnModel);
			rowModel = new DefaultRulerModel(rows, DEFAULT_ROW_HEIGHT,
					SwingConstants.VERTICAL);
		} else {
			rowModel = viewport.getRowModel();
			gridModel = new RowHeaderGridModel(rowModel);
			columnModel = new DefaultRulerModel(columns, DEFAULT_COLUMN_WIDTH,
					SwingConstants.HORIZONTAL);
		}
		create(gridModel, spanModel, styleModel, rowModel, columnModel,
				selectionModel);
		updateUI();
	}

	private final class MyGridModelListener implements GridModelListener {

		public void gridChanged(GridModelEvent event) {
			if (gridModel instanceof ResizableGrid) {
				ResizableGrid grid = (ResizableGrid) gridModel;
				int type = event.getType();
				if (orientation == SwingConstants.HORIZONTAL) {
					if (type == GridModelEvent.COLUMNS_INSERTED) {
						grid.insertColumns(event.getFirstColumn(), event
								.getColumnCount());
					} else if (type == GridModelEvent.COLUMNS_DELETED) {
						grid.removeColumns(event.getFirstColumn(), event
								.getColumnCount());
					}
				} else {
					if (type == GridModelEvent.ROWS_INSERTED) {
						grid.insertRows(event.getFirstRow(), event
								.getRowCount());
					} else if (type == GridModelEvent.ROWS_DELETED) {
						grid.removeRows(event.getFirstRow(), event
								.getRowCount());
					}
				}
			} else {
				repaintMgr.resizeAndRepaint();
			}
		}
	}

	public JGrid getViewport() {
		return viewport;
	}

	public int getOrientation() {
		return orientation;
	}

	public GridUI getUI() {
		return (GridUI) ui;
	}

	public void setUI(GridUI gridUI) {
		if (ui != gridUI) {
			super.setUI(gridUI);
			repaint();
		}
	}

	public void updateUI() {
		StyleModel styleModel = getStyleModel();
		if (styleModel != null)
			styleModel.updateUI();
		setUI(new BasicGridHeaderUI());
		repaintMgr.resizeAndRepaint();
	}

	public String getUIClassID() {
		return "GridHeaderUI";
	}

	public void gridChanged(GridModelEvent event) {
		if (event.getType() == GridModelEvent.ROWS_INSERTED) {
			if (orientation != SwingConstants.VERTICAL
					&& rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (orientation != SwingConstants.HORIZONTAL
					&& columnModel instanceof ResizableGrid) {
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
			if (orientation != SwingConstants.VERTICAL
					&& rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (orientation != SwingConstants.HORIZONTAL
					&& columnModel instanceof ResizableGrid) {
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
			if (orientation != SwingConstants.VERTICAL
					&& rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).insertColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (orientation != SwingConstants.HORIZONTAL
					&& columnModel instanceof ResizableGrid) {
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
			if (orientation != SwingConstants.VERTICAL
					&& rowModel instanceof ResizableGrid) {
				((ResizableGrid) rowModel).removeColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (orientation != SwingConstants.HORIZONTAL
					&& columnModel instanceof ResizableGrid) {
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

	private static class RowHeaderGridModel extends AbstractGridModel {
		private RulerModel ruler;

		public RowHeaderGridModel(RulerModel rowModel) {
			ruler = rowModel;
		}

		public Class<?> getClassAt(int row, int column) {
			return String.class;
		}

		public Object getValueAt(int row, int column) {
			return String.valueOf(row);
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public void setValueAt(Object value, int row, int column) {
		}

		public int getRowCount() {
			return ruler.getCount();
		}

		public int getColumnCount() {
			return 1;
		}
	}

	private static class ColumnHeaderGridModel extends AbstractGridModel {
		private RulerModel ruler;

		public ColumnHeaderGridModel(RulerModel columnModel) {
			ruler = columnModel;
		}

		public Class<?> getClassAt(int row, int column) {
			return String.class;
		}

		public Object getValueAt(int row, int column) {
			String result = "";
			for (; column >= 0; column = column / 26 - 1) {
				result = (char) ((char) (column % 26) + 'A') + result;
			}
			return result;
		}

		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public void setValueAt(Object value, int row, int column) {
		}

		public int getRowCount() {
			return 1;
		}

		public int getColumnCount() {
			return ruler.getCount();
		}
	}

	private static class GridHeaderRenderer extends StampLabel implements
			GridCellRenderer {

		private static final long serialVersionUID = -816888670171171361L;

		public GridHeaderRenderer() {
			super();
		}

		/**
		 * @see net.sf.jeppers.GridCellRenderer#getRendererComponent(int, int,
		 *      Object, CellStyle, boolean, boolean, JGrid)
		 */
		public Component getRendererComponent(int row, int column,
				Object value, CellStyle style, boolean isSelected,
				boolean hasFocus, JGrid grid) {
			if (isSelected) {
				setBackground(grid.getSelectionBackgroundColor());
			} else {
				setForeground(grid.getForeground());
				setBackground(grid.getBackground());
			}
			setFont(grid.getFont());
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalAlignment(SwingConstants.CENTER);
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}
}
