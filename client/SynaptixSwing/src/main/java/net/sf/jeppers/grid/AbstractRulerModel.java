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
 * A base for <code>RulerModel</code> that provides handling of listeners.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public abstract class AbstractRulerModel implements RulerModel {
    /**
      * List of event listeners
      */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Add listener to model
     */
    public void addRulerModelListener(RulerModelListener listener) {
        listenerList.add(RulerModelListener.class, listener);
    }

    /**
     * Remove listener from model
     */
    public void removeRulerModelListener(RulerModelListener listener) {
        listenerList.remove(RulerModelListener.class, listener);
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
     * <code>RulerModelListeners</code> that registered
     * themselves as listeners for this RulerModel.
     *
     * @param e  the event to be forwarded
     *
     * @see #addRulerModelListener
     * @see RulerModelEvent
     * @see EventListenerList
     */
    public void fireRulerChanged(RulerModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == RulerModelListener.class) {
                ((RulerModelListener) listeners[i + 1]).rulerChanged(e);
            }
        }
    }

    /**
    * Notifies all <code>RulerModelListeners</code> that 
    * registered themselves as listeners for this RulerModel
    * that the size at index changed
    */
    public void fireIndexChanged(int index) {
        fireIntervalChanged(index, index);
    }

    /**
    * Notifies all <code>RulerModelListeners</code> that 
    * registered themselves as listeners for this RulerModel
    * that the sizes between firstIndex and lastIndex have changed
    */
    public void fireIntervalChanged(int firstIndex, int lastIndex) {
        fireRulerChanged(new RulerModelEvent(this, firstIndex, lastIndex));
    }
}
