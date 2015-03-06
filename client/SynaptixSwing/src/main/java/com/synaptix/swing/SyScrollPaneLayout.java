package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneLayout;
import javax.swing.Scrollable;
import javax.swing.border.Border;

public class SyScrollPaneLayout extends ScrollPaneLayout implements
		LayoutManager, SyScrollPaneConstants, Serializable {

	private static final long serialVersionUID = 6485900894701109221L;

	protected JViewport viewport;

	protected JScrollBar vsb;

	protected JScrollBar hsb;

	protected JViewport rowHead;

	protected JViewport colHead;

	protected JViewport colFoot;

	protected Component lowerLeft;

	protected Component lowerRight;

	protected Component upperLeft;

	protected Component upperRight;

	protected Component footerLeft;

	protected Component footerRight;

	protected Component northCenter;

	protected Component northLeft;

	protected Component northRight;

	protected int vsbPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;

	protected int hsbPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;

	public void syncWithScrollPane(JSyScrollPane sp) {
		viewport = sp.getViewport();
		vsb = sp.getVerticalScrollBar();
		hsb = sp.getHorizontalScrollBar();
		rowHead = sp.getRowHeader();
		colHead = sp.getColumnHeader();
		colFoot = sp.getColumnFooter();
		lowerLeft = sp.getCorner(LOWER_LEFT_CORNER);
		lowerRight = sp.getCorner(LOWER_RIGHT_CORNER);
		upperLeft = sp.getCorner(UPPER_LEFT_CORNER);
		upperRight = sp.getCorner(UPPER_RIGHT_CORNER);
		footerLeft = sp.getCorner(FOOTER_LEFT_CORNER);
		footerRight = sp.getCorner(FOOTER_RIGHT_CORNER);

		northCenter = sp.getCorner(NORTH_CENTER);
		northLeft = sp.getCorner(NORTH_LEFT_CORNER);
		northRight = sp.getCorner(NORTH_RIGHT_CORNER);

		vsbPolicy = sp.getVerticalScrollBarPolicy();
		hsbPolicy = sp.getHorizontalScrollBarPolicy();
	}

	protected Component addSingletonComponent(Component oldC, Component newC) {
		if ((oldC != null) && (oldC != newC)) {
			oldC.getParent().remove(oldC);
		}
		return newC;
	}

	public void addLayoutComponent(String s, Component c) {
		if (s.equals(VIEWPORT)) {
			viewport = (JViewport) addSingletonComponent(viewport, c);
		} else if (s.equals(VERTICAL_SCROLLBAR)) {
			vsb = (JScrollBar) addSingletonComponent(vsb, c);
		} else if (s.equals(HORIZONTAL_SCROLLBAR)) {
			hsb = (JScrollBar) addSingletonComponent(hsb, c);
		} else if (s.equals(ROW_HEADER)) {
			rowHead = (JViewport) addSingletonComponent(rowHead, c);
		} else if (s.equals(COLUMN_HEADER)) {
			colHead = (JViewport) addSingletonComponent(colHead, c);
		} else if (s.equals(COLUMN_FOOTER)) {
			colFoot = (JViewport) addSingletonComponent(colFoot, c);
		} else if (s.equals(LOWER_LEFT_CORNER)) {
			lowerLeft = addSingletonComponent(lowerLeft, c);
		} else if (s.equals(LOWER_RIGHT_CORNER)) {
			lowerRight = addSingletonComponent(lowerRight, c);
		} else if (s.equals(FOOTER_LEFT_CORNER)) {
			footerLeft = addSingletonComponent(footerLeft, c);
		} else if (s.equals(FOOTER_RIGHT_CORNER)) {
			footerRight = addSingletonComponent(footerRight, c);
		} else if (s.equals(UPPER_LEFT_CORNER)) {
			upperLeft = addSingletonComponent(upperLeft, c);
		} else if (s.equals(UPPER_RIGHT_CORNER)) {
			upperRight = addSingletonComponent(upperRight, c);
		} else if (s.equals(NORTH_CENTER)) {
			northCenter = addSingletonComponent(northCenter, c);
		} else if (s.equals(NORTH_LEFT_CORNER)) {
			northLeft = addSingletonComponent(northLeft, c);
		} else if (s.equals(NORTH_RIGHT_CORNER)) {
			northRight = addSingletonComponent(northRight, c);
		} else {
			throw new IllegalArgumentException("invalid layout key " + s); //$NON-NLS-1$
		}
	}

	public void removeLayoutComponent(Component c) {
		if (c == viewport) {
			viewport = null;
		} else if (c == vsb) {
			vsb = null;
		} else if (c == hsb) {
			hsb = null;
		} else if (c == rowHead) {
			rowHead = null;
		} else if (c == colHead) {
			colHead = null;
		} else if (c == colFoot) {
			colFoot = null;
		} else if (c == lowerLeft) {
			lowerLeft = null;
		} else if (c == lowerRight) {
			lowerRight = null;
		} else if (c == upperLeft) {
			upperLeft = null;
		} else if (c == upperRight) {
			upperRight = null;
		} else if (c == footerLeft) {
			footerLeft = null;
		} else if (c == footerRight) {
			footerRight = null;
		} else if (c == northCenter) {
			northCenter = null;
		} else if (c == northLeft) {
			northLeft = null;
		} else if (c == northRight) {
			northRight = null;
		}
	}

	public int getVerticalScrollBarPolicy() {
		return vsbPolicy;
	}

	public void setVerticalScrollBarPolicy(int x) {
		switch (x) {
		case VERTICAL_SCROLLBAR_AS_NEEDED:
		case VERTICAL_SCROLLBAR_NEVER:
		case VERTICAL_SCROLLBAR_ALWAYS:
			vsbPolicy = x;
			break;
		default:
			throw new IllegalArgumentException(
					"invalid verticalScrollBarPolicy"); //$NON-NLS-1$
		}
	}

	public int getHorizontalScrollBarPolicy() {
		return hsbPolicy;
	}

	public void setHorizontalScrollBarPolicy(int x) {
		switch (x) {
		case HORIZONTAL_SCROLLBAR_AS_NEEDED:
		case HORIZONTAL_SCROLLBAR_NEVER:
		case HORIZONTAL_SCROLLBAR_ALWAYS:
			hsbPolicy = x;
			break;
		default:
			throw new IllegalArgumentException(
					"invalid horizontalScrollBarPolicy"); //$NON-NLS-1$
		}
	}

	public JViewport getViewport() {
		return viewport;
	}

	public JScrollBar getHorizontalScrollBar() {
		return hsb;
	}

	public JScrollBar getVerticalScrollBar() {
		return vsb;
	}

	public JViewport getRowHeader() {
		return rowHead;
	}

	public JViewport getColumnHeader() {
		return colHead;
	}

	public JViewport getColumnFooter() {
		return colFoot;
	}

	public Component getCorner(String key) {
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
		} else {
			return null;
		}
	}

	public Dimension preferredLayoutSize(Container parent) {
		/*
		 * Sync the (now obsolete) policy fields with the JScrollPane.
		 */
		JSyScrollPane scrollPane = (JSyScrollPane) parent;
		vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
		hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

		Insets insets = parent.getInsets();
		int prefWidth = insets.left + insets.right;
		int prefHeight = insets.top + insets.bottom;

		/*
		 * Note that viewport.getViewSize() is equivalent to
		 * viewport.getView().getPreferredSize() modulo a null view or a view
		 * whose size was explicitly set.
		 */

		Dimension extentSize = null;
		Dimension viewSize = null;
		Component view = null;

		if (viewport != null) {
			extentSize = viewport.getPreferredSize();
			viewSize = viewport.getViewSize();
			view = viewport.getView();
		}

		/*
		 * If there's a viewport add its preferredSize.
		 */

		if (extentSize != null) {
			prefWidth += extentSize.width;
			prefHeight += extentSize.height;
		}

		/*
		 * If there's a JScrollPane.viewportBorder, add its insets.
		 */

		Border viewportBorder = scrollPane.getViewportBorder();
		if (viewportBorder != null) {
			Insets vpbInsets = viewportBorder.getBorderInsets(parent);
			prefWidth += vpbInsets.left + vpbInsets.right;
			prefHeight += vpbInsets.top + vpbInsets.bottom;
		}

		/*
		 * If a header exists and it's visible, factor its preferred size in.
		 */

		int maxLeftWidth = 0;
		if ((upperLeft != null) && upperLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					upperLeft.getPreferredSize().width);
		}
		if ((rowHead != null) && rowHead.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					rowHead.getPreferredSize().width);
		}
		if ((lowerLeft != null) && lowerLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					lowerLeft.getPreferredSize().width);
		}
		if ((footerLeft != null) && footerLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					footerLeft.getPreferredSize().width);
		}
		prefHeight += maxLeftWidth;

		if ((colHead != null) && colHead.isVisible()) {
			prefHeight += colHead.getPreferredSize().height;
		}

		if ((colFoot != null) && colFoot.isVisible()) {
			prefHeight += colFoot.getPreferredSize().height;
		}

		// NORTH
		int maxNorthHeight = 0;
		if (northCenter != null && northCenter.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northCenter
					.getPreferredSize().height);
		}
		if (northRight != null && northRight.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northRight
					.getPreferredSize().height);
		}
		if (northLeft != null && northLeft.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northLeft
					.getPreferredSize().height);
		}

		prefHeight += maxNorthHeight;

		/*
		 * If a scrollbar is going to appear, factor its preferred size in. If
		 * the scrollbars policy is AS_NEEDED, this can be a little tricky: - If
		 * the view is a Scrollable then scrollableTracksViewportWidth and
		 * scrollableTracksViewportHeight can be used to effectively disable
		 * scrolling (if they're true) in their respective dimensions. -
		 * Assuming that a scrollbar hasn't been disabled by the previous
		 * constraint, we need to decide if the scrollbar is going to appear to
		 * correctly compute the JScrollPanes preferred size. To do this we
		 * compare the preferredSize of the viewport (the extentSize) to the
		 * preferredSize of the view. Although we're not responsible for laying
		 * out the view we'll assume that the JViewport will always give it its
		 * preferredSize.
		 */

		if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
			if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
				prefWidth += vsb.getPreferredSize().width;
			} else if ((viewSize != null) && (extentSize != null)) {
				boolean canScroll = true;
				if (view instanceof Scrollable) {
					canScroll = !((Scrollable) view)
							.getScrollableTracksViewportHeight();
				}
				if (canScroll && (viewSize.height > extentSize.height)) {
					prefWidth += vsb.getPreferredSize().width;
				}
			}
		}

		if ((hsb != null) && (hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER)) {
			if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
				prefHeight += hsb.getPreferredSize().height;
			} else if ((viewSize != null) && (extentSize != null)) {
				boolean canScroll = true;
				if (view instanceof Scrollable) {
					canScroll = !((Scrollable) view)
							.getScrollableTracksViewportWidth();
				}
				if (canScroll && (viewSize.width > extentSize.width)) {
					prefHeight += hsb.getPreferredSize().height;
				}
			}
		}

		return new Dimension(prefWidth, prefHeight);
	}

	public Dimension minimumLayoutSize(Container parent) {
		JScrollPane scrollPane = (JScrollPane) parent;
		vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
		hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

		Insets insets = parent.getInsets();
		int minWidth = insets.left + insets.right;
		int minHeight = insets.top + insets.bottom;

		/*
		 * If there's a viewport add its minimumSize.
		 */

		if (viewport != null) {
			Dimension size = viewport.getMinimumSize();
			minWidth += size.width;
			minHeight += size.height;
		}

		/*
		 * If there's a JScrollPane.viewportBorder, add its insets.
		 */
		Border viewportBorder = scrollPane.getViewportBorder();
		if (viewportBorder != null) {
			Insets vpbInsets = viewportBorder.getBorderInsets(parent);
			minWidth += vpbInsets.left + vpbInsets.right;
			minHeight += vpbInsets.top + vpbInsets.bottom;
		}

		/*
		 * If a header exists and it's visible, factor its minimum size in.
		 */

		if ((rowHead != null) && rowHead.isVisible()) {
			Dimension size = rowHead.getMinimumSize();
			minWidth += size.width;
			minHeight = Math.max(minHeight, size.height);
		}

		if ((colHead != null) && colHead.isVisible()) {
			Dimension size = colHead.getMinimumSize();
			minWidth = Math.max(minWidth, size.width);
			minHeight += size.height;
		}

		if ((colFoot != null) && colFoot.isVisible()) {
			Dimension size = colFoot.getMinimumSize();
			minWidth = Math.max(minWidth, size.width);
			minHeight += size.height;
		}

		/*
		 * If a scrollbar might appear, factor its minimum size in.
		 */

		if ((vsb != null) && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {
			Dimension size = vsb.getMinimumSize();
			minWidth += size.width;
			minHeight = Math.max(minHeight, size.height);
		}

		if ((hsb != null) && (hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER)) {
			Dimension size = hsb.getMinimumSize();
			minWidth = Math.max(minWidth, size.width);
			minHeight += size.height;
		}

		// NORTH
		if (northCenter != null && northCenter.isVisible()) {
			minHeight += northCenter.getMinimumSize().height;
		}

		return new Dimension(minWidth, minHeight);
	}

	public void layoutContainer(Container parent) {
		/*
		 * Sync the (now obsolete) policy fields with the JScrollPane.
		 */
		JSyScrollPane scrollPane = (JSyScrollPane) parent;
		vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
		hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

		Rectangle availR = scrollPane.getBounds();
		availR.x = availR.y = 0;

		Insets insets = parent.getInsets();
		availR.x = insets.left;
		availR.y = insets.top;
		availR.width -= insets.left + insets.right;
		availR.height -= insets.top + insets.bottom;

		Rectangle northHeadR = new Rectangle(0, availR.y, 0, 0);
		int maxNorthHeight = 0;
		if (northCenter != null && northCenter.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northCenter
					.getPreferredSize().height);
		}
		if (northRight != null && northRight.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northRight
					.getPreferredSize().height);
		}
		if (northLeft != null && northLeft.isVisible()) {
			maxNorthHeight = Math.max(maxNorthHeight, northLeft
					.getPreferredSize().height);
		}
		availR.y += maxNorthHeight;
		availR.height -= maxNorthHeight;
		northHeadR.height = maxNorthHeight;
		
		/*
		 * Get the scrollPane's orientation.
		 */
		boolean leftToRight = scrollPane.getComponentOrientation()
				.isLeftToRight();

		/*
		 * If there's a visible column header remove the space it needs from the
		 * top of availR. The column header is treated as if it were fixed
		 * height, arbitrary width.
		 */
		Rectangle colHeadR = new Rectangle(0, availR.y, 0, 0);

		if ((colHead != null) && (colHead.isVisible())) {
			int colHeadHeight = Math.min(availR.height, colHead
					.getPreferredSize().height);
			colHeadR.height = colHeadHeight;
			availR.y += colHeadHeight;
			availR.height -= colHeadHeight;
		}

		Rectangle colFootR = new Rectangle(0, availR.y, 0, 0);
		if ((colFoot != null) && (colFoot.isVisible())) {
			int colFootHeight = Math.min(availR.height, colFoot
					.getPreferredSize().height);
			colFootR.height = colFootHeight;
			// availR.y += colFootHeight;
			availR.height -= colFootHeight;
		}

		/*
		 * If there's a visible row header remove the space it needs from the
		 * left or right of availR. The row header is treated as if it were
		 * fixed width, arbitrary height.
		 */

		Rectangle rowHeadR = new Rectangle(0, 0, 0, 0);

		int maxLeftWidth = 0;
		if ((upperLeft != null) && upperLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					upperLeft.getPreferredSize().width);
		}
		if ((rowHead != null) && rowHead.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					rowHead.getPreferredSize().width);
		}
		if ((lowerLeft != null) && lowerLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					lowerLeft.getPreferredSize().width);
		}
		if ((footerLeft != null) && footerLeft.isVisible()) {
			maxLeftWidth = Math.max(maxLeftWidth,
					footerLeft.getPreferredSize().width);
		}

		if ((rowHead != null) && (rowHead.isVisible())) {
			int rowHeadWidth = Math.min(availR.width, maxLeftWidth); // rowHead.getPreferredSize().width);
			rowHeadR.width = rowHeadWidth;
			availR.width -= rowHeadWidth;
			if (leftToRight) {
				rowHeadR.x = availR.x;
				availR.x += rowHeadWidth;
			} else {
				rowHeadR.x = availR.x + availR.width;
			}
		}

		/*
		 * If there's a JScrollPane.viewportBorder, remove the space it occupies
		 * for availR.
		 */

		Border viewportBorder = scrollPane.getViewportBorder();
		Insets vpbInsets;
		if (viewportBorder != null) {
			vpbInsets = viewportBorder.getBorderInsets(parent);
			availR.x += vpbInsets.left;
			availR.y += vpbInsets.top;
			availR.width -= vpbInsets.left + vpbInsets.right;
			availR.height -= vpbInsets.top + vpbInsets.bottom;
		} else {
			vpbInsets = new Insets(0, 0, 0, 0);
		}

		/*
		 * At this point availR is the space available for the viewport and
		 * scrollbars. rowHeadR is correct except for its height and y and
		 * colHeadR is correct except for its width and x. Once we're through
		 * computing the dimensions of these three parts we can go back and set
		 * the dimensions of rowHeadR.height, rowHeadR.y, colHeadR.width,
		 * colHeadR.x and the bounds for the corners.
		 * 
		 * We'll decide about putting up scrollbars by comparing the viewport
		 * views preferred size with the viewports extent size (generally just
		 * its size). Using the preferredSize is reasonable because layout
		 * proceeds top down - so we expect the viewport to be laid out next.
		 * And we assume that the viewports layout manager will give the view
		 * it's preferred size. One exception to this is when the view
		 * implements Scrollable and
		 * Scrollable.getViewTracksViewport{Width,Height} methods return true.
		 * If the view is tracking the viewports width we don't bother with a
		 * horizontal scrollbar, similarly if view.getViewTracksViewport(Height)
		 * is true we don't bother with a vertical scrollbar.
		 */

		Component view = (viewport != null) ? viewport.getView() : null;
		Dimension viewPrefSize = (view != null) ? view.getPreferredSize()
				: new Dimension(0, 0);

		Dimension extentSize = (viewport != null) ? viewport
				.toViewCoordinates(availR.getSize()) : new Dimension(0, 0);

		boolean viewTracksViewportWidth = false;
		boolean viewTracksViewportHeight = false;
		boolean isEmpty = (availR.width < 0 || availR.height < 0);
		Scrollable sv;
		// Don't bother checking the Scrollable methods if there is no room
		// for the viewport, we aren't going to show any scrollbars in this
		// case anyway.
		if (!isEmpty && view instanceof Scrollable) {
			sv = (Scrollable) view;
			viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
			viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
		} else {
			sv = null;
		}

		/*
		 * If there's a vertical scrollbar and we need one, allocate space for
		 * it (we'll make it visible later). A vertical scrollbar is considered
		 * to be fixed width, arbitrary height.
		 */

		Rectangle vsbR = new Rectangle(0, availR.y - vpbInsets.top, 0, 0);

		boolean vsbNeeded;
		if (isEmpty) {
			vsbNeeded = false;
		} else if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
			vsbNeeded = true;
		} else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
			vsbNeeded = false;
		} else { // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
			vsbNeeded = !viewTracksViewportHeight
					&& (viewPrefSize.height > extentSize.height);
		}

		if ((vsb != null) && vsbNeeded) {
			adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
			extentSize = viewport.toViewCoordinates(availR.getSize());
		}

		/*
		 * If there's a horizontal scrollbar and we need one, allocate space for
		 * it (we'll make it visible later). A horizontal scrollbar is
		 * considered to be fixed height, arbitrary width.
		 */

		Rectangle hsbR = new Rectangle(availR.x - vpbInsets.left, 0, 0, 0);
		boolean hsbNeeded;
		if (isEmpty) {
			hsbNeeded = false;
		} else if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
			hsbNeeded = true;
		} else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
			hsbNeeded = false;
		} else { // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
			hsbNeeded = !viewTracksViewportWidth
					&& (viewPrefSize.width > extentSize.width);
		}

		if ((hsb != null) && hsbNeeded) {
			adjustForHSB(true, availR, hsbR, vpbInsets);

			/*
			 * If we added the horizontal scrollbar then we've implicitly
			 * reduced the vertical space available to the viewport. As a
			 * consequence we may have to add the vertical scrollbar, if that
			 * hasn't been done so already. Of course we don't bother with any
			 * of this if the vsbPolicy is NEVER.
			 */
			if ((vsb != null) && !vsbNeeded
					&& (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

				extentSize = viewport.toViewCoordinates(availR.getSize());
				vsbNeeded = viewPrefSize.height > extentSize.height;

				if (vsbNeeded) {
					adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
				}
			}
		}

		/*
		 * Set the size of the viewport first, and then recheck the Scrollable
		 * methods. Some components base their return values for the Scrollable
		 * methods on the size of the Viewport, so that if we don't ask after
		 * resetting the bounds we may have gotten the wrong answer.
		 */

		if (viewport != null) {
			viewport.setBounds(availR);

			if (sv != null) {
				extentSize = viewport.toViewCoordinates(availR.getSize());

				boolean oldHSBNeeded = hsbNeeded;
				boolean oldVSBNeeded = vsbNeeded;
				viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
				viewTracksViewportHeight = sv
						.getScrollableTracksViewportHeight();
				if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
					boolean newVSBNeeded = !viewTracksViewportHeight
							&& (viewPrefSize.height > extentSize.height);
					if (newVSBNeeded != vsbNeeded) {
						vsbNeeded = newVSBNeeded;
						adjustForVSB(vsbNeeded, availR, vsbR, vpbInsets,
								leftToRight);
						extentSize = viewport.toViewCoordinates(availR
								.getSize());
					}
				}
				if (hsb != null && hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
					boolean newHSBbNeeded = !viewTracksViewportWidth
							&& (viewPrefSize.width > extentSize.width);
					if (newHSBbNeeded != hsbNeeded) {
						hsbNeeded = newHSBbNeeded;
						adjustForHSB(hsbNeeded, availR, hsbR, vpbInsets);
						if ((vsb != null) && !vsbNeeded
								&& (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

							extentSize = viewport.toViewCoordinates(availR
									.getSize());
							vsbNeeded = viewPrefSize.height > extentSize.height;

							if (vsbNeeded) {
								adjustForVSB(true, availR, vsbR, vpbInsets,
										leftToRight);
							}
						}
					}
				}
				if (oldHSBNeeded != hsbNeeded || oldVSBNeeded != vsbNeeded) {
					viewport.setBounds(availR);
					// You could argue that we should recheck the
					// Scrollable methods again until they stop changing,
					// but they might never stop changing, so we stop here
					// and don't do any additional checks.
				}
			}
		}

		/*
		 * We now have the final size of the viewport: availR. Now fixup the
		 * header and scrollbar widths/heights.
		 */

		colFootR.width = availR.width + vpbInsets.left + vpbInsets.right;
		colFootR.x = availR.x - vpbInsets.left;
		colFootR.y += availR.height + vpbInsets.top + vpbInsets.bottom
				+ hsbR.height;

		vsbR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
		hsbR.width = availR.width + vpbInsets.left + vpbInsets.right;
		rowHeadR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
		rowHeadR.y = availR.y - vpbInsets.top;
		colHeadR.width = availR.width + vpbInsets.left + vpbInsets.right;
		colHeadR.x = availR.x - vpbInsets.left;

		northHeadR.width = availR.width + vpbInsets.left + vpbInsets.right;
		northHeadR.x = availR.x - vpbInsets.left;
		
		/*
		 * Set the bounds of the remaining components. The scrollbars are made
		 * invisible if they're not needed.
		 */
		
		if (northCenter != null) {
			northCenter.setBounds(northHeadR);
		}
		
		if (rowHead != null) {
			rowHead.setBounds(rowHeadR);
		}

		if (colHead != null) {
			colHead.setBounds(colHeadR);
		}

		if (colFoot != null) {
			colFoot.setBounds(colFootR);
		}

		if (vsb != null) {
			if (vsbNeeded) {
				vsb.setVisible(true);
				vsb.setBounds(vsbR);
			} else {
				vsb.setVisible(false);
			}
		}

		if (hsb != null) {
			if (hsbNeeded) {
				hsb.setVisible(true);
				hsb.setBounds(hsbR);
			} else {
				hsb.setVisible(false);
			}
		}

		if (northLeft != null) {
			northLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x, northHeadR.y,
					leftToRight ? rowHeadR.width : vsbR.width, northHeadR.height);
		}
		
		if (northRight != null) {
			northRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x, northHeadR.y,
					leftToRight ? vsbR.width : rowHeadR.width, northHeadR.height);
		}
		
		if (lowerLeft != null) {
			lowerLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x, hsbR.y,
					leftToRight ? rowHeadR.width : vsbR.width, hsbR.height);
		}

		if (lowerRight != null) {
			lowerRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x, hsbR.y,
					leftToRight ? vsbR.width : rowHeadR.width, hsbR.height);
		}

		if (upperLeft != null) {
			upperLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x, colHeadR.y,
					leftToRight ? rowHeadR.width : vsbR.width, colHeadR.height);
		}

		if (upperRight != null) {
			upperRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x, colHeadR.y,
					leftToRight ? vsbR.width : rowHeadR.width, colHeadR.height);
		}

		if (footerLeft != null) {
			footerLeft.setBounds(leftToRight ? rowHeadR.x : vsbR.x, colFootR.y,
					leftToRight ? rowHeadR.width : vsbR.width, colFootR.height);
		}

		if (footerRight != null) {
			footerRight.setBounds(leftToRight ? vsbR.x : rowHeadR.x,
					colFootR.y, leftToRight ? vsbR.width : rowHeadR.width,
					colFootR.height);
		}
	}

	private void adjustForVSB(boolean wantsVSB, Rectangle available,
			Rectangle vsbR, Insets vpbInsets, boolean leftToRight) {
		int oldWidth = vsbR.width;
		if (wantsVSB) {
			int vsbWidth = Math.max(0, Math.min(vsb.getPreferredSize().width,
					available.width));

			available.width -= vsbWidth;
			vsbR.width = vsbWidth;

			if (leftToRight) {
				vsbR.x = available.x + available.width + vpbInsets.right;
			} else {
				vsbR.x = available.x - vpbInsets.left;
				available.x += vsbWidth;
			}
		} else {
			available.width += oldWidth;
		}
	}

	private void adjustForHSB(boolean wantsHSB, Rectangle available,
			Rectangle hsbR, Insets vpbInsets) {
		int oldHeight = hsbR.height;
		if (wantsHSB) {
			int hsbHeight = Math.max(0, Math.min(available.height, hsb
					.getPreferredSize().height));

			available.height -= hsbHeight;
			hsbR.y = available.y + available.height + vpbInsets.bottom;
			hsbR.height = hsbHeight;
		} else {
			available.height += oldHeight;
		}
	}

	public static class UIResource extends ScrollPaneLayout implements
			javax.swing.plaf.UIResource {
		private static final long serialVersionUID = 7713332424687788851L;
	}
}
