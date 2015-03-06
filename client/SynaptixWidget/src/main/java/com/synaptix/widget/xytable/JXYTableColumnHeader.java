package com.synaptix.widget.xytable;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.xytable.plaf.BasicXYTableColumnHeaderUI;

public class JXYTableColumnHeader extends JComponent implements XYTableModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "XYTableColumnHeaderUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicXYTableColumnHeaderUI.class.getName());
	}

	private final JXYTable xyTable;

	private XYTableColumnHeaderRenderer columnHeaderRenderer;

	public JXYTableColumnHeader(JXYTable xyTable) {
		super();

		this.xyTable = xyTable;
		this.columnHeaderRenderer = createColumnHeaderRenderer();

		if (xyTable.getModel() != null) {
			xyTable.getModel().addXYTableModelListener(this);
		}
		xyTable.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof XYTableModel) {
					((XYTableModel) evt.getOldValue()).removeXYTableModelListener(JXYTableColumnHeader.this);
				}
				if (evt.getNewValue() instanceof XYTableModel) {
					((XYTableModel) evt.getNewValue()).addXYTableModelListener(JXYTableColumnHeader.this);
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

	protected XYTableColumnHeaderRenderer createColumnHeaderRenderer() {
		return new DefaultXYTableColumnHeaderRenderer();
	}

	public XYTableColumnHeaderRenderer getColumnHeaderRenderer() {
		return columnHeaderRenderer;
	}

	public void setColumnHeaderRenderer(XYTableColumnHeaderRenderer columnHeaderRenderer) {
		XYTableColumnHeaderRenderer oldValue = this.columnHeaderRenderer;
		this.columnHeaderRenderer = columnHeaderRenderer;
		firePropertyChange("columnHeaderRenderer", oldValue, columnHeaderRenderer);
	}

	public int getColumnCount() {
		return xyTable.getColumnCount();
	}

	public int getColumnWidth() {
		return xyTable.getColumnWidth();
	}

	public int getColumnHeaderHeight() {
		return xyTable.getColumnHeaderHeight();
	}

	public int columnAtPoint(Point point) {
		int rowHeaderWidth = xyTable.isShowRowHeaderInner() && xyTable.getXYTableRowHeader() != null ? xyTable.getRowHeaderWidth() : 0;
		return xyTable.columnAtPoint(new Point(point.x + rowHeaderWidth, point.y));
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
