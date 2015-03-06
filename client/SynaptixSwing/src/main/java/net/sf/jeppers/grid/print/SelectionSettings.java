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

import net.sf.jeppers.grid.SelectionModel;

/**
 * Holds settings of a SelectionModel
 * 
 * @author <a href="mailto:grom@capsicumcorp.com">Cameron Zemek</a>
 */
class SelectionSettings {
    private SelectionModel sm;
    private boolean isAdjusting;
    private int anchorRow, anchorCol, leadRow, leadCol;
    private int firstRow, lastRow, firstCol, lastCol;
    
    /**
     * Store the settings of the SelectionModel
     * 
     * @param sm SelectionModel to get settings from
     */
    public SelectionSettings(SelectionModel sm) {
        this.sm = sm;
        save();
    }
    
    private void save() {
        isAdjusting = sm.getValueIsAdjusting();        
        anchorRow = sm.getAnchorRow();
        anchorCol = sm.getAnchorColumn();
        leadRow = sm.getLeadRow();
        leadCol = sm.getLeadColumn();
        firstRow = sm.getFirstSelectedRow();
        lastRow = sm.getLastSelectedRow();
        firstCol = sm.getFirstSelectedColumn();
        lastCol = sm.getLastSelectedColumn();
    }
    
    /**
     * Clear the SelectionModel
     */
    public void clear() {
        sm.setValueIsAdjusting(true);
        sm.clearSelection();
    }
    
    /**
     * Restore the SelectionModel
     */
    public void restore() {        
        sm.setSelectionRange(firstRow, firstCol, lastRow, lastCol);
        sm.setAnchor(anchorRow, anchorCol);
        sm.setLead(leadRow, leadCol);
        sm.setValueIsAdjusting(isAdjusting);
    }
}
