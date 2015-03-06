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
 * A base for <code>StyleModel</code> that provides handling of listeners.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public abstract class AbstractStyleModel implements StyleModel {
	/**
	  * List of event listeners
	  */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Add listener to model
	 */
	public void addStyleModelListener(StyleModelListener listener) {
		listenerList.add(StyleModelListener.class, listener);
	}

	/**
	 * Remove listener from model
	 */
	public void removeStyleModelListener(StyleModelListener listener) {
		listenerList.remove(StyleModelListener.class, listener);
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
	 * <code>StyleModelListeners</code> that registered
	 * themselves as listeners for this StyleModel.
	 *
	 * @param e  the event to be forwarded
	 *
	 * @see #addStyleModelListener
	 * @see StyleModelEvent
	 * @see EventListenerList
	 */
	public void fireStyleChanged(StyleModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == StyleModelListener.class) {
				((StyleModelListener) listeners[i + 1]).styleChanged(e);
			}
		}
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the cell styles for the range (firstRow, firstColumn) to (lastRow, lastColumn)
	* as changed
	*/
	public void fireCellStylesChanged(
		int firstRow,
		int firstColumn,
		int lastRow,
		int lastColumn) {
		StyleModelEvent e =
			new StyleModelEvent(
				this,
				StyleModelEvent.CELL_STYLES_CHANGED,
				firstRow,
				firstColumn,
				lastRow,
				lastColumn);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the cell styles for the (row, column) as changed
	*/
	public void fireCellStyleChanged(int row, int column) {
		fireCellStylesChanged(row, column, row, column);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the editors for the range (firstRow, firstColumn) to (lastRow, lastColumn)
	* have been changed
	*/
	public void fireEditorsChanged(
		int firstRow,
		int firstColumn,
		int lastRow,
		int lastColumn) {
		StyleModelEvent e =
			new StyleModelEvent(
				this,
				StyleModelEvent.EDITORS_CHANGED,
				firstRow,
				firstColumn,
				lastRow,
				lastColumn);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the editor for the (row, column) has been changed
	*/
	public void fireEditorChanged(int row, int column) {
		fireEditorsChanged(row, column, row, column);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the renderers for the range (firstRow, firstColumn) to (lastRow, lastColumn)
	* have been changed
	*/
	public void fireRenderersChanged(
		int firstRow,
		int firstColumn,
		int lastRow,
		int lastColumn) {
		StyleModelEvent e =
			new StyleModelEvent(
				this,
				StyleModelEvent.RENDERERS_CHANGED,
				firstRow,
				firstColumn,
				lastRow,
				lastColumn);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the renderer for the (row, column) has been changed
	*/
	public void fireRendererChanged(int row, int column) {
		fireRenderersChanged(row, column, row, column);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that an editor has been added
	*/
	public void fireEditorAdded() {
		StyleModelEvent e =
			new StyleModelEvent(this, StyleModelEvent.EDITOR_ADDED);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that a renderer has been added
	*/
	public void fireRendererAdded() {
		StyleModelEvent e =
			new StyleModelEvent(this, StyleModelEvent.RENDERER_ADDED);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that an editor has been removed
	*/
	public void fireEditorRemoved() {
		StyleModelEvent e =
			new StyleModelEvent(this, StyleModelEvent.EDITOR_REMOVED);
		fireStyleChanged(e);
	}

	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that a renderer has been removed
	*/
	public void fireRendererRemoved() {
		StyleModelEvent e =
			new StyleModelEvent(this, StyleModelEvent.RENDERER_REMOVED);
		fireStyleChanged(e);
	}
	
	/**
	* Notifies all <code>StyleModelListeners</code> that 
	* registered themselves as listeners for this StyleModel
	* that the entire model has changed
	*/
	public void fireModelChanged() {
		StyleModelEvent e =
			new StyleModelEvent(this, StyleModelEvent.MODEL_CHANGED);
		fireStyleChanged(e);
	}
}
