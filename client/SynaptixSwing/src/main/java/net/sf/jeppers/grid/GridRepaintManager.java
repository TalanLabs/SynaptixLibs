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
 * Handles repainting of grid.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class GridRepaintManager
    implements
        GridModelListener,
        SelectionModelListener,
        RulerModelListener,
        SpanModelListener,
        StyleModelListener {
    private JGrid grid = null;

    public GridRepaintManager(JGrid grid) {
        this.grid = grid;
    }

    protected void repaint() {
        grid.repaint();
    }

    protected void resizeAndRepaint() {
        grid.revalidate();
        grid.repaint();
    }

    //
    // Repaint listeners. The implementation of repainting should be improved and 
    // only repaint the regions which have changed.
    //

    public void gridChanged(GridModelEvent e) {
        if (e.getType() == GridModelEvent.CELLS_UPDATED
            || e.getType() == GridModelEvent.ROWS_UPDATED
            || e.getType() == GridModelEvent.COLUMNS_UPDATED
            || e.getType() == GridModelEvent.MODEL_CHANGED) {
            repaint();
        }
    }

    public void selectionChanged(SelectionModelEvent e) {
        repaint();
    }

    public void rulerChanged(RulerModelEvent e) {
        resizeAndRepaint();
    }

    public void spanChanged(SpanModelEvent e) {
        repaint();
    }

    public void styleChanged(StyleModelEvent e) {
        repaint();
    }
}
