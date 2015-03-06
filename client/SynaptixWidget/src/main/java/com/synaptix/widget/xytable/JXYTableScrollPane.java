package com.synaptix.widget.xytable;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;

import com.synaptix.widget.xytable.plaf.BasicXYTableScrollPaneUI;

public class JXYTableScrollPane extends JScrollPane {

	private static final long serialVersionUID = -8201404665169879715L;

	private static final String uiClassID = "XYTableScrollPaneUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicXYTableScrollPaneUI.class.getName());
	}

	public static final String ROW_FOOTER = "ROW_FOOTER";

	public static final String COLUMN_FOOTER = "COLUMN_FOOTER";

	public static final String LOWER_LEFT_0_CORNER = "LOWER_LEFT_0_CORNER";

	public static final String LOWER_RIGHT_0_CORNER = "LOWER_RIGHT_0_CORNER";

	public static final String UPPER_RIGHT_0_CORNER = "UPPER_RIGHT_0_CORNER";

	protected JViewport columnFooter;

	protected JViewport rowFooter;

	protected Component lowerLeft0;

	protected Component upperRight0;

	protected Component lowerRight0;

	public JXYTableScrollPane(Component view) {
		super(view);

		this.setHorizontalScrollBarPolicy(JXYTableScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setVerticalScrollBarPolicy(JXYTableScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();

		setUI(UIManager.getUI(this));
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public JViewport getColumnFooter() {
		return columnFooter;
	}

	public void setColumnFooter(JViewport columnFooter) {
		JViewport old = getColumnHeader();
		this.columnFooter = columnFooter;
		if (columnFooter != null) {
			add(columnFooter, COLUMN_FOOTER);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("columnFooter", old, columnFooter); //$NON-NLS-1$
		revalidate();
		repaint();
	}

	public void setColumnFooterView(Component view) {
		if (getColumnFooter() == null) {
			setColumnFooter(createViewport());
		}
		getColumnFooter().setView(view);
	}

	public JViewport getRowFooter() {
		return rowFooter;
	}

	public void setRowFooter(JViewport rowFooter) {
		JViewport old = getRowHeader();
		this.rowFooter = rowFooter;
		if (rowFooter != null) {
			add(rowFooter, ROW_FOOTER);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("rowFooter", old, rowFooter); //$NON-NLS-1$
		revalidate();
		repaint();
	}

	public void setRowFooterView(Component view) {
		if (getRowFooter() == null) {
			setRowFooter(createViewport());
		}
		getRowFooter().setView(view);
	}

	@Override
	public Component getCorner(String key) {
		if (LOWER_LEFT_0_CORNER.equals(key)) {
			return lowerLeft0;
		} else if (LOWER_RIGHT_0_CORNER.equals(key)) {
			return lowerRight0;
		} else if (UPPER_RIGHT_0_CORNER.equals(key)) {
			return upperRight0;
		} else {
			return super.getCorner(key);
		}
	}

	@Override
	public void setCorner(String key, Component corner) {
		Component old = null;
		boolean add = false;
		if (LOWER_LEFT_0_CORNER.equals(key)) {
			old = lowerLeft0;
			lowerLeft0 = corner;
			add = true;
		} else if (LOWER_RIGHT_0_CORNER.equals(key)) {
			old = lowerRight0;
			lowerRight0 = corner;
			add = true;
		} else if (UPPER_RIGHT_0_CORNER.equals(key)) {
			old = upperRight0;
			upperRight0 = corner;
			add = true;
		} else {
			super.setCorner(key, corner);
		}
		if (add) {
			if (old != null) {
				remove(old);
			}
			if (corner != null) {
				add(corner, key);
			}
			firePropertyChange(key, old, corner);
			revalidate();
			repaint();
		}
	}

	@Override
	public Rectangle getViewportBorderBounds() {
		Rectangle borderR = super.getViewportBorderBounds();

		boolean leftToRight = this.getComponentOrientation().isLeftToRight();

		JViewport colFoot = getColumnFooter();
		if ((colFoot != null) && (colFoot.isVisible())) {
			int colFootHeight = colFoot.getHeight();
			borderR.y += colFootHeight;
			borderR.height -= colFootHeight;
		}

		JViewport rowFoot = getRowFooter();
		if ((rowFoot != null) && (rowFoot.isVisible())) {
			int rowFootWidth = rowFoot.getWidth();
			if (leftToRight) {
				borderR.x += rowFootWidth;
			}
			borderR.width -= rowFootWidth;
		}

		return borderR;
	}
}
