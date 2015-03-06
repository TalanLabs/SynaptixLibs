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
public class SpanModelEvent extends java.util.EventObject {
    /**
     * The entire model was changed
     */
    public static int MODEL_CHANGED = 0;
    public static int SPAN_ADDED = 1;
    public static int SPAN_REMOVED = 2;
    public static int SPAN_UPDATED = 3;

    private int type;
    private int anchorRow;
    private int anchorColumn;
    private int oldRowCount;
    private int oldColumnCount;
    private int newRowCount;
    private int newColumnCount;

    /**
     * Constructs a new event for a change to a <code>SpanModel</code>
     */
    public SpanModelEvent(
        SpanModel source,
        int type,
        int anchorRow,
        int anchorColumn,
        int oldRowCount,
        int oldColumnCount,
        int newRowCount,
        int newColumnCount) {
        super(source);
        this.type = type;
        this.anchorRow = anchorRow;
        this.anchorColumn = anchorColumn;
        this.oldRowCount = oldRowCount;
        this.oldColumnCount = oldColumnCount;
        this.newRowCount = newRowCount;
        this.newColumnCount = newColumnCount;
    }

    /**
     *  Returns the anchor (top) row of the span event
     */
    public int getAnchorRow() {
        return anchorRow;
    }

    /**
     * Returns the anchor (leftmost) column of the span
     */
    public int getAnchorColumn() {
        return anchorColumn;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_REMOVED</code> events
     */
    public int getOldRowCount() {
        return oldRowCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_REMOVED</code> events
     */
    public int getOldColumnCount() {
        return oldColumnCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_ADDED</code> events
     */
    public int getNewRowCount() {
        return newRowCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_ADDED</code> events
     */
    public int getNewColumnCount() {
        return newColumnCount;
    }

    /**
     * Returns the type of the event
     */
    public int getType() {
        return type;
    }
}
