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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

/**
 * JScrollGrid is used to display JGrid in a scroll panel with row and column
 * headers.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class JSpanScrollGrid extends JPanel implements RulerModelListener {

	private static final long serialVersionUID = 2226847234127195641L;

	private JSpanGridHeader spanRowHeader;

	private JSpanGridHeader spanColumnHeader;

	private JScrollPane scrollGrid = null;

	private JGridHeader rowHeader = null;

	private JGridHeader columnHeader = null;

	private JGrid grid = null;

	private boolean showRowHeader;

	private boolean showColumnHeader;

	public JSpanScrollGrid(JGrid grid) {
		this.grid = grid;

		spanRowHeader = new JSpanGridHeader(SwingConstants.VERTICAL);
		spanColumnHeader = new JSpanGridHeader(SwingConstants.HORIZONTAL);

		scrollGrid = new JScrollPane(grid);
		setLayout(new BorderLayout());
		grid.setPreferredScrollableViewportSize(grid.getMaximumSize());
		add(scrollGrid, BorderLayout.CENTER);

		setColumnHeader(new JGridHeader(grid, SwingConstants.HORIZONTAL));
		setRowHeader(new JGridHeader(grid, SwingConstants.VERTICAL));
		setShowHeaders(true);
	}

	/**
	 * Return true if row headers are enabled
	 */
	public boolean getShowRowHeader() {
		return showRowHeader;
	}

	/**
	 * Set whether the row header should be displayed
	 */
	public void setShowRowHeader(boolean showHeader) {
		showRowHeader = showHeader;
		if (showHeader) {
			setRowHeader(rowHeader);
		} else {
			scrollGrid.setRowHeaderView(null);
		}
	}

	/**
	 * Return true if column headers are enabled
	 */
	public boolean getShowColumnHeader() {
		return showColumnHeader;
	}

	/**
	 * Set whether the column header should be displayed
	 */
	public void setShowColumnHeader(boolean showHeader) {
		showColumnHeader = showHeader;
		if (showHeader) {
			setColumnHeader(columnHeader);
		} else {
			scrollGrid.setColumnHeaderView(null);
		}
	}

	/**
	 * Returns true if both headers are enabled
	 */
	public boolean getShowHeaders() {
		return showRowHeader && showColumnHeader;
	}

	/**
	 * Set whether both headers should be displayed
	 */
	public void setShowHeaders(boolean showHeader) {
		setShowRowHeader(showHeader);
		setShowColumnHeader(showHeader);
	}

	/**
	 * Return the row header
	 */
	public JGridHeader getRowHeader() {
		return rowHeader;
	}

	/**
	 * Set the row header to display
	 */
	public void setRowHeader(JGridHeader rowHeader) {
		if (getRowHeader() != null) {
			// remove RulerModelListener
			getRowHeader().getColumnModel().removeRulerModelListener(this);
		}

		spanRowHeader.setGridHeader(rowHeader);

		scrollGrid.setRowHeaderView(null);
		this.rowHeader = rowHeader;
		if (rowHeader != null) {
			rowHeader.getColumnModel().addRulerModelListener(this);
			JViewport viewport = new JViewport();
			viewport.setView(spanRowHeader);
			viewport.setPreferredSize(spanRowHeader.getPreferredSize());
			scrollGrid.setRowHeaderView(viewport);
			repaint();
		}
	}

	public JSpanGridHeader getSpanRowHeader() {
		return spanRowHeader;
	}

	public JSpanGridHeader getSpanColumnHeader() {
		return spanColumnHeader;
	}

	/**
	 * Return the column header
	 */
	public JGridHeader getColumnHeader() {
		return columnHeader;
	}

	/**
	 * Set the column header to display
	 */
	public void setColumnHeader(JGridHeader columnHeader) {
		if (getColumnHeader() != null) {
			// remove RulerModelListener
			getColumnHeader().getRowModel().removeRulerModelListener(this);
		}

		spanColumnHeader.setGridHeader(columnHeader);

		scrollGrid.setColumnHeaderView(null);
		this.columnHeader = columnHeader;
		if (columnHeader != null) {
			columnHeader.getRowModel().addRulerModelListener(this);
			JViewport viewport = new JViewport();
			viewport.setView(spanColumnHeader);
			viewport.setPreferredSize(spanColumnHeader.getPreferredSize());
			scrollGrid.setColumnHeaderView(viewport);
			repaint();
		}
	}

	/**
	 * Update header view when the JGridHeader changes size
	 */
	public void rulerChanged(RulerModelEvent e) {
		if (e.getSource() == rowHeader.getColumnModel()
				|| e.getSource() == rowHeader.getRowModel()) {
			setRowHeader(rowHeader);
		}
		if (e.getSource() == columnHeader.getRowModel()
				|| e.getSource() == columnHeader.getColumnModel()) {
			setColumnHeader(columnHeader);
		}
	}
}
