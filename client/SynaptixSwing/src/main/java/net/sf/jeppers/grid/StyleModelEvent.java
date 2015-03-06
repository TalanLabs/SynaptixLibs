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

/**
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class StyleModelEvent extends java.util.EventObject {
    /**
    * The entire model was changed
    */
    public static int MODEL_CHANGED = 0;
    /**
     * An editor was removed from the model
     */
    public static int EDITOR_REMOVED = 1;
    /**
     * An editor was added to the model
     */
    public static int EDITOR_ADDED = 2;
    /**
     * A renderer was removed from the model
     */
    public static int RENDERER_REMOVED = 3;
    /**
     * A renderer was added to the model
     */
    public static int RENDERER_ADDED = 4;
    /**
     * Renderers for a range of cells has been changed
     */
    public static int RENDERERS_CHANGED = 5;
    /**
     * Editors for a range of cells has been changed
     */
    public static int EDITORS_CHANGED = 6;
    /**
     * CellStyles have changed for a range of cells
     */
    public static int CELL_STYLES_CHANGED = 7;

    private int type;
    private int firstRow;
    private int firstColumn;
    private int lastRow;
    private int lastColumn;

    /**
     * Construct new Event corresponding to a change in a <code>StyleModel</code>. 
     */
    public StyleModelEvent(StyleModel source, int type) {
        super(source);
        this.type = type;
    }

    /**
     * Construct new Event corresponding to a change in a <code>StyleModel</code>. 
     */
    public StyleModelEvent(
        StyleModel source,
        int type,
        int firstRow,
        int firstColumn,
        int lastRow,
        int lastColumn) {
        super(source);
        this.type = type;
        this.firstRow = firstRow;
        this.firstColumn = firstColumn;
        this.lastRow = lastRow;
        this.lastColumn = lastColumn;
    }

    /**
    * Return the number of rows changed. 
    * Only valid for <code>RENDERERS_CHANGED</code>, 
    * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
    * event types
    */
    public int getRowCount() {
        return (lastRow - firstRow + 1);
    }

    /**
     * Return the number of columns changed
     * Only valid for <code>RENDERERS_CHANGED</code>, 
     * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
     * event types
     */
    public int getColumnCount() {
        return (lastColumn - firstColumn + 1);
    }

    /**
     * Return the index of the first row changed
     * Only valid for <code>RENDERERS_CHANGED</code>, 
     * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
     * event types
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Returns the index of the last row changed
     * Only valid for <code>RENDERERS_CHANGED</code>, 
     * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
     * event types
     */
    public int getLastRow() {
        return lastRow;
    }

    /**
     * Return the index of the first column changed
    	 * Only valid for <code>RENDERERS_CHANGED</code>, 
    	 * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
    	 * event types
     */
    public int getFirstColumn() {
        return firstColumn;
    }

    /**
     * Return the index of the last column changed
     * Only valid for <code>RENDERERS_CHANGED</code>, 
     * <code>EDITORS_CHANGED</code>, and <code>CELL_STYLES_CHANGED</code>
     * event types
     */
    public int getLastColumn() {
        return lastColumn;
    }

    /**
     * Return the event type
     */
    public int getType() {
        return type;
    }
}
