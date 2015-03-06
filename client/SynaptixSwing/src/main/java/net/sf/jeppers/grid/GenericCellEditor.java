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
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 * Generic implementation of <code>GridCellEditor</code> that uses the 
 * <code>toString</code> method.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class GenericCellEditor
    extends DefaultCellEditor
    implements GridCellEditor {
    /**
     * @see net.sf.jeppers.grid.GridCellEditor#getEditorComponent(int, int, Object, CellStyle, boolean, JGrid)
     */
    public Component getEditorComponent(
        int row,
        int column,
        Object value,
        CellStyle style,
        boolean isSelected,
        JGrid grid) {
    	editorComponent.setBorder(new LineBorder(Color.black));    	
    	editorComponent.setForeground(style.getForegroundColor());
    	editorComponent.setBackground(style.getBackgroundColor());
        delegate.setValue(value);
        return editorComponent;
    }

    /**
    * Constructs a <code>GenericCellEditor</code> that uses a text field.
    *
    * @param x  a <code>JTextField</code> object
    */
    public GenericCellEditor(final JTextField textField) {
        super(textField);
    }

    /**
     * Constructs a <code>GenericCellEditor</code> object that uses a check box.
     *
     * @param x  a <code>JCheckBox</code> object
     */
    public GenericCellEditor(final JCheckBox checkBox) {
        super(checkBox);
    }

    /**
     * Constructs a <code>GenericCellEditor</code> object that uses a combo box.
     *
     * @param x  a <code>JComboBox</code> object
     */
    public GenericCellEditor(final JComboBox comboBox) {
        super(comboBox);
    }
}