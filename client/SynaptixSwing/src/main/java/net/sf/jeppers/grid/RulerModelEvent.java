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
public class RulerModelEvent extends java.util.EventObject {
    private int firstIndex;
    private int lastIndex;

    /**
     * The entries in the range <code>[firstIndex, lastIndex]</code> have 
     * been changed.
     */
    public RulerModelEvent(RulerModel source, int firstIndex, int lastIndex) {
        super(source);
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
    }

    /**
     * Returns the first index of the interval that changed.
     * 
     * @return	first index of interval change
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Returns the last index of the interval that changed.
     * 
     * @return	last index of interval change
     */
    public int getLastIndex() {
        return lastIndex;
    }
}
