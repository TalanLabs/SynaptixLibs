package com.synaptix.widget.hierarchical.view.swing.component.panel.implementation;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.GraphicsHelper;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HeaderPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.writer.CellInformation;
import com.synaptix.widget.util.StaticWidgetHelper;

public class TitleHeaderPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends HeaderPanel<IField, E, F, L> {

	private static final long serialVersionUID = 8275301373809728413L;

	private Border border = BorderFactory.createEtchedBorder();

	private final SubstanceColorScheme colorScheme;

	private int[] columnBorders;

	private Action defaultColumnsAction;

	private Action showColumnControlDialogAction;

	public TitleHeaderPanel(final ConfigurationContext<E, F, L> configurationContext, final HierarchicalPanel<IField, E, F, L> parent) {
		super(configurationContext, parent);

		this.colorScheme = SubstanceColorSchemeUtilities.getColorScheme(this, ColorSchemeAssociationKind.FILL, ComponentState.ENABLED);

		buildActions();

		install();
	}

	private void buildActions() {
		defaultColumnsAction = new AbstractAction(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().defaultColumns()) {

			private static final long serialVersionUID = 1350921272837147235L;

			@Override
			public void actionPerformed(ActionEvent e) {
				for (IField field : parent.getColumnDefinitionList()) {
					SyPreferences.getPreferences().remove(getColumnPrefKey(field));
				}
				parent.updateColumnSize();
			}
		};
		showColumnControlDialogAction = new AbstractAction(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().columnDisplay()) {

			private static final long serialVersionUID = 4823119406027390380L;

			@Override
			public void actionPerformed(ActionEvent e) {
				configurationContext.getHierarchicalContext().showColumnControlDialog();
			}
		};
	}

	private void install() {
		MouseAdapter mouseListener = new MouseAdapter() {

			private boolean resizeCursor = false;

			private IField selectedField = null;

			private int selectedSize = -1;

			private int selectedPosition = -1;

			@Override
			public void mouseMoved(MouseEvent e) {
				int border = getBorder(e);
				if (selectedField == null) {
					if (border >= 0) {
						setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
						resizeCursor = true;
					} else {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						resizeCursor = false;
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (selectedField != null) {
					int newSize = e.getX() - selectedPosition + selectedSize;
					if (newSize > 10) {
						SyPreferences.getPreferences().putInt(getColumnPrefKey(selectedField), newSize);
						parent.updateColumnSize();
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selectedField = null;
				selectedSize = -1;
				selectedPosition = -1;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				int border = getBorder(e);
				if (border >= 0) {
					if (e.getClickCount() == 2) {
						SyPreferences.getPreferences().remove(getColumnPrefKey(parent.getColumnDefinitionAt(border)));
						parent.updateColumnSize();
						mouseMoved(e);
					} else {
						selectedField = parent.getColumnDefinitionAt(border);
						selectedSize = getColumnWidth(border);
						selectedPosition = e.getX();
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if ((resizeCursor) && (selectedField == null)) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					resizeCursor = false;
				}
			}
		};
		addMouseMotionListener(mouseListener);
		addMouseListener(mouseListener);
	}

	/**
	 * Returns the column index which owns the border, (10px wide, 5px each side)
	 * 
	 * @param e
	 * @return
	 */
	public final int getBorder(MouseEvent e) {
		int border = -1;
		if (columnBorders != null) {
			int i = 0;
			int max = columnBorders.length;
			while ((i < max) && (border < 0)) {
				int xc = columnBorders[i];
				if ((xc + 5 > e.getX()) && (xc - 5 < e.getX())) {
					border = i;
				}
				i++;
			}
		}
		return border;
	}

	private final void updateColumnBorders() {
		int max = parent.getColumnDefinitionList().size();
		columnBorders = new int[max];
		int offset = 0;
		for (int i = 0; i < max; i++) {
			offset += getColumnWidth(i);
			columnBorders[i] = offset;
		}
	}

	@Override
	public void updateWidth(int panelWidth) {
		super.updateWidth(panelWidth);

		updateColumnBorders();
	}

	public int computeColumnWidth(int columnIndex) {
		IField column = parent.getColumnDefinitionAt(columnIndex);
		int size = SyPreferences.getPreferences().getInt(getColumnPrefKey(column), 0);
		return size;
	}

	protected String getColumnPrefKey(IField field) {
		return configurationContext.getTitle() + "_columnSize_" + field.name();
	}

	@Override
	protected void paintContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		g2.setFont(super.parent.getColumnTitleFont());

		int nextColumnAbsciss = 0;

		for (int i = 0; i < super.parent.getColumnDefinitionList().size(); i++) {
			nextColumnAbsciss = paintColumnAndReturnEndAbsciss(i, g2, nextColumnAbsciss);
		}
		g2.dispose();
	}

	protected void paintBackground(final Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(GraphicsHelper.buildVerticalGradientPaint(HeaderPanel.class.getName() + "_normal", this.getHeight(), this.configurationContext.getStandardColor(), this.configurationContext
				.getStandardColor().darker()));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	protected int paintColumnAndReturnEndAbsciss(final int columnIndex, final Graphics2D graphics, final int absciss) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		final int columnWidth = super.parent.getColumnSizeAt(columnIndex);

		final Rectangle rectangleToPaint = buildRectangleToPaint(columnIndex, absciss, 0);
		if (super.parent.isWholeColumnSelected(columnIndex)) {
			paintSelectedCellBorder(graphics, rectangleToPaint);
			paintSelectedCellBackground(graphics, rectangleToPaint);
		} else {
			paintCellBackground(graphics, rectangleToPaint);
			this.border.paintBorder(this, graphics, absciss, 0, columnWidth, this.getHeight());
		}

		graphics.setColor(this.colorScheme.getForegroundColor());
		GraphicsHelper.paintCenterString(graphics, columnLabel, graphics.getFont(), absciss, 0, columnWidth, this.getHeight());
		return absciss + columnWidth;
	}

	@Override
	protected void paintEmptyContents(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g.create();
		paintBackground(g2);
		this.border.paintBorder(this, g2, 0, 0, this.getWidth(), this.getHeight());
		g2.dispose();
	}

	@Override
	protected void setSelectionAtPoint(final Point point) {
		super.parent.selectAll();
	}

	@Override
	protected void selectAtPointIfNotSelected(Point point) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(defaultColumnsAction);
		if ((configurationContext.hasColumnControl()) && (configurationContext.getColumnControl().canHideColumns())) {
			menu.add(showColumnControlDialogAction);
			// } else if (!super.parent.isAllModelSelected()) {
			// setSelectionAtPoint(point);
		}
		menu.show(this, point.x, point.y);
	}

	@Override
	protected void addOrRemoveSelectionAtPoint(final Point point) {
		if (super.parent.isAllModelSelected()) {
			super.parent.unselectAll();
		} else {
			super.parent.selectAll();
		}
	}

	@Override
	public CellInformation getCellInformation(int rowIndex, int columnIndex) {
		final String columnLabel = super.parent.getColumnLabelAt(columnIndex);
		return new CellInformation(columnLabel);
	}
}
