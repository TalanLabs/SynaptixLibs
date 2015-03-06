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

/**
 * StampLabel overrides methods as per the documentation from Sun's JDK 1.4.2
 * JavaDoc for DefaultTableCellRenderer. Refer to the <a
 * href="http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/table/DefaultTableCellRenderer.html#override">
 * Implementation note </a> for further details.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek </a>
 */
public class StampLabel extends JLabel {
    public boolean isOpaque() {
        return true;
    }

    public void validate() {
    }

    public void revalidate() {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void repaint(Rectangle r) {
    }

    protected void firePropertyChange(
        String propertyName,
        Object oldValue,
        Object newValue) {
    }

    public void firePropertyChange(
        String propertyName,
        boolean oldValue,
        boolean newValue) {
    }

    /**
	 * Sets the <code>String</code> object for the cell being rendered to
	 * <code>value</code>.
	 * 
	 * @param value
	 *            the string value for this cell; if value is <code>null</code>
	 *            it sets the text value to an empty string
	 * @see JLabel#setText
	 */
    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }
}
