package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;

import javax.swing.JPanel;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.model.HierarchicalSelectionModel.IHoverChangeCallback;
import com.synaptix.widget.hierarchical.view.swing.model.HoverCell;
import com.synaptix.widget.hierarchical.writer.CellInformation;
import com.synaptix.widget.hierarchical.writer.IExportableTable;
import com.synaptix.widget.hierarchical.writer.IHierarchicalExportWriter;

public abstract class ContentPanel<U, E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements IExportableTable {

	private static final long serialVersionUID = -9095580860828236582L;

	private static final int HORIZONTAL_OFFSET = 2;

	protected final HierarchicalPanel<U, E, F, L> parent;

	protected final ConfigurationContext<E, F, L> configurationContext;

	protected final SubstanceColorScheme colorScheme;

	private final IHoverChangeCallback hoverChangeCallback;

	protected MyMouseListener mouseListener;

	protected Point mousePosition;

	protected boolean isEnabledListeners;

	public ContentPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<U, E, F, L> parent) {
		super();

		this.parent = parent;
		this.configurationContext = configurationContext;
		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);
		this.hoverChangeCallback = new IHoverChangeCallback() {

			@Override
			public void hoverChanged(HoverCell hoverCell) {
				if ((hoverCell == null) || (hoverCell.getPanelKind() != getPanelKind())) {
					mousePosition = null;
					repaint();
				}
			}
		};

		initListeners();
	}

	protected void initListeners() {
		this.mouseListener = new MyMouseListener();
		addMouseListener(this.mouseListener);
		addMouseMotionListener(this.mouseListener);
		this.isEnabledListeners = false;
	}

	protected void computeContents(final List<L> model) {
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	public void setEnableListeners(final boolean enable) {
		this.isEnabledListeners = enable;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (CollectionHelper.isNotEmpty(this.parent.getColumnDefinitionList()) && CollectionHelper.isNotEmpty(this.parent.getModel())) {
			paintContents(g);
		} else {
			paintEmptyContents(g);
		}
	}

	protected abstract void paintContents(final Graphics g);

	protected void paintEmptyContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.dispose();
	}

	public void updateWidth(final int panelWidth) {
		final Dimension preferredSize = new Dimension(panelWidth, getPreferredSize().height);
		setPreferredSize(preferredSize);
	}

	protected Rectangle buildRectangleToPaint(final int rowIndex, final int columnIndex, final int absciss, final int nextRowOrdinate) {
		int columnWidth = this.parent.getColumnSizeAt(columnIndex);
		if (this.parent.isLastColumn(columnIndex)) {
			columnWidth -= 2;
		}
		final Rectangle rectangleToPaint = new Rectangle(absciss, nextRowOrdinate, columnWidth, this.configurationContext.getCellHeight());
		return rectangleToPaint;
	}

	protected void paintCellBackground(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		if (this.mousePosition != null && getHoverCell() != null && rectangleToPaint.contains(this.mousePosition)) {
			graphics.setColor(this.configurationContext.getHighlightedStandardColor());
		} else {
			graphics.setColor(this.configurationContext.getStandardColor());
		}
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected void paintCellBorder(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		graphics.setColor(this.colorScheme.getDarkColor());
		graphics.drawRect(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
	}

	protected String getCellLabel(final int rowIndex, final int columnIndex) {
		final U fieldForCurrentColumn = this.parent.getColumnDefinitionAt(columnIndex);
		final Serializable cellObject = this.parent.getModel().get(rowIndex).getValuesMap().get(fieldForCurrentColumn);
		final String cellLabel = this.parent.getStringFromObject(cellObject);
		return cellLabel;
	}

	protected void paintSelectedCellBackground(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		if (this.mousePosition != null && getHoverCell() != null && rectangleToPaint.contains(this.mousePosition)) {
			graphics.setColor(this.configurationContext.getHighlightedSelectionColor());
		} else {
			graphics.setColor(this.configurationContext.getSelectionColor());
		}
		graphics.fillRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected void paintSelectedCellBorder(final Graphics2D graphics, final Rectangle rectangleToPaint) {
		graphics.setColor(Color.yellow);
		graphics.drawRect(rectangleToPaint.x + 1, rectangleToPaint.y + 1, rectangleToPaint.width - 2, rectangleToPaint.height - 2);
	}

	protected void paintCellValue(final Graphics2D graphics, final Rectangle rectangleToPaint, final int rowIndex, final int columnIndex) {
		if (this.parent.getCellRenderer() != null) {
			performUserRendering(graphics, rectangleToPaint, rowIndex, columnIndex);
		} else {
			performDefaultRendering(graphics, rectangleToPaint, rowIndex, columnIndex);
		}
	}

	protected void performUserRendering(final Graphics2D graphics, final Rectangle rectangleToPaint, final int rowIndex, final int columnIndex) {
		final U column = this.parent.getColumnDefinitionAt(columnIndex);
		final L row = this.parent.getModel().get(rowIndex);
		final Graphics2D restrictedGraphics = (Graphics2D) graphics.create(rectangleToPaint.x, rectangleToPaint.y, rectangleToPaint.width, rectangleToPaint.height);
		this.parent.getCellRenderer().renderForElement(restrictedGraphics, row, column);
	}

	protected void performDefaultRendering(final Graphics2D graphics, final Rectangle rectangleToPaint, final int rowIndex, final int columnIndex) {
		final String cellLabel = getCellLabel(rowIndex, columnIndex);
		graphics.setColor(this.colorScheme.getForegroundColor());
		GraphicsHelper.paintCenterString(graphics, cellLabel, graphics.getFont(), rectangleToPaint.x + HORIZONTAL_OFFSET, rectangleToPaint.y, rectangleToPaint.width - 2 * HORIZONTAL_OFFSET,
				rectangleToPaint.height);
	}

	protected void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(ContentPanel.class.getName(), this.getHeight(), this.colorScheme.getUltraLightColor(), this.colorScheme.getLightColor()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	protected abstract void setSelectionAtPoint(final Point point);

	protected abstract void addOrRemoveSelectionAtPoint(final Point point);

	protected abstract void setSelectedValueAtPoint(final Point point);

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
		setSelectedValueAtPoint(e.getPoint());
	}

	@Override
	public int getExportHeight() {
		return parent.getModelSize();
	}

	@Override
	public int getExportWidth() {
		return parent.getColumnDefinitionList().size();
	}

	@Override
	public int getColumnWidth(int columnIndex) {
		return parent.getColumnSizeAt(columnIndex);
	}

	public int getVerticalCellSpan(int rowIndex, int columnIndex) {
		return 1;
	}

	protected abstract HierarchicalPanelKind getPanelKind();

	private void updateHover() {
		HoverCell hoverCell = null;
		if ((mousePosition != null) && (parent.isValidOrdinate(mousePosition.getY())) && (parent.isValidAbsciss(mousePosition.getX()))) {
			int rowIndex = parent.getRowIndexAtOrdinate(mousePosition.y);
			int columnIndex = parent.getColumnIndexAtAbsciss(mousePosition.x);
			if ((rowIndex >= 0) && (columnIndex >= 0)) {
				hoverCell = new HoverCell(getPanelKind(), rowIndex, columnIndex, getHoverObject(rowIndex, columnIndex));
			}
		}
		HoverCell currentHoverCell = parent.selectionModel.getHoverCell();
		if ((hoverCell != null) || ((currentHoverCell != null) && (currentHoverCell.getPanelKind() == getPanelKind()))) {
			parent.selectionModel.setHoverCell(hoverCell, hoverChangeCallback);
		}
	}

	protected Object getHoverObject(int rowIndex, int columnIndex) {
		CellInformation cellInformation = getCellInformation(rowIndex, columnIndex);
		return cellInformation != null ? cellInformation.getObject() : null;
	}

	protected final HoverCell getHoverCell() {
		return parent.selectionModel.getHoverCell();
	}

	@Override
	public IHierarchicalExportWriter.Type getForcedType(int rowIndex, int columnIndex) {
		return null;
	}

	private final class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			if (isEnabledListeners) {
				processDefaultMouseEvent(e);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (isEnabledListeners) {
				repaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isEnabledListeners) {
				mousePosition = null;
				updateHover();
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (isEnabledListeners) {
				mousePosition = e.getPoint();
				updateHover();
				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isEnabledListeners) {
				mouseMoved(e);
			}
		}
	}
}
