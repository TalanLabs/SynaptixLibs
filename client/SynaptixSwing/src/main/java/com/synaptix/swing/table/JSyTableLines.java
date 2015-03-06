package com.synaptix.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.plaf.SyTableLinesUI;

public class JSyTableLines extends JComponent {

	private static final long serialVersionUID = 2819542439049557159L;

	private static final String uiClassID = "SyTableLinesUI"; //$NON-NLS-1$

	protected JSyTable table;

	transient protected int resizingRow;

	protected boolean rowResinzingAllowed;

	protected TableCellRenderer defaultRenderer;

	static {
		UIManager.getDefaults().put("SyTableLinesUI", //$NON-NLS-1$
				"com.synaptix.swing.plaf.basic.BasicSyTableLinesUI"); //$NON-NLS-1$
	}

	public JSyTableLines() {
		super();

		this.setFocusable(false);

		initializeLocalVars();

		updateUI();
	}

	protected void initializeLocalVars() {
		setOpaque(true);
		table = null;
		resizingRow = -1;
		rowResinzingAllowed = true;

		setDefaultRenderer(createDefaultRenderer());
	}

	public SyTableLinesUI getUI() {
		return (SyTableLinesUI) ui;
	}

	public void setUI(SyTableLinesUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((SyTableLinesUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public JSyTable getTable() {
		return table;
	}

	public void setTable(JSyTable table) {
		JSyTable old = this.table;
		this.table = table;

		firePropertyChange("table", old, table); //$NON-NLS-1$
	}

	public int getResizingRow() {
		return resizingRow;
	}

	public void setResizingRow(int resizingRow) {
		int old = this.resizingRow;
		this.resizingRow = resizingRow;
		firePropertyChange("resizingRow", old, resizingRow); //$NON-NLS-1$
	}

	public boolean isRowResinzingAllowed() {
		return rowResinzingAllowed;
	}

	public void setRowResinzingAllowed(boolean rowResinzingAllowed) {
		boolean old = this.rowResinzingAllowed;
		this.rowResinzingAllowed = rowResinzingAllowed;
		firePropertyChange("rowResinzingAllowed", old, resizingRow); //$NON-NLS-1$
	}

	public TableCellRenderer getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(TableCellRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	protected TableCellRenderer createDefaultRenderer() {
		return new UIResourceTableCellRenderer();
	}

	private static class UIResourceTableCellRenderer extends JLabel implements
			TableCellRenderer, UIResource {

		private static final long serialVersionUID = 2978679154357427403L;

		private Color foreground;

		private Color background;

		private boolean selected;
		
		public UIResourceTableCellRenderer() {
			super();
			this.setOpaque(false);
			this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.setHorizontalAlignment(JLabel.CENTER);

			foreground = this.getForeground();
			background = this.getBackground();
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if (table != null) {
				setSelected(isSelected);
				if (hasFocus || isSelected) {
					setForeground(table.getSelectionForeground());
					setBackground(table.getSelectionBackground());
				} else {
					setForeground(foreground);
					setBackground(background);
				}
			}
			this.setText((value == null) ? "" : value.toString()); //$NON-NLS-1$

			return this;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			
			int w = this.getWidth();
			int h = this.getHeight();
			
			Color c1 = this.getBackground();
			Color c2 = c1.brighter().brighter();
			
			GradientPaint gp = new GradientPaint(0,0,c2,w,0,c1);
			g2.setPaint(gp);
			g2.fillRect(0, 0, w,h);
			
			super.paintComponent(g);
		}
	}
}
