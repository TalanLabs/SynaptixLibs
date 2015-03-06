package com.synaptix.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.plaf.TimelineRessourceHeaderUI;
import com.synaptix.swing.timeline.JTimelineRessourceHeader;
import com.synaptix.swing.timeline.TimelineRessource;
import com.synaptix.swing.timeline.TimelineRessourceModel;
import com.synaptix.swing.timeline.TimelineRessourceRenderer;

public class BasicTimelineRessourceHeaderUI extends TimelineRessourceHeaderUI {

	private static Cursor resizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	protected JTimelineRessourceHeader header;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	private int rolloverRessource = -1;

	private int selectedRessourceIndex = 0;

	public class MouseInputHandler implements MouseInputListener {

		private int mouseYOffset;

		private Cursor otherCursor = resizeCursor;

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() % 2 == 1
					&& SwingUtilities.isLeftMouseButton(e)) {
				// Si je veux faire un trie, non !!!
			}
		}

		private TimelineRessource getResizingRessource(Point p) {
			return getResizingRessource(p, header.ressourceAtPoint(p));
		}

		private TimelineRessource getResizingRessource(Point p, int ressource) {
			if (ressource == -1) {
				return null;
			}
			Rectangle r = header.getHeaderRect(ressource);
			r.grow(0, -3);
			if (r.contains(p)) {
				return null;
			}
			int midPoint = r.y + r.height / 2;
			int ressourceIndex;
			ressourceIndex = (p.y < midPoint) ? ressource - 1 : ressource;

			if (ressourceIndex == -1) {
				return null;
			}
			return header.getRessourceModel().getRessource(ressourceIndex);
		}

		public void mousePressed(MouseEvent e) {
			header.setDraggedRessource(null);
			header.setResizingRessource(null);
			header.setDraggedDistance(0);

			Point p = e.getPoint();

			// First find which header cell was hit
			TimelineRessourceModel ressourceModel = header.getRessourceModel();
			int index = header.ressourceAtPoint(p);

			if (index != -1) {
				// The last 3 pixels + 3 pixels of next column are for resizing
				TimelineRessource resizingRessource = getResizingRessource(p,
						index);
				if (canResize(resizingRessource, header)) {
					header.setResizingRessource(resizingRessource);
					mouseYOffset = p.y - resizingRessource.getHeight();
				} else if (header.getReorderingAllowed()) {
					TimelineRessource hitRessource = ressourceModel
							.getRessource(index);
					header.setDraggedRessource(hitRessource);
					mouseYOffset = p.y;
				}
			}

			if (header.getReorderingAllowed()) {
				int oldRolloverRessource = rolloverRessource;
				rolloverRessource = -1;
				rolloverRessourceUpdated(oldRolloverRessource,
						rolloverRessource);
			}
		}

		private void swapCursor() {
			Cursor tmp = header.getCursor();
			header.setCursor(otherCursor);
			otherCursor = tmp;
		}

		public void mouseMoved(MouseEvent e) {
			if (canResize(getResizingRessource(e.getPoint()), header) != (header
					.getCursor() == resizeCursor)) {
				swapCursor();
			}
			updateRolloverRessource(e);
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();

			TimelineRessource resizingRessource = header.getResizingRessource();
			TimelineRessource draggedRessource = header.getDraggedRessource();

			if (resizingRessource != null) {
				int oldHeight = resizingRessource.getHeight();
				int newHeight = mouseY - mouseYOffset;
				mouseYOffset += changeRessourceHeight(resizingRessource,
						header, oldHeight, newHeight);
			} else if (draggedRessource != null) {
				TimelineRessourceModel rm = header.getRessourceModel();
				int draggedDistance = mouseY - mouseYOffset;
				int direction = (draggedDistance < 0) ? -1 : 1;
				int ressourceIndex = viewIndexForRessource(draggedRessource);
				int newRessourceIndex = ressourceIndex + direction;

				if (0 <= newRessourceIndex
						&& newRessourceIndex < rm.getRessourceCount()) {
					int height = rm.getRessource(newRessourceIndex).getHeight();
					if (Math.abs(draggedDistance) > (height / 2)) {
						// JTimeline timeline = header.getTimeline();

						mouseYOffset = mouseYOffset + direction * height;
						header.setDraggedDistance(draggedDistance - direction
								* height);

						// Cache the selected column.
						// int selectedIndex = timeline
						// .convertRessourceIndexToModel(getSelectedRessourceIndex());

						rm.moveRessource(ressourceIndex, newRessourceIndex);
						selectRessource(newRessourceIndex);
						// timeline.convertRessourceIndexToView(selectedIndex));
						return;
					}
				}
				setDraggedDistance(draggedDistance, ressourceIndex);
			}

			updateRolloverRessource(e);
		}

		public void mouseReleased(MouseEvent e) {
			setDraggedDistance(0, viewIndexForRessource(header
					.getDraggedRessource()));

			header.setResizingRessource(null);
			header.setDraggedRessource(null);

			updateRolloverRessource(e);
		}

		public void mouseEntered(MouseEvent e) {
			updateRolloverRessource(e);
		}

		public void mouseExited(MouseEvent e) {
			int oldRolloverRessource = rolloverRessource;
			rolloverRessource = -1;
			rolloverRessourceUpdated(oldRolloverRessource, rolloverRessource);
		}

		private void setDraggedDistance(int draggedDistance, int ressource) {
			header.setDraggedDistance(draggedDistance);
			if (ressource != -1) {
				header.getRessourceModel().moveRessource(ressource, ressource);
			}
		}
	}

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicTimelineRessourceHeaderUI();
	}

	public void installUI(JComponent c) {
		header = (JTimelineRessourceHeader) c;

		rendererPane = new CellRendererPane();
		header.add(rendererPane);

		installDefaults();
		installListeners();
	}

	protected void installDefaults() {
		/*
		 * LookAndFeel.installColorsAndFont(header, "TableHeader.background",
		 * "TableHeader.foreground", "TableHeader.font");
		 * LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);
		 */
	}

	protected void installListeners() {
		mouseInputListener = createMouseInputListener();

		header.addMouseListener(mouseInputListener);
		header.addMouseMotionListener(mouseInputListener);
		// header.addFocusListener(focusListener);
	}

	public void uninstallUI(JComponent c) {
		uninstallDefaults();
		uninstallListeners();

		header.remove(rendererPane);
		rendererPane = null;
		header = null;
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
		header.removeMouseListener(mouseInputListener);
		header.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	protected int getRolloverRessource() {
		return rolloverRessource;
	}

	protected void rolloverRessourceUpdated(int oldColumn, int newColumn) {
	}

	private void updateRolloverRessource(MouseEvent e) {
		if (header.getDraggedRessource() == null
				&& header.contains(e.getPoint())) {

			int col = header.ressourceAtPoint(e.getPoint());
			if (col != rolloverRessource) {
				int oldRolloverColumn = rolloverRessource;
				rolloverRessource = col;
				rolloverRessourceUpdated(oldRolloverColumn, rolloverRessource);
			}
		}
	}

	void selectRessource(int newResIndex) {
		Rectangle repaintRect = header.getHeaderRect(selectedRessourceIndex);
		header.repaint(repaintRect);
		selectedRessourceIndex = newResIndex;
		repaintRect = header.getHeaderRect(newResIndex);
		header.repaint(repaintRect);

		scrollToRessource(newResIndex);
		return;
	}

	/**
	 * Used by selectColumn to scroll horizontally, if necessary, to ensure that
	 * the newly selected column is visible.
	 */
	private void scrollToRessource(int res) {
		Container container;
		JTimeline timeline;

		// Test whether the header is in a scroll pane and has a table.
		if ((header.getParent() == null)
				|| ((container = header.getParent().getParent()) == null)
				|| !(container instanceof JScrollPane)
				|| ((timeline = header.getTimeline()) == null)) {
			return;
		}

		// Now scroll, if necessary.
		Rectangle vis = timeline.getVisibleRect();
		Rectangle cellBounds = timeline.getCellRect(0, res, true);
		vis.y = cellBounds.y;
		vis.height = cellBounds.height;
		timeline.scrollRectToVisible(vis);
	}

	private int getSelectedRessourceIndex() {
		int numCols = header.getRessourceModel().getRessourceCount();
		if (selectedRessourceIndex >= numCols && numCols > 0) {
			selectedRessourceIndex = numCols - 1;
		}
		return selectedRessourceIndex;
	}

	private static boolean canResize(TimelineRessource ressource,
			JTimelineRessourceHeader header) {
		return (ressource != null) && header.getResizingAllowed()
				&& ressource.getResizable();
	}

	private int changeRessourceHeight(TimelineRessource resizingRessource,
			JTimelineRessourceHeader th, int oldHeight, int newHeight) {
		resizingRessource.setHeight(newHeight);

		Container container;
		JTimeline timeline;

		if ((th.getParent() == null)
				|| ((container = th.getParent().getParent()) == null)
				|| !(container instanceof JScrollPane)
				|| ((timeline = th.getTimeline()) == null)) {
			return 0;
		}

		if (!container.getComponentOrientation().isLeftToRight()
				&& !th.getComponentOrientation().isLeftToRight()) {
			JViewport viewport = ((JScrollPane) container).getViewport();
			int viewportHeight = viewport.getHeight();
			int diff = newHeight - oldHeight;
			int newHeaderHeight = timeline.getHeight() + diff;

			/* Resize a table */
			Dimension tableSize = timeline.getSize();
			tableSize.height += diff;
			timeline.setSize(tableSize);

			/*
			 * If this table is in AUTO_RESIZE_OFF mode and has a horizontal
			 * scrollbar, we need to update a view's position.
			 */
			if ((newHeaderHeight >= viewportHeight)
					&& (timeline.getAutoResizeMode() == JTimeline.AUTO_RESIZE_OFF)) {
				Point p = viewport.getViewPosition();
				p.y = Math.max(0, Math.min(newHeaderHeight - viewportHeight,
						p.y + diff));
				viewport.setViewPosition(p);
				return diff;
			}

		}
		return 0;
	}

	public void paint(Graphics g, JComponent c) {
		if (header.getRessourceModel().getRessourceCount() <= 0) {
			return;
		}

		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x, clip.y + clip.height - 1);
		TimelineRessourceModel rm = header.getRessourceModel();
		int cMin = header.ressourceAtPoint(left);
		int cMax = header.ressourceAtPoint(right);

		if (cMin == -1) {
			cMin = 0;
		}

		if (cMax == -1) {
			cMax = rm.getRessourceCount() - 1;
		}

		TimelineRessource draggedRessource = header.getDraggedRessource();
		int ressourceHeight;
		Rectangle cellRect = header.getHeaderRect(cMin);
		TimelineRessource aRessource;
		for (int ressource = cMin; ressource <= cMax; ressource++) {
			aRessource = rm.getRessource(ressource);
			ressourceHeight = aRessource.getHeight();
			cellRect.height = ressourceHeight;
			if (aRessource != draggedRessource) {
				paintCell(g, cellRect, ressource);
			}
			cellRect.y += ressourceHeight;
		}

		// Paint the dragged column if we are dragging.
		if (draggedRessource != null) {
			int draggedRessourceIndex = viewIndexForRessource(draggedRessource);
			Rectangle draggedCellRect = header
					.getHeaderRect(draggedRessourceIndex);

			// Draw a gray well in place of the moving column.
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			draggedCellRect.y += header.getDraggedDistance();

			// Fill the background.
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			paintCell(g, draggedCellRect, draggedRessourceIndex);
		}

		rendererPane.removeAll();
	}

	private Component getHeaderRenderer(int ressourceIndex) {
		TimelineRessource aRessource = header.getRessourceModel().getRessource(
				ressourceIndex);
		TimelineRessourceRenderer renderer = aRessource.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}

		boolean selected = false;
		JTimeline timeline = header.getTimeline();
		if (timeline.getSelectedTimeline() != null) {
			int r = timeline.getSelectedTimeline().getRessource();
			selected = r == ressourceIndex;
		}

		boolean hasFocus = !header.isPaintingForPrint()
				&& (ressourceIndex == getSelectedRessourceIndex())
				&& header.hasFocus();
		return renderer.getTimelineRessourceRendererComponent(header
				.getTimeline(), aRessource, ressourceIndex, selected, hasFocus);
	}

	private void paintCell(Graphics g, Rectangle cellRect, int ressourceIndex) {
		Component component = getHeaderRenderer(ressourceIndex);
		rendererPane.paintComponent(g, component, header, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private int viewIndexForRessource(TimelineRessource aRessource) {
		TimelineRessourceModel rm = header.getRessourceModel();
		for (int ressource = 0; ressource < rm.getRessourceCount(); ressource++) {
			if (rm.getRessource(ressource) == aRessource) {
				return ressource;
			}
		}
		return -1;
	}

	private int getHeaderWidth() {
		int width = 0;
		boolean accomodatedDefault = false;
		TimelineRessourceModel ressourceModel = header.getRessourceModel();
		for (int ressource = 0; ressource < ressourceModel.getRessourceCount(); ressource++) {
			TimelineRessource aRessource = ressourceModel
					.getRessource(ressource);
			boolean isDefault = (aRessource.getHeaderRenderer() == null);

			if (!isDefault || !accomodatedDefault) {
				Component comp = getHeaderRenderer(ressource);
				int rendererWidth = comp.getPreferredSize().width + 32;
				width = Math.max(width, rendererWidth);

				if (isDefault && rendererWidth > 0) {
					Object headerValue = aRessource.getHeaderValue();
					if (headerValue != null) {
						headerValue = headerValue.toString();

						if (headerValue != null && !headerValue.equals("")) { //$NON-NLS-1$
							accomodatedDefault = true;
						}
					}
				}
			}
		}
		return width;
	}

	private Dimension createHeaderSize(long height) {
		if (height > Integer.MAX_VALUE) {
			height = Integer.MAX_VALUE;
		}
		return new Dimension(getHeaderWidth(), (int) height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = header.getRessourceModel()
				.getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getMinHeight();
		}
		return createHeaderSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = header.getRessourceModel()
				.getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getPreferredHeight();
		}
		return createHeaderSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = header.getRessourceModel()
				.getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getMaxHeight();
		}
		return createHeaderSize(height);
	}
}
