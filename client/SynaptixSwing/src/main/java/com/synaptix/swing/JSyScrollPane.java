package com.synaptix.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRole;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;

import com.synaptix.swing.plaf.SyScrollPaneUI;

public class JSyScrollPane extends JScrollPane implements
		SyScrollPaneConstants, Accessible {

	private static final long serialVersionUID = 2198124649850346597L;

	private static final String uiClassID = "SyScrollPaneUI"; //$NON-NLS-1$

	private Border viewportBorder;

	protected int verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;

	protected int horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;

	protected JViewport viewport;

	protected JScrollBar verticalScrollBar;

	protected JScrollBar horizontalScrollBar;

	protected JViewport rowHeader;

	protected JViewport columnHeader;

	protected JViewport columnFooter;

	protected Component lowerLeft;

	protected Component lowerRight;

	protected Component upperLeft;

	protected Component upperRight;

	protected Component footerLeft;

	protected Component footerRight;

	protected Component northCenter;

	protected Component northLeft;

	protected Component northRight;

	private boolean wheelScrollState = true;

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicSyScrollPaneUI"); //$NON-NLS-1$
	}

	public JSyScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		setLayout(new SyScrollPaneLayout());
		setVerticalScrollBarPolicy(vsbPolicy);
		setHorizontalScrollBarPolicy(hsbPolicy);
		setViewport(createViewport());
		setVerticalScrollBar(createVerticalScrollBar());
		setHorizontalScrollBar(createHorizontalScrollBar());
		if (view != null) {
			setViewportView(view);
		}
		setOpaque(true);
		updateUI();

		if (!this.getComponentOrientation().isLeftToRight()) {
			viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
		}
	}

	public JSyScrollPane(Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public JSyScrollPane(int vsbPolicy, int hsbPolicy) {
		this(null, vsbPolicy, hsbPolicy);
	}

	public JSyScrollPane() {
		this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public SyScrollPaneUI getUI() {
		return (SyScrollPaneUI) ui;
	}

	public void setUI(SyScrollPaneUI ui) {
		super.setUI(ui);
	}

	public void updateUI() {
		setUI((SyScrollPaneUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setLayout(LayoutManager layout) {
		if (layout instanceof SyScrollPaneLayout) {
			super.setLayout(layout);
			((SyScrollPaneLayout) layout).syncWithScrollPane(this);
		} else if (layout == null) {
			super.setLayout(layout);
		} else {
		}
	}

	public boolean isValidateRoot() {
		return true;
	}

	public int getVerticalScrollBarPolicy() {
		return verticalScrollBarPolicy;
	}

	public void setVerticalScrollBarPolicy(int policy) {
		switch (policy) {
		case VERTICAL_SCROLLBAR_AS_NEEDED:
		case VERTICAL_SCROLLBAR_NEVER:
		case VERTICAL_SCROLLBAR_ALWAYS:
			break;
		default:
			throw new IllegalArgumentException(
					"invalid verticalScrollBarPolicy"); //$NON-NLS-1$
		}
		int old = verticalScrollBarPolicy;
		verticalScrollBarPolicy = policy;
		firePropertyChange("verticalScrollBarPolicy", old, policy); //$NON-NLS-1$
		revalidate();
		repaint();
	}

	public int getHorizontalScrollBarPolicy() {
		return horizontalScrollBarPolicy;
	}

	public void setHorizontalScrollBarPolicy(int policy) {
		switch (policy) {
		case HORIZONTAL_SCROLLBAR_AS_NEEDED:
		case HORIZONTAL_SCROLLBAR_NEVER:
		case HORIZONTAL_SCROLLBAR_ALWAYS:
			break;
		default:
			throw new IllegalArgumentException(
					"invalid horizontalScrollBarPolicy"); //$NON-NLS-1$
		}
		int old = horizontalScrollBarPolicy;
		horizontalScrollBarPolicy = policy;
		firePropertyChange("horizontalScrollBarPolicy", old, policy); //$NON-NLS-1$
		revalidate();
		repaint();
	}

	public Border getViewportBorder() {
		return viewportBorder;
	}

	public void setViewportBorder(Border viewportBorder) {
		Border oldValue = this.viewportBorder;
		this.viewportBorder = viewportBorder;
		firePropertyChange("viewportBorder", oldValue, viewportBorder); //$NON-NLS-1$
	}

	public Rectangle getViewportBorderBounds() {
		Rectangle borderR = new Rectangle(getSize());

		Insets insets = getInsets();
		borderR.x = insets.left;
		borderR.y = insets.top;
		borderR.width -= insets.left + insets.right;
		borderR.height -= insets.top + insets.bottom;

		boolean leftToRight = this.getComponentOrientation().isLeftToRight();

		/*
		 * If there's a visible column header remove the space it needs from the
		 * top of borderR.
		 */

		JViewport colHead = getColumnHeader();
		if ((colHead != null) && (colHead.isVisible())) {
			int colHeadHeight = colHead.getHeight();
			borderR.y += colHeadHeight;
			borderR.height -= colHeadHeight;
		}

		JViewport colFoot = getColumnFooter();
		if ((colFoot != null) && (colFoot.isVisible())) {
			int colFootHeight = colFoot.getHeight();
			borderR.y += colFootHeight;
			borderR.height -= colFootHeight;
		}

		/*
		 * If there's a visible row header remove the space it needs from the
		 * left of borderR.
		 */

		JViewport rowHead = getRowHeader();
		if ((rowHead != null) && (rowHead.isVisible())) {
			int rowHeadWidth = rowHead.getWidth();
			if (leftToRight) {
				borderR.x += rowHeadWidth;
			}
			borderR.width -= rowHeadWidth;
		}

		/*
		 * If there's a visible vertical scrollbar remove the space it needs
		 * from the width of borderR.
		 */
		JScrollBar vsb = getVerticalScrollBar();
		if ((vsb != null) && (vsb.isVisible())) {
			int vsbWidth = vsb.getWidth();
			if (!leftToRight) {
				borderR.x += vsbWidth;
			}
			borderR.width -= vsbWidth;
		}

		/*
		 * If there's a visible horizontal scrollbar remove the space it needs
		 * from the height of borderR.
		 */
		JScrollBar hsb = getHorizontalScrollBar();
		if ((hsb != null) && (hsb.isVisible())) {
			borderR.height -= hsb.getHeight();
		}

		return borderR;
	}

	protected class ScrollBar extends JScrollBar implements UIResource {

		private static final long serialVersionUID = -1775814405417634891L;

		private boolean unitIncrementSet;

		private boolean blockIncrementSet;

		public ScrollBar(int orientation) {
			super(orientation);
		}

		public void setUnitIncrement(int unitIncrement) {
			unitIncrementSet = true;
			super.setUnitIncrement(unitIncrement);
		}

		public int getUnitIncrement(int direction) {
			JViewport vp = getViewport();
			if (!unitIncrementSet && (vp != null)
					&& (vp.getView() instanceof Scrollable)) {
				Scrollable view = (Scrollable) (vp.getView());
				Rectangle vr = vp.getViewRect();
				return view.getScrollableUnitIncrement(vr, getOrientation(),
						direction);
			} else {
				return super.getUnitIncrement(direction);
			}
		}

		public void setBlockIncrement(int blockIncrement) {
			blockIncrementSet = true;
			super.setBlockIncrement(blockIncrement);
		}

		public int getBlockIncrement(int direction) {
			JViewport vp = getViewport();
			if (blockIncrementSet || vp == null) {
				return super.getBlockIncrement(direction);
			} else if (vp.getView() instanceof Scrollable) {
				Scrollable view = (Scrollable) (vp.getView());
				Rectangle vr = vp.getViewRect();
				return view.getScrollableBlockIncrement(vr, getOrientation(),
						direction);
			} else if (getOrientation() == VERTICAL) {
				return vp.getExtentSize().height;
			} else {
				return vp.getExtentSize().width;
			}
		}

	}

	public JScrollBar createHorizontalScrollBar() {
		return new ScrollBar(JScrollBar.HORIZONTAL);
	}

	public JScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
	}

	public void setHorizontalScrollBar(JScrollBar horizontalScrollBar) {
		JScrollBar old = getHorizontalScrollBar();
		this.horizontalScrollBar = horizontalScrollBar;
		if (horizontalScrollBar != null) {
			add(horizontalScrollBar, HORIZONTAL_SCROLLBAR);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("horizontalScrollBar", old, horizontalScrollBar); //$NON-NLS-1$

		revalidate();
		repaint();
	}

	public JScrollBar createVerticalScrollBar() {
		return new ScrollBar(JScrollBar.VERTICAL);
	}

	public JScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	public void setVerticalScrollBar(JScrollBar verticalScrollBar) {
		JScrollBar old = getVerticalScrollBar();
		this.verticalScrollBar = verticalScrollBar;
		add(verticalScrollBar, VERTICAL_SCROLLBAR);
		firePropertyChange("verticalScrollBar", old, verticalScrollBar); //$NON-NLS-1$

		revalidate();
		repaint();
	}

	protected JViewport createViewport() {
		return new JViewport();
	}

	public JViewport getViewport() {
		return viewport;
	}

	public void setViewport(JViewport viewport) {
		JViewport old = getViewport();
		this.viewport = viewport;
		if (viewport != null) {
			add(viewport, VIEWPORT);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("viewport", old, viewport); //$NON-NLS-1$

		if (accessibleContext != null) {
			((AccessibleJYScrollPane) accessibleContext).resetViewPort();
		}

		revalidate();
		repaint();
	}

	public void setViewportView(Component view) {
		if (getViewport() == null) {
			setViewport(createViewport());
		}
		getViewport().setView(view);
	}

	public JViewport getRowHeader() {
		return rowHeader;
	}

	public void setRowHeader(JViewport rowHeader) {
		JViewport old = getRowHeader();
		this.rowHeader = rowHeader;
		if (rowHeader != null) {
			add(rowHeader, ROW_HEADER);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("rowHeader", old, rowHeader); //$NON-NLS-1$
		revalidate();
		repaint();
	}

	public void setRowHeaderView(Component view) {
		if (getRowHeader() == null) {
			setRowHeader(createViewport());
		}
		getRowHeader().setView(view);
	}

	public JViewport getColumnHeader() {
		return columnHeader;
	}

	public void setColumnHeader(JViewport columnHeader) {
		JViewport old = getColumnHeader();
		this.columnHeader = columnHeader;
		if (columnHeader != null) {
			add(columnHeader, COLUMN_HEADER);
		} else if (old != null) {
			remove(old);
		}
		firePropertyChange("columnHeader", old, columnHeader); //$NON-NLS-1$

		revalidate();
		repaint();
	}

	public void setColumnHeaderView(Component view) {
		if (getColumnHeader() == null) {
			setColumnHeader(createViewport());
		}
		getColumnHeader().setView(view);
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

	public Component getCorner(String key) {
		boolean isLeftToRight = getComponentOrientation().isLeftToRight();
		if (key.equals(LOWER_LEADING_CORNER)) {
			key = isLeftToRight ? LOWER_LEFT_CORNER : LOWER_RIGHT_CORNER;
		} else if (key.equals(LOWER_TRAILING_CORNER)) {
			key = isLeftToRight ? LOWER_RIGHT_CORNER : LOWER_LEFT_CORNER;
		} else if (key.equals(UPPER_LEADING_CORNER)) {
			key = isLeftToRight ? UPPER_LEFT_CORNER : UPPER_RIGHT_CORNER;
		} else if (key.equals(UPPER_TRAILING_CORNER)) {
			key = isLeftToRight ? UPPER_RIGHT_CORNER : UPPER_LEFT_CORNER;
		} else if (key.equals(FOOTER_LEADING_CORNER)) {
			key = isLeftToRight ? FOOTER_LEFT_CORNER : FOOTER_RIGHT_CORNER;
		} else if (key.equals(FOOTER_TRAILING_CORNER)) {
			key = isLeftToRight ? FOOTER_RIGHT_CORNER : FOOTER_LEFT_CORNER;
		} else if (key.equals(NORTH_LEADING_CORNER)) {
			key = isLeftToRight ? NORTH_LEFT_CORNER : NORTH_RIGHT_CORNER;
		} else if (key.equals(NORTH_TRAILING_CORNER)) {
			key = isLeftToRight ? NORTH_RIGHT_CORNER : NORTH_LEFT_CORNER;
		}
		if (key.equals(LOWER_LEFT_CORNER)) {
			return lowerLeft;
		} else if (key.equals(LOWER_RIGHT_CORNER)) {
			return lowerRight;
		} else if (key.equals(UPPER_LEFT_CORNER)) {
			return upperLeft;
		} else if (key.equals(UPPER_RIGHT_CORNER)) {
			return upperRight;
		} else if (key.equals(FOOTER_LEFT_CORNER)) {
			return footerLeft;
		} else if (key.equals(FOOTER_RIGHT_CORNER)) {
			return footerRight;
		} else if (key.equals(NORTH_CENTER)) {
			return northCenter;
		} else if (key.equals(NORTH_LEFT_CORNER)) {
			return northLeft;
		} else if (key.equals(NORTH_RIGHT_CORNER)) {
			return northRight;
		} else {
			return null;
		}
	}

	public void setCorner(String key, Component corner) {
		Component old;
		boolean isLeftToRight = getComponentOrientation().isLeftToRight();
		if (key.equals(LOWER_LEADING_CORNER)) {
			key = isLeftToRight ? LOWER_LEFT_CORNER : LOWER_RIGHT_CORNER;
		} else if (key.equals(LOWER_TRAILING_CORNER)) {
			key = isLeftToRight ? LOWER_RIGHT_CORNER : LOWER_LEFT_CORNER;
		} else if (key.equals(UPPER_LEADING_CORNER)) {
			key = isLeftToRight ? UPPER_LEFT_CORNER : UPPER_RIGHT_CORNER;
		} else if (key.equals(UPPER_TRAILING_CORNER)) {
			key = isLeftToRight ? UPPER_RIGHT_CORNER : UPPER_LEFT_CORNER;
		} else if (key.equals(FOOTER_LEADING_CORNER)) {
			key = isLeftToRight ? FOOTER_LEFT_CORNER : FOOTER_RIGHT_CORNER;
		} else if (key.equals(FOOTER_TRAILING_CORNER)) {
			key = isLeftToRight ? FOOTER_RIGHT_CORNER : FOOTER_LEFT_CORNER;
		} else if (key.equals(NORTH_LEADING_CORNER)) {
			key = isLeftToRight ? NORTH_LEFT_CORNER : NORTH_RIGHT_CORNER;
		} else if (key.equals(NORTH_TRAILING_CORNER)) {
			key = isLeftToRight ? NORTH_RIGHT_CORNER : NORTH_LEFT_CORNER;
		}
		if (key.equals(LOWER_LEFT_CORNER)) {
			old = lowerLeft;
			lowerLeft = corner;
		} else if (key.equals(LOWER_RIGHT_CORNER)) {
			old = lowerRight;
			lowerRight = corner;
		} else if (key.equals(UPPER_LEFT_CORNER)) {
			old = upperLeft;
			upperLeft = corner;
		} else if (key.equals(UPPER_RIGHT_CORNER)) {
			old = upperRight;
			upperRight = corner;
		} else if (key.equals(FOOTER_LEFT_CORNER)) {
			old = footerLeft;
			footerLeft = corner;
		} else if (key.equals(FOOTER_RIGHT_CORNER)) {
			old = footerRight;
			footerRight = corner;
		} else if (key.equals(NORTH_CENTER)) {
			old = northCenter;
			northCenter = corner;
		} else if (key.equals(NORTH_LEFT_CORNER)) {
			old = northLeft;
			northLeft = corner;
		} else if (key.equals(NORTH_RIGHT_CORNER)) {
			old = northRight;
			northRight = corner;
		} else {
			throw new IllegalArgumentException("invalid corner key"); //$NON-NLS-1$
		}
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

	public void setComponentOrientation(ComponentOrientation co) {
		super.setComponentOrientation(co);
		if (verticalScrollBar != null)
			verticalScrollBar.setComponentOrientation(co);
		if (horizontalScrollBar != null)
			horizontalScrollBar.setComponentOrientation(co);
	}

	public boolean isWheelScrollingEnabled() {
		return wheelScrollState;
	}

	public void setWheelScrollingEnabled(boolean handleWheel) {
		boolean old = wheelScrollState;
		wheelScrollState = handleWheel;
		firePropertyChange("wheelScrollingEnabled", old, handleWheel); //$NON-NLS-1$
	}

	private void writeObject(ObjectOutputStream s) throws IOException {
		/*
		 * s.defaultWriteObject(); if (getUIClassID().equals(uiClassID)) { byte
		 * count = (byte)((this.flags >> WRITE_OBJ_COUNTER_FIRST) &
		 * 0xFF);JComponent.getWriteObjCounter(this);
		 * JComponent.setWriteObjCounter(this, --count); if (count == 0 && ui !=
		 * null) { ui.installUI(this); } }
		 */
	}

	protected String paramString() {
		String viewportBorderString = (viewportBorder != null ? viewportBorder
				.toString() : ""); //$NON-NLS-1$
		String viewportString = (viewport != null ? viewport.toString() : ""); //$NON-NLS-1$
		String verticalScrollBarPolicyString;
		if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
			verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_AS_NEEDED"; //$NON-NLS-1$
		} else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_NEVER) {
			verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_NEVER"; //$NON-NLS-1$
		} else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
			verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_ALWAYS"; //$NON-NLS-1$
		} else
			verticalScrollBarPolicyString = ""; //$NON-NLS-1$
		String horizontalScrollBarPolicyString;
		if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
			horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_AS_NEEDED"; //$NON-NLS-1$
		} else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
			horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_NEVER"; //$NON-NLS-1$
		} else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
			horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_ALWAYS"; //$NON-NLS-1$
		} else
			horizontalScrollBarPolicyString = ""; //$NON-NLS-1$
		String horizontalScrollBarString = (horizontalScrollBar != null ? horizontalScrollBar
				.toString()
				: ""); //$NON-NLS-1$
		String verticalScrollBarString = (verticalScrollBar != null ? verticalScrollBar
				.toString()
				: ""); //$NON-NLS-1$
		String columnHeaderString = (columnHeader != null ? columnHeader
				.toString() : ""); //$NON-NLS-1$
		String columnFooterString = (columnFooter != null ? columnFooter
				.toString() : ""); //$NON-NLS-1$
		String rowHeaderString = (rowHeader != null ? rowHeader.toString() : ""); //$NON-NLS-1$
		String lowerLeftString = (lowerLeft != null ? lowerLeft.toString() : ""); //$NON-NLS-1$
		String lowerRightString = (lowerRight != null ? lowerRight.toString()
				: ""); //$NON-NLS-1$
		String upperLeftString = (upperLeft != null ? upperLeft.toString() : ""); //$NON-NLS-1$
		String upperRightString = (upperRight != null ? upperRight.toString()
				: ""); //$NON-NLS-1$
		String footerLeftString = (footerLeft != null ? footerLeft.toString()
				: ""); //$NON-NLS-1$
		String footerRightString = (footerRight != null ? footerRight
				.toString() : ""); //$NON-NLS-1$

		String northCenterString = (northCenter != null ? northCenter
				.toString() : ""); //$NON-NLS-1$
		String northLeftString = (northLeft != null ? northLeft.toString() : ""); //$NON-NLS-1$
		String northRightString = (northRight != null ? northRight.toString()
				: ""); //$NON-NLS-1$

		return super.paramString() + ",columnHeader=" + columnHeaderString //$NON-NLS-1$
				+ ",columnFooter=" + columnFooterString //$NON-NLS-1$
				+ ",horizontalScrollBar=" + horizontalScrollBarString //$NON-NLS-1$
				+ ",horizontalScrollBarPolicy=" //$NON-NLS-1$
				+ horizontalScrollBarPolicyString + ",lowerLeft=" //$NON-NLS-1$
				+ lowerLeftString + ",lowerRight=" + lowerRightString //$NON-NLS-1$
				+ ",rowHeader=" + rowHeaderString + ",upperLeft=" //$NON-NLS-1$ //$NON-NLS-2$
				+ upperLeftString + ",upperRight=" + upperRightString //$NON-NLS-1$
				+ ",footerLeft=" + footerLeftString + ",footerRight=" //$NON-NLS-1$ //$NON-NLS-2$
				+ footerRightString + ",northLeft=" + northLeftString //$NON-NLS-1$
				+ ",northRight=" + northRightString + ",northCenter=" //$NON-NLS-1$ //$NON-NLS-2$
				+ northCenterString + ",verticalScrollBar=" //$NON-NLS-1$
				+ verticalScrollBarString + ",verticalScrollBarPolicy=" //$NON-NLS-1$
				+ verticalScrollBarPolicyString + ",viewport=" + viewportString //$NON-NLS-1$
				+ ",viewportBorder=" + viewportBorderString; //$NON-NLS-1$
	}

	// ///////////////
	// Accessibility support
	// //////////////

	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleJYScrollPane();
		}
		return accessibleContext;
	}

	protected class AccessibleJYScrollPane extends AccessibleJComponent
			implements ChangeListener, PropertyChangeListener {

		private static final long serialVersionUID = 2268296182680094779L;

		protected JViewport viewPort = null;

		/*
		 * Resets the viewport ChangeListener and PropertyChangeListener
		 */
		public void resetViewPort() {
			if (viewPort != null) {
				viewPort.removeChangeListener(this);
				viewPort.removePropertyChangeListener(this);
			}
			viewPort = JSyScrollPane.this.getViewport();
			if (viewPort != null) {
				viewPort.addChangeListener(this);
				viewPort.addPropertyChangeListener(this);
			}
		}

		/**
		 * AccessibleJScrollPane constructor
		 */
		public AccessibleJYScrollPane() {
			super();

			resetViewPort();

			// initialize the AccessibleRelationSets for the JScrollPane
			// and JScrollBar(s)
			JScrollBar scrollBar = getHorizontalScrollBar();
			if (scrollBar != null) {
				setScrollBarRelations(scrollBar);
			}
			scrollBar = getVerticalScrollBar();
			if (scrollBar != null) {
				setScrollBarRelations(scrollBar);
			}
		}

		/**
		 * Get the role of this object.
		 * 
		 * @return an instance of AccessibleRole describing the role of the
		 *         object
		 * @see AccessibleRole
		 */
		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.SCROLL_PANE;
		}

		/**
		 * Invoked when the target of the listener has changed its state.
		 * 
		 * @param e
		 *            a <code>ChangeEvent</code> object. Must not be null.
		 * 
		 * @throws NullPointerException
		 *             if the parameter is null.
		 */
		public void stateChanged(ChangeEvent e) {
			if (e == null) {
				throw new NullPointerException();
			}
			firePropertyChange(ACCESSIBLE_VISIBLE_DATA_PROPERTY, Boolean
					.valueOf(false), Boolean.valueOf(true));
		}

		/**
		 * This method gets called when a bound property is changed.
		 * 
		 * @param evt
		 *            A <code>PropertyChangeEvent</code> object describing the
		 *            event source and the property that has changed. Must not
		 *            be null.
		 * 
		 * @throws NullPointerException
		 *             if the parameter is null.
		 * @since 1.5
		 */
		public void propertyChange(PropertyChangeEvent e) {
			String propertyName = e.getPropertyName();
			if (propertyName == "horizontalScrollBar" //$NON-NLS-1$
					|| propertyName == "verticalScrollBar") { //$NON-NLS-1$

				if (e.getNewValue() instanceof JScrollBar) {
					setScrollBarRelations((JScrollBar) e.getNewValue());
				}
			}
		}

		/*
		 * Sets the CONTROLLER_FOR and CONTROLLED_BY AccessibleRelations for the
		 * JScrollPane and JScrollBar. JScrollBar must not be null.
		 */
		void setScrollBarRelations(JScrollBar scrollBar) {
			/*
			 * The JScrollBar is a CONTROLLER_FOR the JScrollPane. The
			 * JScrollPane is CONTROLLED_BY the JScrollBar.
			 */
			AccessibleRelation controlledBy = new AccessibleRelation(
					AccessibleRelation.CONTROLLED_BY, scrollBar);
			AccessibleRelation controllerFor = new AccessibleRelation(
					AccessibleRelation.CONTROLLER_FOR, JSyScrollPane.this);

			// set the relation set for the scroll bar
			AccessibleContext ac = scrollBar.getAccessibleContext();
			ac.getAccessibleRelationSet().add(controllerFor);

			// set the relation set for the scroll pane
			getAccessibleRelationSet().add(controlledBy);
		}
	}
}
