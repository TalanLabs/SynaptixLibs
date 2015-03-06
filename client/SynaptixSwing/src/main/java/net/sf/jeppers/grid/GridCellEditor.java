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
import javax.swing.CellEditor;

/**
 * This interface defines the method that editors must implement to be used
 * as an editor by <code>JGrid</code>.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface GridCellEditor extends CellEditor {
	/**
	 * <p>Sets an initial <code>value</code> for the editor. This will cause the editor to 
	 * <code>stopEditing</code> and lose any partially edited value if the editor is editing when 
	 * this method is called.</p>
	 * 
	 * <p>Returns the component that should be added to the client's <code>Component</code> 
	 * hierarchy. Once installed in the client's hierarchy this component will then be able to 
	 * draw and receive user input.</p>
	 * 
	 * @param row			the row of the cell being edited
	 * @param column		the column of the cell being edited
	 * @param value			the value of the cell to be edited; it is up to the specific editor to 
	 * 								interpret and draw the value. For example, if value is the string "true", 
	 * 								it could be rendered as a string or it could be rendered as a check 
	 * 								box that is checked. <code>null</code>  is a valid value
	 * @param style			the style to be use for rendering the cell
	 * @param isSelected	true if the cell is to be rendered with highlighting
	 * @param grid			the <code>JGrid</code> that is asking the editor to edit; 
	 * 								can be <code>null</code>
	 * 
	 * @return					the component for editing
	 */
	public Component getEditorComponent(
		int row,
		int column,
		Object value,
		CellStyle style,
		boolean isSelected,
		JGrid grid);
}
