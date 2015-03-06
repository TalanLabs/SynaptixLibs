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
 * Events corresponding to changes in a GridModel. These events may be:
 * <ul>
 * <li>Data changed in some cells</li>
 * <li>A continuous set of rows/colums was inserted</li>
 * <li>A continuous set of rows/columns was deleted</li>
 * </ul>
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class GridModelEvent extends java.util.EventObject {
    /**
    * The entire model was changed.
    */
    public static final int MODEL_CHANGED = 0;
    /**
     * A range of cells were updated.
     */
    public static final int CELLS_UPDATED = 1;
    /**
     * A continuous set of rows were updated.
     */
    public static final int ROWS_UPDATED = 2;
    /**
     * A continuous set of rows were inserted into the model.
     */
    public static final int ROWS_INSERTED = 3;
    /**
     * A continuous set of rows were deleted from the model.
     */
    public static final int ROWS_DELETED = 4;
    /**
     * A continuous set of columns were updated.
     */
    public static final int COLUMNS_UPDATED = 5;
    /**
     * A continuous set of columns were inserted into the model.
     */
    public static final int COLUMNS_INSERTED = 6;
    /**
     * A continuous set of columns were removed from the model.
     */
    public static final int COLUMNS_DELETED = 7;

    private int type;
    private int firstRow;
    private int firstColumn;
    private int lastRow;
    private int lastColumn;

    /** Creates a new instance of GridModelEvent */
    public GridModelEvent(
        GridModel source,
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
    * Return the number of rows changed
    */
    public int getRowCount() {
        return (lastRow - firstRow + 1);
    }

    /**
     * Return the number of columns changed
     */
    public int getColumnCount() {
        return (lastColumn - firstColumn + 1);
    }

    /**
     * Return the index of the first row changed
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Returns the index of the last row changed
     */
    public int getLastRow() {
        return lastRow;
    }

    /**
     * Return the index of the first column changed
     */
    public int getFirstColumn() {
        return firstColumn;
    }

    /**
     * Return the index of the last column changed
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
