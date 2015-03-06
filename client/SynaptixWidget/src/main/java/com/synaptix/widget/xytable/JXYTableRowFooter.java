package com.synaptix.widget.xytable;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.xytable.plaf.BasicXYTableRowFooterUI;

public class JXYTableRowFooter extends JComponent implements XYTableModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "XYTableRowFooterUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicXYTableRowFooterUI.class.getName());
	}

	private final JXYTable xyTable;

	private XYTableRowFooterRenderer rowFooterRenderer;

	public JXYTableRowFooter(JXYTable xyTable) {
		super();

		this.xyTable = xyTable;
		this.rowFooterRenderer = createRowFooterRenderer();

		if (xyTable.getModel() != null) {
			xyTable.getModel().addXYTableModelListener(this);
		}
		xyTable.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof XYTableModel) {
					((XYTableModel) evt.getOldValue()).removeXYTableModelListener(JXYTableRowFooter.this);
				}
				if (evt.getNewValue() instanceof XYTableModel) {
					((XYTableModel) evt.getNewValue()).addXYTableModelListener(JXYTableRowFooter.this);
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

	protected XYTableRowFooterRenderer createRowFooterRenderer() {
		return new DefaultXYTableRowFooterRenderer();
	}

	public XYTableRowFooterRenderer getRowFooterRenderer() {
		return rowFooterRenderer;
	}

	public void setRowFooterRenderer(XYTableRowFooterRenderer rowFooterRenderer) {
		XYTableRowFooterRenderer oldValue = this.rowFooterRenderer;
		this.rowFooterRenderer = rowFooterRenderer;
		firePropertyChange("rowFooterRenderer", oldValue, rowFooterRenderer);
	}

	public int getRowCount() {
		return xyTable.getRowCount();
	}

	public int getRowHeight() {
		return xyTable.getRowHeight();
	}

	public int getRowFooterWidth() {
		return xyTable.getRowFooterWidth();
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
