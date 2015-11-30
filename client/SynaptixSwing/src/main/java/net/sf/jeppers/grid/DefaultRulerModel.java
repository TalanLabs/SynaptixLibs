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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.SizeSequence;
import javax.swing.SwingConstants;

/**
 * Default implementation of <code>RulerModel</code>. 
 * Uses <code>javax.swing.SizeSequence</code> as underlying data structure.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class DefaultRulerModel
    extends AbstractRulerModel
    implements ResizableGrid, Serializable {
    transient private SizeSequence delegate = null;
    private int count;
    private int defaultSize;
    private int orientation;

    public DefaultRulerModel(
        int numEntries,
        int defaultSize,
        int orientation) {
        delegate = new SizeSequence(numEntries, defaultSize);
        count = numEntries;
        this.defaultSize = defaultSize;
        this.orientation = orientation;
    }

    public int getSize(int index) {
        return delegate.getSize(index);
    }

    public int getIndex(int position) {
        return delegate.getIndex(position);
    }

    public int getPosition(int index) {
        return delegate.getPosition(index);
    }

    public int getCount() {
        return count;
    }

    public void setSize(int index, int size) {
        delegate.setSize(index, size);
        fireIndexChanged(index);
    }

    public int getTotalSize() {
        int totalSize = 0;
        for (int i = 0; i < count; i++) {
            totalSize += delegate.getSize(i);
        }
        return totalSize;
    }

    public void insertRows(int row, int rowCount) {
        if (orientation == SwingConstants.VERTICAL) {
            delegate.insertEntries(row, rowCount, defaultSize);
            count += rowCount;
        }
    }

    public void removeRows(int row, int rowCount) {
        if (orientation == SwingConstants.VERTICAL) {
            delegate.removeEntries(row, rowCount);
            count -= rowCount;
        }
    }

    public void insertColumns(int column, int columnCount) {
        if (orientation == SwingConstants.HORIZONTAL) {
            delegate.insertEntries(column, columnCount, defaultSize);
            count += columnCount;
        }
    }

    public void removeColumns(int column, int columnCount) {
        if (orientation == SwingConstants.HORIZONTAL) {
            delegate.removeEntries(column, columnCount);
            count -= columnCount;
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        for (int i = 0; i < count; i++) {
            out.writeInt(getSize(i));
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        delegate = new SizeSequence(count, defaultSize);
        for (int i = 0; i < count; i++) {
            setSize(i, in.readInt());
        }
    }    
}
