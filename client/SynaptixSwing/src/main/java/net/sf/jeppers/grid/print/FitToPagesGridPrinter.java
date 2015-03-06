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
public class FitToPagesGridPrinter implements Printable {
    private JGrid grid;
    private int maxNumPage;
    private int pageRows;
    private int pageCols;

    public FitToPagesGridPrinter(JGrid grid, int pageRows, int pageCols) {
        this.grid = grid;
        this.pageRows = pageRows;
        this.pageCols = pageCols;
        maxNumPage = pageRows * pageCols;
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
        throws PrinterException {
        // Check there is something to print
        if (pageIndex >= maxNumPage 
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
        
        // Check there is something visible to print
        if (gridWidth == 0 || gridHeight == 0) {
            return Printable.NO_SUCH_PAGE;
        }        
        
        // Calculate page to print
        int pageColumn = pageIndex % pageCols;
        int pageRow = pageIndex / pageCols;    
        
        // Calculate scale to make the grid fit in maxNumPage
        float xFitPageScale = (pageWidth * pageCols) / (float) gridWidth;
        float yFitPageScale = (pageHeight * pageRows) / (float) gridHeight;     
        float minScale = Math.min(xFitPageScale, yFitPageScale);
        
        // Scale the print area
        int pageX = (int) ((pageColumn * pageWidth) / minScale);
        int pageY = (int) ((pageRow * pageHeight) / minScale);
        pageWidth = (int) (pageWidth / minScale);
        pageHeight = (int) (pageHeight / minScale);
        
        // Get clip region for page    
        Point p = new Point(pageX, pageY);
        int firstRow = grid.rowAtPoint(p);
        int firstCol = grid.columnAtPoint(p);
        int x1 = grid.getColumnPosition(firstCol);   
        int y1 = grid.getRowPosition(firstRow);       
        int x2 = x1 + pageWidth;
        int y2 = y1 + pageHeight;        
        p = new Point(x2, y2);
        int lastRow = grid.rowAtPoint(p);
        int lastCol = grid.columnAtPoint(p);
        if (grid.getRowPosition(lastRow) + grid.getRowHeight(lastRow) > y2) {
            y2 = grid.getRowPosition(lastRow);
        } else {
            y2 = grid.getRowPosition(lastRow) + grid.getRowHeight(lastRow);
        }
        if (grid.getColumnPosition(lastCol) + grid.getColumnWidth(lastCol) > x2) {
            x2 = grid.getColumnPosition(lastCol);
        } else {
            x2 = grid.getColumnPosition(lastCol) + grid.getColumnWidth(lastCol);
        }
        int x = x1;
        int y = y1;
        int width = x2 - x1;
        int height = y2 - y1;
        
        SelectionSettings selectionSettings = new SelectionSettings(
                grid.getSelectionModel());                        
        selectionSettings.clear();
        
        // Print the page
        g2d.scale(minScale, minScale);
        g2d.translate(-x, -y);
        g2d.setClip(x, y, width, height);
        grid.print(g2d);
        
        selectionSettings.restore();
        
        return Printable.PAGE_EXISTS;
    }
}
