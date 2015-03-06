package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JPanel;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.writer.IExportableTable;

public abstract class HeaderPanel<U, E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements IExportableTable {

	private static final long serialVersionUID = -5484292295215447014L;

	protected final HierarchicalPanel<U, E, F, L> parent;

	protected final ConfigurationContext<E, F, L> configurationContext;

	protected boolean isHighlightable;

	protected Point mousePosition;

	public HeaderPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<U, E, F, L> parent) {
		super();
		this.parent = parent;
		this.configurationContext = configurationContext;
		addMouseListener(new MyMouseListener());
		addMouseMotionListener(new MyMouseListener());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (CollectionHelper.isNotEmpty(this.parent.getColumnDefinitionList())) {
			paintContents(g);
		} else {
			paintEmptyContents(g);
		}
	}

	protected Rectangle buildRectangleToPaint(final int columnIndex, final int absciss, final int ordinate) {
		int columnWidth = this.parent.getColumnSizeAt(columnIndex);
		if (this.parent.isLastColumn(columnIndex)) {
			columnWidth -= 2;
		}
		final Rectangle rectangleToPaint = new Rectangle(absciss, ordinate, columnWidth, this.getHeight());
		return rectangleToPaint;
	}

	protected void paintCellBackground(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		if (this.isHighlightable && this.mousePosition != null && rectangleToPaint.contains(this.mousePosition)) {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_highlighted", this.getHeight(), this.configurationContext.getHighlightedStandardColor(),
					this.configurationContext.getHighlightedStandardColor().darker()));
		} else {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_normal", this.getHeight(), this.configurationContext.getStandardColor(),
					this.configurationContext.getStandardColor().darker()));
		}
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected void paintSelectedCellBackground(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		if (this.isHighlightable && this.mousePosition != null && rectangleToPaint.contains(this.mousePosition)) {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_highlighted_selected", this.getHeight(),
					this.configurationContext.getHighlightedSelectionColor(), this.configurationContext.getHighlightedSelectionColor().darker()));
		} else {
			graphics.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_selected", this.getHeight(), this.configurationContext.getSelectionColor(),
					this.configurationContext.getSelectionColor().darker()));
		}
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected void paintSelectedCellBorder(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		graphics.setColor(Color.yellow);
		graphics.drawRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected abstract void paintContents(final Graphics g);

	protected abstract void paintEmptyContents(final Graphics g);

	protected abstract void setSelectionAtPoint(final Point point);

	protected abstract void addOrRemoveSelectionAtPoint(final Point point);

	protected abstract void selectAtPointIfNotSelected(final Point point);

	private void processDefaultMouseEvent(final MouseEvent e) {
		final Point mousePosition = e.getPoint();
		if (parent.isValidAbsciss(mousePosition.getX()) && parent.isValidOrdinate(mousePosition.getY())) {
			performSelection(e);
		} else {
			parent.unselectAll();
		}
	}

	private void performSelection(final MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.isControlDown()) {
				addOrRemoveSelectionAtPoint(e.getPoint());
			} else {
				setSelectionAtPoint(e.getPoint());
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			selectAtPointIfNotSelected(e.getPoint());
		}
	}

	public void updateWidth(final int panelWidth) {
		final Dimension preferredSize = new Dimension(panelWidth, this.configurationContext.getHeaderHeight());
		setPreferredSize(preferredSize);
	}

	@Override
	public int getExportHeight() {
		return 1;
	}

	@Override
	public int getExportWidth() {
		return parent.getColumnDefinitionList().size();
	}

	@Override
	public int getColumnWidth(int columnIndex) {
		return parent.getColumnSizeAt(columnIndex);
	}

	private final class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if ((getCursor().equals(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR))) && (e.getClickCount() % 2 != 0)) {
				processDefaultMouseEvent(e);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			isHighlightable = true;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			isHighlightable = false;
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mousePosition = e.getPoint();
			if (isHighlightable) {
				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
	}
}
