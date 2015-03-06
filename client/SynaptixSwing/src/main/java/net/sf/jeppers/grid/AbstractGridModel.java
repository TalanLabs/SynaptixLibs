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

import java.util.EventListener;
import javax.swing.event.EventListenerList;

/**
 * This abstract class provides default implementations for most of the methods 
 * in the <code>GridModel</code> interface. It takes care of the management of 
 * listeners and provides some conveniences for generating 
 * <code>GridModelEvents</code> and dispatching them to the listeners. To create 
 * a concrete <code>GridModel</code> as a subclass of 
 * <code>AbstractGridModel</code> you need only provide implementations 
 * for the following three methods: 
 * 
 * <pre>
 * public int getRowCount();
 * public int getColumnCount();
 * public Object getValueAt(int row, int column);
 * </pre>
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public abstract class AbstractGridModel implements GridModel {
    /**
      * List of event listeners
      */
    protected EventListenerList listenerList = new EventListenerList();
    
    public void addGridModelListener(GridModelListener listener) {
        listenerList.add(GridModelListener.class, listener);
    }

    public void removeGridModelListener(GridModelListener listener) {
        listenerList.remove(GridModelListener.class, listener);
    }

    /**
     * Returns an array of all the listeners of the given type that
     * were added to this model.
     *
     * @return all of the objects receiving <code>listenerType</code>
     *		notifications from this model
     */
    public EventListener[] getListeners(Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

    //
    // Fire methods
    //

    /**
     * Forwards the given notification event to all
     * <code>GridModelListeners</code> that registered
     * themselves as listeners for this GridModel.
     *
     * @param e  the event to be forwarded
     *
     * @see #addGridModelListener
     * @see GridModelEvent
     * @see EventListenerList
     */
    public void fireGridChanged(GridModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == GridModelListener.class) {
                ((GridModelListener) listeners[i + 1]).gridChanged(e);
            }
        }
    }

    /**
     * Notifies all <code>GridModelListeners</code> that registered
     * themselves as listeners for this GridModel that the cell at
     * <code>row</code> and <code>column</code> has been updated
     */
    public void fireGridCellUpdated(int row, int column) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.CELLS_UPDATED,
                row,
                column,
                row,
                column);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the rows in the range <code>[firstRow, lastRow]</code>,
     * inclusive, have been inserted.
     */
    public void fireGridRowsInserted(int firstRow, int lastRow) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.ROWS_INSERTED,
                firstRow,
                0,
                lastRow,
                getColumnCount() - 1);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the rows in the range <code>[firstRow, lastRow]</code>,
     * inclusive, have been updated.
     */
    public void fireGridRowsUpdated(int firstRow, int lastRow) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.ROWS_UPDATED,
                firstRow,
                0,
                lastRow,
                this.getColumnCount() - 1);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the rows in the range <code>[firstRow, lastRow]</code>,
     * inclusive, have been deleted.
     */
    public void fireGridRowsDeleted(int firstRow, int lastRow) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.ROWS_DELETED,
                firstRow,
                0,
                lastRow,
                this.getColumnCount() - 1);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the columns in the range <code>[firstColumn, lastColumn]</code>,
     * inclusive, have been inserted.
     */
    public void fireGridColumnsInserted(int firstColumn, int lastColumn) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.COLUMNS_INSERTED,
                0,
                firstColumn,
                this.getRowCount() - 1,
                lastColumn);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the columns in the range <code>[firstColumn, lastColumn]</code>,
     * inclusive, have been inserted.
     */
    public void fireGridColumnsUpdated(int firstColumn, int lastColumn) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.COLUMNS_UPDATED,
                0,
                firstColumn,
                this.getRowCount() - 1,
                lastColumn);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the columns in the range <code>[firstColumn, lastColumn]</code>,
     * inclusive, have been deleted.
     */
    public void fireGridColumnsDeleted(int firstColumn, int lastColumn) {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.COLUMNS_DELETED,
                0,
                firstColumn,
                this.getRowCount() - 1,
                lastColumn);
        fireGridChanged(e);
    }

    /**
     * Notifies all <code>GridModelListeners</code> that 
     * registered themselves as listeners for this GridModel
     * that the entire model has changed
     */
    public void fireGridModelChanged() {
        GridModelEvent e =
            new GridModelEvent(
                this,
                GridModelEvent.MODEL_CHANGED,
                0,
                0,
                this.getRowCount() - 1,
                this.getColumnCount() - 1);
        fireGridChanged(e);
    }
}
