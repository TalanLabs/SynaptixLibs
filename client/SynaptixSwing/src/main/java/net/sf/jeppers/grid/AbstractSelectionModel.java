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
 * A base for <code>SelectionModel</code> that provides handling of listeners.
 *
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public abstract class AbstractSelectionModel implements SelectionModel {
    /**
     * List of event listeners
     */
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean isAdjusting = false;

    public void addSelectionModelListener(SelectionModelListener listener) {
        listenerList.add(SelectionModelListener.class, listener);
    }

    public void removeSelectionModelListener(SelectionModelListener listener) {
        listenerList.remove(SelectionModelListener.class, listener);
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

    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }

    public void setValueIsAdjusting(boolean isAdjusting) {
        this.isAdjusting = isAdjusting;
    }

    //
    // Fire methods
    //

    /**
     * Forwards the given notification event to all
     * <code>SelectionModelListeners</code> that registered
     * themselves as listeners for this SelectionModel.
     *
     * @param e  the event to be forwarded
     *
     * @see #addSelectionModelListener
     * @see SelectionModelEvent
     * @see EventListenerList
     */
    public void fireSelectionChanged(SelectionModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SelectionModelListener.class) {
                ((SelectionModelListener) listeners[i + 1]).selectionChanged(e);
            }
        }
    }

    /**
     * Notifies all <code>SelectionModelListeners</code> that 
     * registered themselves as listeners for this SelectionModel
     * that the selection has changed
     */
    public void fireSelectionChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SelectionModelListener.class) {
                ((SelectionModelListener) listeners[i + 1]).selectionChanged(
                    new SelectionModelEvent(this, isAdjusting));
            }
        }
    }
}
