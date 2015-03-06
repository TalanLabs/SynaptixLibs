package com.synaptix.widget.xytable;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.xytable.plaf.BasicXYTableRowHeaderUI;

public class JXYTableRowHeader extends JComponent implements XYTableModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "XYTableRowHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicXYTableRowHeaderUI.class.getName());
	}

	private final JXYTable xyTable;

	private XYTableRowHeaderRenderer rowHeaderRenderer;

	public JXYTableRowHeader(JXYTable xyTable) {
		super();

		this.xyTable = xyTable;
		this.rowHeaderRenderer = createRowHeaderRenderer();

		if (xyTable.getModel() != null) {
			xyTable.getModel().addXYTableModelListener(this);
		}
		xyTable.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof XYTableModel) {
					((XYTableModel) evt.getOldValue()).removeXYTableModelListener(JXYTableRowHeader.this);
				}
				if (evt.getNewValue() instanceof XYTableModel) {
					((XYTableModel) evt.getNewValue()).addXYTableModelListener(JXYTableRowHeader.this);
				}
			}
		});

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

	public JXYTable getXYTable() {
		return xyTable;
	}

	protected XYTableRowHeaderRenderer createRowHeaderRenderer() {
		return new DefaultXYTableRowHeaderRenderer();
	}

	public XYTableRowHeaderRenderer getRowHeaderRenderer() {
		return rowHeaderRenderer;
	}

	public void setRowHeaderRenderer(XYTableRowHeaderRenderer rowHeaderRenderer) {
		XYTableRowHeaderRenderer oldValue = this.rowHeaderRenderer;
		this.rowHeaderRenderer = rowHeaderRenderer;
		firePropertyChange("rowHeaderRenderer", oldValue, rowHeaderRenderer);
	}

	public int getRowCount() {
		return xyTable.getRowCount();
	}

	public int getRowHeight() {
		return xyTable.getRowHeight();
	}

	public int getRowHeaderWidth() {
		return xyTable.getRowHeaderWidth();
	}

	public int rowAtPoint(Point point) {
		int columnHeaderHeight = xyTable.isShowColumnHeaderInner() && xyTable.getXYTableColumnHeader() != null ? xyTable.getColumnHeaderHeight() : 0;
		return xyTable.rowAtPoint(new Point(point.x, point.y + columnHeaderHeight));
	}

	protected void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	@Override
	public void xyTableChanged(XYTableModelEvent e) {
		resizeAndRepaint();
	}
}
