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
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.plaf.SimpleTimelineResourceHeaderUI;
import com.synaptix.swing.simpletimeline.JSimpleTimelineResourcesHeader;
import com.synaptix.swing.simpletimeline.SimpleTimelineResource;
import com.synaptix.swing.simpletimeline.SimpleTimelineResourceRenderer;
import com.synaptix.swing.simpletimeline.SimpleTimelineResourcesModel;
import com.synaptix.swing.simpletimeline.SimpleTimelineSelectionModel;

public class BasicSimpleTimelineResourceHeaderUI extends
		SimpleTimelineResourceHeaderUI {

	private static Cursor resizeCursor = Cursor
			.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	protected JSimpleTimelineResourcesHeader header;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	private int rolloverResource = -1;

	private int selectedResourceIndex = 0;

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicSimpleTimelineResourceHeaderUI();
	}

	public void installUI(JComponent c) {
		header = (JSimpleTimelineResourcesHeader) c;

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

	protected int getRolloverResource() {
		return rolloverResource;
	}

	protected void rolloverResourceUpdated(int oldColumn, int newColumn) {
	}

	private void updateRolloverResource(MouseEvent e) {
		if (header.getDraggedResource() == null
				&& header.contains(e.getPoint())) {

			int col = header.resourceAtPoint(e.getPoint());
			if (col != rolloverResource) {
				int oldRolloverColumn = rolloverResource;
				rolloverResource = col;
				rolloverResourceUpdated(oldRolloverColumn, rolloverResource);
			}
		}
	}

	void selectResource(int newResIndex) {
		Rectangle repaintRect = header.getHeaderRect(selectedResourceIndex);
		header.repaint(repaintRect);
		selectedResourceIndex = newResIndex;
		repaintRect = header.getHeaderRect(newResIndex);
		header.repaint(repaintRect);

		scrollToResource(newResIndex);
		return;
	}

	/**
	 * Used by selectColumn to scroll horizontally, if necessary, to ensure that
	 * the newly selected column is visible.
	 */
	private void scrollToResource(int res) {
		JSimpleTimeline simpleTimeline;

		// Test whether the header is in a scroll pane and has a table.
		if ((header.getParent() == null)
				|| ((header.getParent().getParent()) == null)
				|| ((simpleTimeline = header.getSimpleTimeline()) == null)) {
			return;
		}

		// Now scroll, if necessary.
		Rectangle vis = header.getVisibleRect();
		Rectangle cellBounds = header.getHeaderRect(res);
		vis.y = cellBounds.y;
		vis.height = cellBounds.height;
		simpleTimeline.scrollRectToVisible(vis);
	}

	private int getSelectedResourceIndex() {
		int numCols = header.getResourceModel().getResourceCount();
		if (selectedResourceIndex >= numCols && numCols > 0) {
			selectedResourceIndex = numCols - 1;
		}
		return selectedResourceIndex;
	}

	private static boolean canResize(SimpleTimelineResource resource,
			JSimpleTimelineResourcesHeader header) {
		return (resource != null) && header.getResizingAllowed()
				&& resource.getResizable();
	}

	private int changeResourceHeight(SimpleTimelineResource resizingResource,
			JSimpleTimelineResourcesHeader th, int oldHeight, int newHeight) {
		resizingResource.setHeight(newHeight);

		Container container;
		JSimpleTimeline simpleTimeline;

		if ((th.getParent() == null)
				|| ((container = th.getParent().getParent()) == null)
				|| !(container instanceof JScrollPane)
				|| ((simpleTimeline = th.getSimpleTimeline()) == null)) {
			return 0;
		}

		if (!container.getComponentOrientation().isLeftToRight()
				&& !th.getComponentOrientation().isLeftToRight()) {
			JViewport viewport = ((JScrollPane) container).getViewport();
			int viewportHeight = viewport.getHeight();
			int diff = newHeight - oldHeight;
			int newHeaderHeight = simpleTimeline.getHeight() + diff;

			/* Resize a table */
			Dimension tableSize = simpleTimeline.getSize();
			tableSize.height += diff;
			simpleTimeline.setSize(tableSize);

			/*
			 * If this table is in AUTO_RESIZE_OFF mode and has a horizontal
			 * scrollbar, we need to update a view's position.
			 */
			if (newHeaderHeight >= viewportHeight) {
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
		if (header.getResourceModel().getResourceCount() <= 0) {
			return;
		}

		Rectangle clip = g.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x, clip.y + clip.height - 1);
		SimpleTimelineResourcesModel rm = header.getResourceModel();
		int cMin = header.resourceAtPoint(left);
		int cMax = header.resourceAtPoint(right);

		if (cMin == -1) {
			cMin = 0;
		}

		if (cMax == -1) {
			cMax = rm.getResourceCount() - 1;
		}

		SimpleTimelineResource draggedResource = header.getDraggedResource();
		int resourceHeight;
		Rectangle cellRect = header.getHeaderRect(cMin);
		SimpleTimelineResource aResource;
		for (int resource = cMin; resource <= cMax; resource++) {
			aResource = rm.getResource(resource);
			resourceHeight = aResource.getHeight();
			cellRect.height = resourceHeight;
			if (aResource != draggedResource) {
				paintCell(g, cellRect, resource, false);
			}
			cellRect.y += resourceHeight;
		}

		// Paint the dragged column if we are dragging.
		if (draggedResource != null) {
			int draggedResourceIndex = viewIndexForResource(draggedResource);
			Rectangle draggedCellRect = header
					.getHeaderRect(draggedResourceIndex);

			// Draw a gray well in place of the moving column.
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			draggedCellRect.y += header.getDraggedDistance();

			// Fill the background.
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);

			paintCell(g, draggedCellRect, draggedResourceIndex, true);
		}

		rendererPane.removeAll();
	}

	private Component getHeaderRenderer(int resourceIndex, boolean isDrag) {
		SimpleTimelineResource aResource = header.getResourceModel()
				.getResource(resourceIndex);
		SimpleTimelineResourceRenderer renderer = aResource.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}

		JSimpleTimeline simpleTimeline = header.getSimpleTimeline();
		boolean selected = simpleTimeline.getSelectionModel()
				.isSelectedIndexResource(aResource.getModelIndex());

		boolean hasFocus = !header.isPaintingForPrint()
				&& (resourceIndex == getSelectedResourceIndex())
				&& header.hasFocus();
		return renderer.getSimpleTimelineResourceRendererComponent(
				simpleTimeline, aResource, resourceIndex, selected, hasFocus,
				isDrag);
	}

	private void paintCell(Graphics g, Rectangle cellRect, int resourceIndex,
			boolean isDrag) {
		Component component = getHeaderRenderer(resourceIndex, isDrag);
		rendererPane.paintComponent(g, component, header, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	private int viewIndexForResource(SimpleTimelineResource aResource) {
		SimpleTimelineResourcesModel rm = header.getResourceModel();
		for (int resource = 0; resource < rm.getResourceCount(); resource++) {
			if (rm.getResource(resource) == aResource) {
				return resource;
			}
		}
		return -1;
	}

	private int getHeaderWidth() {
		int width = 0;
		boolean accomodatedDefault = false;
		SimpleTimelineResourcesModel resourceModel = header.getResourceModel();
		for (int resource = 0; resource < resourceModel.getResourceCount(); resource++) {
			SimpleTimelineResource aResource = resourceModel
					.getResource(resource);
			boolean isDefault = (aResource.getHeaderRenderer() == null);

			if (!isDefault || !accomodatedDefault) {
				Component comp = getHeaderRenderer(resource, false);
				int rendererWidth = comp.getPreferredSize().width + 32;
				width = Math.max(width, rendererWidth);

				if (isDefault && rendererWidth > 0) {
					Object headerValue = aResource.getHeaderValue();
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
		Enumeration<SimpleTimelineResource> enumeration = header
				.getResourceModel().getResources();
		while (enumeration.hasMoreElements()) {
			SimpleTimelineResource aResource = enumeration.nextElement();
			height = height + aResource.getMinHeight();
		}
		return createHeaderSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = 0;
		Enumeration<SimpleTimelineResource> enumeration = header
				.getResourceModel().getResources();
		while (enumeration.hasMoreElements()) {
			SimpleTimelineResource aResource = enumeration.nextElement();
			height = height + aResource.getPreferredHeight();
		}
		return createHeaderSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = 0;
		Enumeration<SimpleTimelineResource> enumeration = header
				.getResourceModel().getResources();
		while (enumeration.hasMoreElements()) {
			SimpleTimelineResource aResource = enumeration.nextElement();
			height = height + aResource.getMaxHeight();
		}
		return createHeaderSize(height);
	}

	private final class MouseInputHandler implements MouseInputListener {

		private int mouseYOffset;

		private Cursor otherCursor = resizeCursor;

		public void mouseClicked(MouseEvent e) {
			// if (e.getClickCount() == 1 &&
			// SwingUtilities.isLeftMouseButton(e)) {
			// Point p = e.getPoint();
			//
			// SimpleTimelineResourcesModel resourceModel = header
			// .getResourceModel();
			// int index = header.resourceAtPoint(p);
			//
			// if (index != -1) {
			// SimpleTimelineResource hitResource = resourceModel
			// .getResource(index);
			//
			// SimpleTimelineSelectionModel selectionModel = header
			// .getSimpleTimeline().getSelectionModel();
			//
			// if (e.isControlDown()) {
			// if (selectionModel.isSelectedIndexResource(hitResource
			// .getModelIndex())) {
			// selectionModel
			// .removeSelectionIndexResource(hitResource
			// .getModelIndex());
			// } else {
			// selectionModel
			// .addSelectionIndexResource(hitResource
			// .getModelIndex());
			// }
			// } else {
			// selectionModel.setSelectionIndexResource(hitResource
			// .getModelIndex());
			// }
			// }
			// }
		}

		private SimpleTimelineResource getResizingResource(Point p) {
			return getResizingResource(p, header.resourceAtPoint(p));
		}

		private SimpleTimelineResource getResizingResource(Point p, int resource) {
			if (resource == -1) {
				return null;
			}
			Rectangle r = header.getHeaderRect(resource);
			r.grow(0, -3);
			if (r.contains(p)) {
				return null;
			}
			int midPoint = r.y + r.height / 2;
			int resourceIndex;
			resourceIndex = (p.y < midPoint) ? resource - 1 : resource;

			if (resourceIndex == -1) {
				return null;
			}
			return header.getResourceModel().getResource(resourceIndex);
		}

		public void mousePressed(MouseEvent e) {
			header.setDraggedResource(null);
			header.setResizingResource(null);
			header.setDraggedDistance(0);

			Point p = e.getPoint();

			// First find which header cell was hit
			SimpleTimelineResourcesModel resourceModel = header
					.getResourceModel();
			int index = header.resourceAtPoint(p);

			if (index != -1) {
				// The last 3 pixels + 3 pixels of next column are for resizing
				SimpleTimelineResource resizingResource = getResizingResource(
						p, index);
				if (canResize(resizingResource, header)) {
					header.setResizingResource(resizingResource);
					mouseYOffset = p.y - resizingResource.getHeight();
				} else if (header.getReorderingAllowed()) {
					SimpleTimelineResource hitResource = resourceModel
							.getResource(index);

					SimpleTimelineSelectionModel selectionModel = header
							.getSimpleTimeline().getSelectionModel();

					if (e.isControlDown()) {
						if (selectionModel.isSelectedIndexResource(hitResource
								.getModelIndex())) {
							selectionModel
									.removeSelectionIndexResource(hitResource
											.getModelIndex());
						} else {
							selectionModel
									.addSelectionIndexResource(hitResource
											.getModelIndex());
						}
					} else {
						selectionModel.setSelectionIndexResource(hitResource
								.getModelIndex());
					}

					header.setDraggedResource(hitResource);
					mouseYOffset = p.y;
				}
			}

			if (header.getReorderingAllowed()) {
				int oldRolloverResource = rolloverResource;
				rolloverResource = -1;
				rolloverResourceUpdated(oldRolloverResource, rolloverResource);
			}
		}

		private void swapCursor() {
			Cursor tmp = header.getCursor();
			header.setCursor(otherCursor);
			otherCursor = tmp;
		}

		public void mouseMoved(MouseEvent e) {
			if (canResize(getResizingResource(e.getPoint()), header) != (header
					.getCursor() == resizeCursor)) {
				swapCursor();
			}
			updateRolloverResource(e);
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();

			SimpleTimelineResource resizingResource = header
					.getResizingResource();
			SimpleTimelineResource draggedResource = header
					.getDraggedResource();

			if (resizingResource != null) {
				int oldHeight = resizingResource.getHeight();
				int newHeight = mouseY - mouseYOffset;
				mouseYOffset += changeResourceHeight(resizingResource, header,
						oldHeight, newHeight);
			} else if (draggedResource != null) {
				SimpleTimelineResourcesModel rm = header.getResourceModel();
				int draggedDistance = mouseY - mouseYOffset;
				int direction = (draggedDistance < 0) ? -1 : 1;
				int resourceIndex = viewIndexForResource(draggedResource);
				int newResourceIndex = resourceIndex + direction;

				if (0 <= newResourceIndex
						&& newResourceIndex < rm.getResourceCount()) {
					int height = rm.getResource(newResourceIndex).getHeight();
					if (Math.abs(draggedDistance) > (height / 2)) {
						// JTimeline timeline = header.getTimeline();

						mouseYOffset = mouseYOffset + direction * height;
						header.setDraggedDistance(draggedDistance - direction
								* height);

						// Cache the selected column.
						// int selectedIndex = timeline
						// .convertResourceIndexToModel(getSelectedResourceIndex
						// ());

						rm.moveResource(resourceIndex, newResourceIndex);
						selectResource(newResourceIndex);
						// timeline.convertResourceIndexToView(selectedIndex));
						return;
					}
				}
				setDraggedDistance(draggedDistance, resourceIndex);
			}

			updateRolloverResource(e);
		}

		public void mouseReleased(MouseEvent e) {
			setDraggedDistance(0, viewIndexForResource(header
					.getDraggedResource()));

			header.setResizingResource(null);
			header.setDraggedResource(null);

			updateRolloverResource(e);
		}

		public void mouseEntered(MouseEvent e) {
			updateRolloverResource(e);
		}

		public void mouseExited(MouseEvent e) {
			int oldRolloverResource = rolloverResource;
			rolloverResource = -1;
			rolloverResourceUpdated(oldRolloverResource, rolloverResource);
		}

		private void setDraggedDistance(int draggedDistance, int resource) {
			header.setDraggedDistance(draggedDistance);
			if (resource != -1) {
				header.getResourceModel().moveResource(resource, resource);
			}
		}
	}
}
