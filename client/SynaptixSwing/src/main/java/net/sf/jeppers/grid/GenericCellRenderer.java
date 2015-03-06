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

import java.awt.*;
import javax.swing.border.*;
import java.text.*;

/**
 * Generic implementation of <code>GridCellRenderer</code>. This implementation 
 * applies the cell's format to the contents.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class GenericCellRenderer
	extends StampLabel
	implements GridCellRenderer {

	public Component getRendererComponent(
		int row,
		int column,
		Object value,
		CellStyle style,
		boolean isSelected,
		boolean hasFocus,
		JGrid grid){
		if(value == null){
			value = "";
		}
		
		// Format value
		Format formatter = style.getFormat();
		if(formatter != null){
			try{
				value = formatter.format(value);
			}catch(Exception e){
				/* Ignore formatting errors */
			}
		}
		
		if (isSelected && hasFocus) {
			setForeground(grid.getFocusForegroundColor());
			setBackground(grid.getFocusBackgroundColor());				
		} else if (isSelected) {
			setForeground(grid.getSelectionForegroundColor());
			setBackground(grid.getSelectionBackgroundColor());
		} else {
			setForeground(style.getForegroundColor());
			setBackground(style.getBackgroundColor());
		}
		setFont(style.getFont());
		setBorder(new EmptyBorder(style.getPadding()));
		setHorizontalAlignment(style.getHorizontalAlignment());
		setVerticalAlignment(style.getVerticalAlignment());
		setText(value.toString());
		return this;
	}
}
