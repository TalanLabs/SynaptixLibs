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
 * This interface captures the model for handling a component that can be 
 * segmented along the horizontal or vertical axis, with each segment size 
 * being separately tunable.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface RulerModel {
    public void addRulerModelListener(RulerModelListener listener);
    public void removeRulerModelListener(RulerModelListener listener);

    /**
     * Returns the size of the specified entry.
     * 
     * @param index		the index corresponding to the entry
     * @return	the size of the entry
     */
    public int getSize(int index);

    /**
     * Returns the index of the entry that contains the specified position.
     * 
     * @param position		the position of the entry
     * @return the index of the entry that occupies the specified position
     */
    public int getIndex(int position);

    /**
     * Returns the start position for the specified entry.
     * 
     * @param index		the index of the entry whose position is desired
     * @return	the starting position of the specified entry
     */
    public int getPosition(int index);

    /**
     * Returns the number of entries.
     * 
     * @return	number of entries
     */
    public int getCount();

    /**
     * Sets the size of the specified entry.
     * 
     * @param index		the index corresponding to the entry
     * @param size		the size of the entry
     */
    public void setSize(int index, int size);

    /**
     * Returns the total size of the entries.
     * 
     * @return	the total size of the entries
     */
    public int getTotalSize();
}
