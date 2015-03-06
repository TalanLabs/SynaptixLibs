package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class SynaptixTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -8917685934109973826L;

	private static List<DragDropComponent> ddcs;

	private static SwingDragGestureRecognizer recognizer = null;

	static {
		ddcs = new ArrayList<DragDropComponent>();
	}

	public static void addDragDropComponent(DragDropComponent ddc) {
		synchronized (ddcs) {
			if (!ddcs.contains(ddc)) {
				ddcs.add(ddc);
			}
		}
	}

	public static void removeDragDropComponent(DragDropComponent ddc) {
		synchronized (ddcs) {
			if (ddcs.contains(ddc)) {
				ddcs.remove(ddc);
			}
		}
	}

	private static void clearDropDDCs() {
		synchronized (ddcs) {
			for (int i = ddcs.size() - 1; i >= 0; i--) {
				DragDropComponent ddc = ddcs.get(i);
				ddc.clearDrop();
			}
		}
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		int srcActions = getSourceActions(comp);

		if (!(e instanceof MouseEvent)
				|| !(action == COPY || action == MOVE || action == LINK)
				|| (srcActions & action) == 0) {
			action = NONE;
		}

		if (action != NONE && !GraphicsEnvironment.isHeadless()) {
			if (recognizer == null) {
				recognizer = new SwingDragGestureRecognizer(new DragHandler());
			}
			recognizer.gestured(comp, (MouseEvent) e, srcActions, action);
		} else {
			exportDone(comp, null, NONE);
		}
	}

	private static class DragHandler implements DragGestureListener,
			DragSourceListener {

		private boolean scrolls;

		private Component currentComponent;

		public void dragGestureRecognized(DragGestureEvent dge) {
			JComponent c = (JComponent) dge.getComponent();
			SynaptixTransferHandler th = (SynaptixTransferHandler) c
					.getTransferHandler();
			Transferable t = th.createTransferable(c);
			if (t != null) {
				scrolls = c.getAutoscrolls();
				c.setAutoscrolls(false);
				try {
					Image img = null;
					Icon icn = th.getVisualRepresentation(t);
					if (icn != null) {
						if (icn instanceof ImageIcon) {
							img = ((ImageIcon) icn).getImage();
						} else {
							img = new BufferedImage(icn.getIconWidth(), icn
									.getIconWidth(),
									BufferedImage.TYPE_4BYTE_ABGR);
							Graphics g = img.getGraphics();
							icn.paintIcon(c, g, 0, 0);
						}
					}

					if (img == null) {
						dge.startDrag(null, t, this);
					} else {
						dge.startDrag(null, img, new Point(0, -img
								.getHeight(null)), t, this);
					}
					return;
				} catch (RuntimeException re) {
					c.setAutoscrolls(scrolls);
				}
			}

			th.exportDone(c, t, NONE);
		}

		public void dragEnter(DragSourceDragEvent dsde) {
			System.out.println("ici dragEnter");
		}

		public void dragOver(DragSourceDragEvent dsde) {
			// Point p = SwingUtilities.convertPoint(dsde.getDragSourceContext()
			// .getComponent(), dsde.getLocation(), SwingUtilities
			// .getRoot(dsde.getDragSourceContext().getComponent()));
			// currentComponent = SwingUtilities.getDeepestComponentAt(
			// SwingUtilities.getRoot(dsde.getDragSourceContext()
			// .getComponent()), p.x, p.y);
			// System.out.println(currentComponent);

			// System.out.println("ici dragOver");
		}

		public void dragExit(DragSourceEvent dsde) {
			System.out.println("ici dragExit " + currentComponent);
		}

		public void dragDropEnd(DragSourceDropEvent dsde) {
			DragSourceContext dsc = dsde.getDragSourceContext();
			JComponent c = (JComponent) dsc.getComponent();
			SynaptixTransferHandler th = (SynaptixTransferHandler) c
					.getTransferHandler();

			clearDropDDCs();

			if (dsde.getDropSuccess()) {
				th.exportDone(c, dsc.getTransferable(), dsde.getDropAction());
			} else {
				th.exportDone(c, dsc.getTransferable(), NONE);
			}
			c.setAutoscrolls(scrolls);
		}

		public void dropActionChanged(DragSourceDragEvent dsde) {
		}
	}

	private static class SwingDragGestureRecognizer extends
			DragGestureRecognizer {

		private static final long serialVersionUID = -8743681005525165123L;

		SwingDragGestureRecognizer(DragGestureListener dgl) {
			super(DragSource.getDefaultDragSource(), null, NONE, dgl);
		}

		void gestured(JComponent c, MouseEvent e, int srcActions, int action) {
			setComponent(c);
			setSourceActions(srcActions);
			appendEvent(e);
			fireDragGestureRecognized(action, e.getPoint());
		}

		protected void registerListeners() {
		}

		protected void unregisterListeners() {
		}
	}
}
