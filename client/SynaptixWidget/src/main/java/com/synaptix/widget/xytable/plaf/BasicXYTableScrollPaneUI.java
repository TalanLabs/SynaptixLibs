package com.synaptix.widget.xytable.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceScrollPaneUI;

import com.synaptix.widget.xytable.JXYTableScrollPane;

public class BasicXYTableScrollPaneUI extends SubstanceScrollPaneUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicXYTableScrollPaneUI();
	}

	private MyHandler myHandler;

	protected PropertyChangeListener myPropertyChangeListener;

	private MyHandler getMyHandler() {
		if (myHandler == null) {
			myHandler = new MyHandler();
		}
		return myHandler;
	}

	@Override
	protected void installDefaults(JScrollPane scrollpane) {
		ScrollPaneLayout oldLayout = (ScrollPaneLayout) scrollpane.getLayout();

		super.installDefaults(scrollpane);

		scrollpane.setLayout(new MyAdjustedLayout(oldLayout));
	}

	@Override
	protected void installListeners(JScrollPane c) {
		super.installListeners(c);

		myPropertyChangeListener = createMyPropertyChangeListener();

		scrollpane.addPropertyChangeListener(spPropertyChangeListener);
	}

	@Override
	protected void uninstallListeners(JComponent c) {
		super.uninstallListeners(c);
	}

	protected PropertyChangeListener createMyPropertyChangeListener() {
		return getMyHandler();
	}

	@Override
	protected void syncScrollPaneWithViewport() {
		super.syncScrollPaneWithViewport();

		JXYTableScrollPane xyTableScrollPane = (JXYTableScrollPane) scrollpane;

		JViewport viewport = scrollpane.getViewport();
		JViewport rowFoot = xyTableScrollPane.getRowFooter();
		JViewport colFoot = xyTableScrollPane.getColumnFooter();
		boolean ltr = scrollpane.getComponentOrientation().isLeftToRight();

		if (rowFoot != null) {
			Point p = rowFoot.getViewPosition();
			p.y = viewport.getViewPosition().y;
			p.x = 0;
			rowFoot.setViewPosition(p);
		}

		if (colFoot != null) {
			Point p = colFoot.getViewPosition();
			if (ltr) {
				p.x = viewport.getViewPosition().x;
			} else {
				p.x = Math.max(0, viewport.getViewPosition().x);
			}
			p.y = 0;
			colFoot.setViewPosition(p);
		}
	}

	protected void updateRowFooter(PropertyChangeEvent e) {
		JViewport newRowFoot = (JViewport) (e.getNewValue());
		if (newRowFoot != null) {
			JViewport viewport = scrollpane.getViewport();
			Point p = newRowFoot.getViewPosition();
			p.y = (viewport != null) ? viewport.getViewPosition().y : 0;
			newRowFoot.setViewPosition(p);
		}
	}

	protected void updateColumnFooter(PropertyChangeEvent e) {
		JViewport newColFoot = (JViewport) (e.getNewValue());
		if (newColFoot != null) {
			JViewport viewport = scrollpane.getViewport();
			Point p = newColFoot.getViewPosition();
			if (viewport == null) {
				p.x = 0;
			} else {
				if (scrollpane.getComponentOrientation().isLeftToRight()) {
					p.x = viewport.getViewPosition().x;
				} else {
					p.x = Math.max(0, viewport.getViewPosition().x);
				}
			}
			newColFoot.setViewPosition(p);
		}
	}

	protected static class MyAdjustedLayout extends AdjustedLayout {

		private static final long serialVersionUID = 9122850097904383512L;

		protected JViewport colFoot;

		protected JViewport rowFoot;

		protected Component lowerLeft0;

		protected Component upperRight0;

		protected Component lowerRight0;

		public MyAdjustedLayout(ScrollPaneLayout delegate) {
			super(delegate);
		}

		@Override
		public void syncWithScrollPane(JScrollPane sp) {
			super.syncWithScrollPane(sp);

			JXYTableScrollPane xytsp = (JXYTableScrollPane) sp;
			colFoot = xytsp.getColumnFooter();
			rowFoot = xytsp.getRowFooter();
			lowerLeft0 = xytsp.getCorner(JXYTableScrollPane.LOWER_LEFT_0_CORNER);
			upperRight0 = xytsp.getCorner(JXYTableScrollPane.UPPER_RIGHT_0_CORNER);
			lowerRight0 = xytsp.getCorner(JXYTableScrollPane.LOWER_RIGHT_0_CORNER);
		}

		@Override
		public void addLayoutComponent(String s, Component c) {
			if (JXYTableScrollPane.COLUMN_FOOTER.equals(s)) {
				colFoot = (JViewport) addSingletonComponent(colFoot, c);
			} else if (JXYTableScrollPane.ROW_FOOTER.equals(s)) {
				rowFoot = (JViewport) addSingletonComponent(rowFoot, c);
			} else if (JXYTableScrollPane.LOWER_LEFT_0_CORNER.equals(s)) {
				lowerLeft0 = addSingletonComponent(lowerLeft0, c);
			} else if (JXYTableScrollPane.UPPER_RIGHT_0_CORNER.equals(s)) {
				upperRight0 = addSingletonComponent(upperRight0, c);
			} else if (JXYTableScrollPane.LOWER_RIGHT_0_CORNER.equals(s)) {
				lowerRight0 = addSingletonComponent(lowerRight0, c);
			} else {
				super.addLayoutComponent(s, c);
			}
		}

		@Override
		public void removeLayoutComponent(Component c) {
			if (c == colFoot) {
				colFoot = null;
			} else if (c == rowFoot) {
				rowFoot = null;
			} else if (c == lowerLeft0) {
				lowerLeft0 = null;
			} else if (c == upperRight0) {
				upperRight0 = null;
			} else if (c == lowerRight0) {
				lowerRight0 = null;
			} else {
				super.removeLayoutComponent(c);
			}
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Dimension d = super.preferredLayoutSize(parent);
			int prefWidth = d.width;
			int prefHeight = d.height;

			if ((rowFoot != null) && rowFoot.isVisible()) {
				prefWidth += rowFoot.getPreferredSize().width;
			}

			if ((colFoot != null) && colFoot.isVisible()) {
				prefHeight += colFoot.getPreferredSize().height;
			}

			return new Dimension(prefWidth, prefHeight);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Dimension d = super.minimumLayoutSize(parent);
			int minWidth = d.width;
			int minHeight = d.height;

			if ((rowFoot != null) && rowFoot.isVisible()) {
				Dimension size = rowFoot.getMinimumSize();
				minWidth += size.width;
				minHeight = Math.max(minHeight, size.height);
			}

			if ((colFoot != null) && colFoot.isVisible()) {
				Dimension size = colFoot.getMinimumSize();
				minWidth = Math.max(minWidth, size.width);
				minHeight += size.height;
			}

			return new Dimension(minWidth, minHeight);
		}

		@Override
		public void layoutContainer(Container parent) {
			int verticalValue = getVerticalScrollBar().getValue();
			int horizontalValue = getHorizontalScrollBar().getValue();

			super.layoutContainer(parent);

			JXYTableScrollPane scrollPane = (JXYTableScrollPane) parent;

			Border viewportBorder = scrollPane.getViewportBorder();
			Insets vpbInsets;
			if (viewportBorder != null) {
				vpbInsets = viewportBorder.getBorderInsets(parent);
			} else {
				vpbInsets = new Insets(0, 0, 0, 0);
			}

			if (getViewport() != null) {
				Component lrc = getCorner(LOWER_RIGHT_CORNER);
				Component llc = getCorner(LOWER_LEFT_CORNER);
				Component urc = getCorner(UPPER_RIGHT_CORNER);

				Rectangle viewportR = getViewport().getBounds();
				Rectangle colHeadR = getColumnHeader().getBounds();
				Rectangle rowHeadR = getRowHeader().getBounds();
				Rectangle hScrollR = getHorizontalScrollBar().getBounds();
				Rectangle vScrollR = getVerticalScrollBar().getBounds();
				Rectangle lrcR = lrc != null ? lrc.getBounds() : new Rectangle();
				Rectangle llcR = llc != null ? llc.getBounds() : new Rectangle();
				Rectangle urcR = urc != null ? urc.getBounds() : new Rectangle();

				Rectangle rowFootR = new Rectangle();
				Rectangle colFootR = new Rectangle();

				if (scrollPane.getComponentOrientation().isLeftToRight()) {
					if ((rowFoot != null) && rowFoot.isVisible()) {
						Dimension d = rowFoot.getPreferredSize();
						viewportR.width = viewportR.width - d.width - 1;
						colHeadR.width = colHeadR.width - d.width;
						hScrollR.width = hScrollR.width - d.width;
						rowFootR.x = viewportR.x + viewportR.width;
						rowFootR.y = colHeadR.y + colHeadR.height;
						rowFootR.width = d.width;
					}

					if ((colFoot != null) && colFoot.isVisible()) {
						Dimension d = colFoot.getPreferredSize();
						viewportR.height = viewportR.height - d.height - 1;
						rowHeadR.height = rowHeadR.height - d.height;
						vScrollR.height = vScrollR.height - d.height;
						colFootR.x = rowHeadR.x + rowHeadR.width;
						colFootR.y = viewportR.y + viewportR.height;
						colFootR.height = d.height;
					}

					rowFootR.height = rowHeadR.height;
					colFootR.width = colHeadR.width;

					vScrollR.x = rowFootR.x + rowFootR.width;
					hScrollR.y = colFootR.y + colFootR.height;

					lrcR.x = vScrollR.x;
					lrcR.y = hScrollR.y;

					llcR.y = hScrollR.y;
					urcR.x = vScrollR.x;
				} else {

				}

				getViewport().setBounds(viewportR);
				getColumnHeader().setBounds(colHeadR);
				getRowHeader().setBounds(rowHeadR);
				getHorizontalScrollBar().setBounds(hScrollR);
				getVerticalScrollBar().setBounds(vScrollR);
				if (lrc != null) {
					lrc.setBounds(lrcR);
				}
				if (llc != null) {
					llc.setBounds(llcR);
				}
				if (urc != null) {
					urc.setBounds(urcR);
				}
				rowFoot.setBounds(rowFootR);
				colFoot.setBounds(colFootR);
				lowerLeft0.setBounds(rowHeadR.x, colFootR.y, rowHeadR.width, colFootR.height);
				upperRight0.setBounds(rowFootR.x, colHeadR.y, rowFootR.width, colHeadR.height);
				lowerRight0.setBounds(rowFootR.x, colFootR.y, rowFootR.width, colFootR.height);

				getHorizontalScrollBar().setValue(horizontalValue);
				getVerticalScrollBar().setValue(verticalValue);
			}
		}
	}

	class MyHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			if (e.getSource() == scrollpane) {
				scrollPanePropertyChange(e);
			}
		}

		private void scrollPanePropertyChange(PropertyChangeEvent e) {
			String propertyName = e.getPropertyName();

			if (propertyName == "rowFooter") {
				updateRowFooter(e);
			} else if (propertyName == "columnFooter") {
				updateColumnFooter(e);
			}
		}
	}
}
