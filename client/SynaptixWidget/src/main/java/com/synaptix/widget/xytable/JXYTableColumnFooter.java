package com.synaptix.widget.xytable;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

import com.synaptix.widget.xytable.plaf.BasicXYTableColumnFooterUI;

public class JXYTableColumnFooter extends JComponent implements XYTableModelListener {

	private static final long serialVersionUID = 7789628569244174704L;

	private static final String uiClassID = "XYTableColumnFooterUI"; //$NON-NLS-1$

	static {
		UIManager.getDefaults().put(uiClassID, BasicXYTableColumnFooterUI.class.getName());
	}

	private final JXYTable xyTable;

	private XYTableColumnFooterRenderer columnFooterRenderer;

	public JXYTableColumnFooter(JXYTable xyTable) {
		super();

		this.xyTable = xyTable;
		this.columnFooterRenderer = createColumnFooterRenderer();

		if (xyTable.getModel() != null) {
			xyTable.getModel().addXYTableModelListener(this);
		}
		xyTable.addPropertyChangeListener("model", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getOldValue() instanceof XYTableModel) {
					((XYTableModel) evt.getOldValue()).removeXYTableModelListener(JXYTableColumnFooter.this);
				}
				if (evt.getNewValue() instanceof XYTableModel) {
					((XYTableModel) evt.getNewValue()).addXYTableModelListener(JXYTableColumnFooter.this);
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

	protected XYTableColumnFooterRenderer createColumnFooterRenderer() {
		return new DefaultXYTableColumnFooterRenderer();
	}

	public XYTableColumnFooterRenderer getColumnFooterRenderer() {
		return columnFooterRenderer;
	}

	public void setColumnFooterRenderer(XYTableColumnFooterRenderer columnFooterRenderer) {
		XYTableColumnFooterRenderer oldValue = this.columnFooterRenderer;
		this.columnFooterRenderer = columnFooterRenderer;
		firePropertyChange("columnFooterRenderer", oldValue, columnFooterRenderer);
	}

	public int getColumnCount() {
		return xyTable.getColumnCount();
	}

	public int getColumnWidth() {
		return xyTable.getColumnWidth();
	}

	public int getColumnFooterHeight() {
		return xyTable.getColumnFooterHeight();
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
