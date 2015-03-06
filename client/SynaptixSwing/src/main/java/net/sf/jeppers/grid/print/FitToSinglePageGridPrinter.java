/* 
 * Copyright (c) 2005, Cameron Zemek
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
package net.sf.jeppers.grid.print;

import java.awt.*;
import java.awt.print.*;

import net.sf.jeppers.grid.*;

/**
 * @author <a href="mailto:grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class FitToSinglePageGridPrinter implements Printable {
    private JGrid grid;

    /** Fit grid to <code>pageRows * pageCols</code> */
    public FitToSinglePageGridPrinter(JGrid grid) {
        this.grid = grid;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
        throws PrinterException {
        if (pageIndex >= 1
            || grid.getRowCount() == 0
            || grid.getColumnCount() == 0) {
            return Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(
            (int) pageFormat.getImageableX(),
            (int) pageFormat.getImageableY());

        int pageWidth = (int) pageFormat.getImageableWidth();
        int pageHeight = (int) pageFormat.getImageableHeight();
        int gridWidth = grid.getColumnModel().getTotalSize();
        int gridHeight = grid.getRowModel().getTotalSize();
        if (gridWidth == 0 || gridHeight == 0) {
            return Printable.NO_SUCH_PAGE;
        }

        int x = 0;
        int y = 0;
        
        float xFitPageScale = pageWidth / (float) gridWidth;
        float yFitPageScale = pageHeight / (float) gridHeight;     
        float minScale = Math.min(xFitPageScale, yFitPageScale);                
        
        // scale the available page size
        x = (int) (x / minScale);
        y = (int) (y / minScale);
        pageWidth = (int) (pageWidth / minScale);
        pageHeight = (int) (pageHeight / minScale);
        
        SelectionSettings selectionSettings = new SelectionSettings(
                grid.getSelectionModel());                        
        selectionSettings.clear();
        
        // Print the page
        g2d.scale(minScale, minScale);                
        grid.paint(g2d);
        g2d.dispose();
            
        selectionSettings.restore();

        return Printable.PAGE_EXISTS;
    }
}
