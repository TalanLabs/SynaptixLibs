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
import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Default implementation of <code>StyleModel</code> that uses a
 * <code>Map</code> between class types and editors / renderers.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class DefaultStyleModel extends AbstractStyleModel implements
		ResizableGrid, Serializable {

	private static final long serialVersionUID = 3767637049262462862L;

	private Map<Class<?>, GridCellEditor> defaultEditors = new HashMap<Class<?>, GridCellEditor>();

	private Map<Class<?>, GridCellRenderer> defaultRenderers = new HashMap<Class<?>, GridCellRenderer>();

	private Map<Point, GridCellEditor> editors = new HashMap<Point, GridCellEditor>();

	private Map<Point, GridCellRenderer> renderers = new HashMap<Point, GridCellRenderer>();

	private CellStyle defaultStyle;

	private Map<Point, CellStyle> styleMap = new HashMap<Point, CellStyle>();

	private Point point = new Point();

	public DefaultStyleModel() {
		createDefaults();
	}

	private void createDefaults() {
		GridCellRenderer defaultRenderer = new GenericCellRenderer();
		defaultRenderers.put(Object.class, defaultRenderer);
		GridCellEditor defaultEditor = new GenericCellEditor(new JTextField());
		defaultEditors.put(Object.class, defaultEditor);
		defaultStyle = new DefaultCellStyle();
	}

	public GridCellEditor getEditor(Class<?> aClass, int row, int column,
			JGrid grid) {
		point.setLocation(column, row);
		GridCellEditor editor = editors.get(point);
		if (editor != null) {
			return editor;
		} else {
			editor = (GridCellEditor) defaultEditors.get(aClass);
			if (editor != null) {
				return editor;
			} else if (aClass != null && aClass.getSuperclass() != null) {
				return getEditor(aClass.getSuperclass(), row, column, grid);
			} else {
				return getEditor(Object.class, row, column, grid);
			}
		}
	}

	public void setDefaultEditor(Class<?> clazz, GridCellEditor editor) {
		defaultEditors.put(clazz, editor);
	}

	public void setEditor(GridCellEditor editor, int row, int column) {
		editors.put(new Point(column, row), editor);
	}

	public GridCellRenderer getRenderer(Class<?> aClass, int row, int column,
			JGrid grid) {
		point.setLocation(column, row);
		GridCellRenderer renderer = renderers.get(point);
		if (renderer != null) {
			return renderer;
		} else {
			renderer = (GridCellRenderer) defaultRenderers.get(aClass);
			if (renderer != null) {
				return renderer;
			} else if (aClass != null && aClass.getSuperclass() != null) {
				return getRenderer(aClass.getSuperclass(), row, column, grid);
			} else {
				return getRenderer(Object.class, row, column, grid);
			}
		}
	}

	public void setDefaultRenderer(Class<?> clazz, GridCellRenderer renderer) {
		defaultRenderers.put(clazz, renderer);
	}

	public void setRenderer(GridCellRenderer renderer, int row, int column) {
		renderers.put(new Point(column, row), renderer);
	}

	private void updateSubComponentUI(Object componentShell) {
		if (componentShell == null) {
			return;
		}
		Component component = null;
		if (componentShell instanceof Component) {
			component = (Component) componentShell;
		}
		if (componentShell instanceof GenericCellEditor) {
			component = ((GenericCellEditor) componentShell).getComponent();
		}

		if (component != null && component instanceof JComponent) {
			((JComponent) component).updateUI();
		}
	}

	public void updateUI() {
		Iterator<GridCellRenderer> rendererIter = defaultRenderers.values()
				.iterator();
		while (rendererIter.hasNext()) {
			updateSubComponentUI(rendererIter.next());
		}

		Iterator<GridCellEditor> editorIter = defaultEditors.values()
				.iterator();
		while (editorIter.hasNext()) {
			updateSubComponentUI(editorIter.next());
		}
	}

	public CellStyle getDefaultCellStyle() {
		return defaultStyle;
	}

	public void setDefaultCellStyle(CellStyle defaultStyle) {
		this.defaultStyle = defaultStyle;
	}

	public CellStyle getCellStyle(int row, int column) {
		point.setLocation(column, row);
		CellStyle style = (CellStyle) styleMap.get(point);
		if (style == null) {
			return getDefaultCellStyle();
		}
		return style;
	}

	public void setCellStyle(CellStyle style, int row, int column) {
		styleMap.put(new Point(column, row), style);
		fireCellStyleChanged(row, column);
	}

	public void insertRows(int row, int rowCount) {
		Map<Point, CellStyle> oldStyleMap = styleMap;
		styleMap = new HashMap<Point, CellStyle>();
		Iterator<Point> i = oldStyleMap.keySet().iterator();
		while (i.hasNext()) {
			Point cell = (Point) i.next();
			CellStyle style = (CellStyle) oldStyleMap.get(cell);
			if (cell.y < row) {
				// leave style in same position
				styleMap.put(cell, style);
			} else {
				// move style down
				cell.y += rowCount;
				styleMap.put(cell, style);
			}
		}

		Map<Point, GridCellEditor> oldEditorMap = editors;
		editors = new HashMap<Point, GridCellEditor>();
		Iterator<Point> j = oldEditorMap.keySet().iterator();
		while (j.hasNext()) {
			Point cell = (Point) j.next();
			GridCellEditor editor = (GridCellEditor) oldEditorMap.get(cell);
			if (cell.y < row) {
				editors.put(cell, editor);
			} else {
				cell.y += rowCount;
				editors.put(cell, editor);
			}
		}

		Map<Point, GridCellRenderer> oldRendererMap = renderers;
		renderers = new HashMap<Point, GridCellRenderer>();
		Iterator<Point> k = oldRendererMap.keySet().iterator();
		while (k.hasNext()) {
			Point cell = (Point) k.next();
			GridCellRenderer renderer = (GridCellRenderer) oldRendererMap
					.get(cell);
			if (cell.y < row) {
				renderers.put(cell, renderer);
			} else {
				cell.y += rowCount;
				renderers.put(cell, renderer);
			}
		}
	}

	public void removeRows(int row, int rowCount) {
		Map<Point, CellStyle> oldStyleMap = styleMap;
		styleMap = new HashMap<Point, CellStyle>();
		Iterator<Point> i = oldStyleMap.keySet().iterator();
		while (i.hasNext()) {
			Point cell = (Point) i.next();
			CellStyle style = (CellStyle) oldStyleMap.get(cell);
			if (cell.y < row) {
				// leave style in same position
				styleMap.put(cell, style);
			} else if (cell.y >= row + rowCount) {
				// move style up
				cell.y -= rowCount;
				styleMap.put(cell, style);
			}
		}

		Map<Point, GridCellEditor> oldEditorMap = editors;
		editors = new HashMap<Point, GridCellEditor>();
		Iterator<Point> j = oldEditorMap.keySet().iterator();
		while (j.hasNext()) {
			Point cell = (Point) j.next();
			GridCellEditor editor = (GridCellEditor) oldEditorMap.get(cell);
			if (cell.y < row) {
				editors.put(cell, editor);
			} else if (cell.y >= row + rowCount) {
				cell.y -= rowCount;
				editors.put(cell, editor);
			}
		}

		Map<Point, GridCellRenderer> oldRendererMap = renderers;
		renderers = new HashMap<Point, GridCellRenderer>();
		Iterator<Point> k = oldRendererMap.keySet().iterator();
		while (k.hasNext()) {
			Point cell = (Point) k.next();
			GridCellRenderer renderer = (GridCellRenderer) oldRendererMap
					.get(cell);
			if (cell.y < row) {
				renderers.put(cell, renderer);
			} else if (cell.y >= row + rowCount) {
				cell.y -= rowCount;
				renderers.put(cell, renderer);
			}
		}
	}

	public void insertColumns(int column, int columnCount) {
		Map<Point, CellStyle> oldStyleMap = styleMap;
		styleMap = new HashMap<Point, CellStyle>();
		Iterator<Point> i = oldStyleMap.keySet().iterator();
		while (i.hasNext()) {
			Point cell = (Point) i.next();
			CellStyle style = (CellStyle) oldStyleMap.get(cell);
			if (cell.x < column) {
				// leave style in same position
				styleMap.put(cell, style);
			} else {
				// move style right
				cell.x += columnCount;
				styleMap.put(cell, style);
			}
		}

		Map<Point, GridCellEditor> oldEditorMap = editors;
		editors = new HashMap<Point, GridCellEditor>();
		Iterator<Point> j = oldEditorMap.keySet().iterator();
		while (j.hasNext()) {
			Point cell = (Point) j.next();
			GridCellEditor editor = (GridCellEditor) oldEditorMap.get(cell);
			if (cell.y < column) {
				editors.put(cell, editor);
			} else {
				cell.y += columnCount;
				editors.put(cell, editor);
			}
		}

		Map<Point, GridCellRenderer> oldRendererMap = renderers;
		renderers = new HashMap<Point, GridCellRenderer>();
		Iterator<Point> k = oldRendererMap.keySet().iterator();
		while (k.hasNext()) {
			Point cell = (Point) k.next();
			GridCellRenderer renderer = (GridCellRenderer) oldRendererMap
					.get(cell);
			if (cell.x < column) {
				renderers.put(cell, renderer);
			} else {
				cell.x += columnCount;
				renderers.put(cell, renderer);
			}
		}
	}

	public void removeColumns(int column, int columnCount) {
		Map<Point, CellStyle> oldStyleMap = styleMap;
		styleMap = new HashMap<Point, CellStyle>();
		Iterator<Point> i = oldStyleMap.keySet().iterator();
		while (i.hasNext()) {
			Point cell = (Point) i.next();
			CellStyle style = (CellStyle) oldStyleMap.get(cell);
			if (cell.x < column) {
				// leave style in same position
				styleMap.put(cell, style);
			} else if (cell.x >= column + columnCount) {
				// move style left
				cell.x -= columnCount;
				styleMap.put(cell, style);
			}
		}

		Map<Point, GridCellEditor> oldEditorMap = editors;
		editors = new HashMap<Point, GridCellEditor>();
		Iterator<Point> j = oldEditorMap.keySet().iterator();
		while (j.hasNext()) {
			Point cell = (Point) j.next();
			GridCellEditor editor = (GridCellEditor) oldEditorMap.get(cell);
			if (cell.y < column) {
				editors.put(cell, editor);
			} else if (cell.x >= column + columnCount) {
				cell.y -= columnCount;
				editors.put(cell, editor);
			}
		}

		Map<Point, GridCellRenderer> oldRendererMap = renderers;
		renderers = new HashMap<Point, GridCellRenderer>();
		Iterator<Point> k = oldRendererMap.keySet().iterator();
		while (k.hasNext()) {
			Point cell = (Point) k.next();
			GridCellRenderer renderer = (GridCellRenderer) oldRendererMap
					.get(cell);
			if (cell.x < column) {
				renderers.put(cell, renderer);
			} else if (cell.x >= column + columnCount) {
				cell.x -= columnCount;
				renderers.put(cell, renderer);
			}
		}
	}
}
