package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.PickedObject;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

/**
 * Calque qui affiche la longitude et latitude du centre et le zoom
 * 
 * @author Gaby
 * 
 */
public class ViewControlsLayer extends AbstractLayer implements SelectListener,
		PropertyChangeListener {

	private static final int DEFAULT_TIMER_DELAY = 250;

	private static final ImageIcon ZOOM_IN_IMAGE_ICON;

	private static final ImageIcon ZOOM_IN_PRESSED_IMAGE_ICON;

	private static final ImageIcon ZOOM_IN_OVER_IMAGE_ICON;

	private static final ImageIcon ZOOM_OUT_IMAGE_ICON;

	private static final ImageIcon ZOOM_OUT_PRESSED_IMAGE_ICON;

	private static final ImageIcon ZOOM_OUT_OVER_IMAGE_ICON;

	static {
		ZOOM_IN_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-in.png"));
		ZOOM_IN_PRESSED_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-in-pressed.png"));
		ZOOM_IN_OVER_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-in-over.png"));

		ZOOM_OUT_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-out.png"));
		ZOOM_OUT_PRESSED_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-out-pressed.png"));
		ZOOM_OUT_OVER_IMAGE_ICON = new ImageIcon(
				ViewControlsLayer.class.getResource("zoom-out-over.png"));
	}

	private JXMapViewer mapViewer;

	private Color backgroundColor = new Color(0.0f, 0.0f, 0.0f, 0.8f);

	private JButton zoomInButton;

	private JButton zoomOutButton;

	private CellRendererPane rendererPane = new CellRendererPane();

	private JButton currentControl = null;

	private JButton pressedControl = null;

	public ViewControlsLayer(JXMapViewer mapViewer) {
		super();

		this.mapViewer = mapViewer;

		this.setAntialiasing(true);
		this.setCacheable(false);

		this.setPickEnabled(true);

		initComponents();

		mapViewer.addSelectListener(this);
		mapViewer.addPropertyChangeListener("zoomEnabled", this);
	}

	private void initComponents() {
		zoomInButton = new JButton();
		zoomOutButton = new JButton();

		zoomInButton.setIcon(ZOOM_IN_IMAGE_ICON);
		zoomInButton.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
		zoomInButton.setBorderPainted(false);
		zoomInButton.setContentAreaFilled(false);
		zoomInButton.setFocusPainted(false);
		zoomInButton.setHorizontalTextPosition(SwingConstants.CENTER);
		zoomInButton.setIconTextGap(0);
		zoomInButton.setMargin(new Insets(0, 0, 0, 0));
		zoomInButton.setPressedIcon(ZOOM_IN_PRESSED_IMAGE_ICON);
		zoomInButton.setRolloverIcon(ZOOM_IN_OVER_IMAGE_ICON);
		rendererPane.add(zoomInButton);

		zoomOutButton.setIcon(ZOOM_OUT_IMAGE_ICON);
		zoomOutButton.setBorder(BorderFactory.createEmptyBorder(2, 1, 3, 1));
		zoomOutButton.setBorderPainted(false);
		zoomOutButton.setContentAreaFilled(false);
		zoomOutButton.setFocusPainted(false);
		zoomOutButton.setHorizontalTextPosition(SwingConstants.CENTER);
		zoomOutButton.setIconTextGap(0);
		zoomOutButton.setMargin(new Insets(0, 0, 0, 0));
		zoomOutButton.setPressedIcon(ZOOM_OUT_PRESSED_IMAGE_ICON);
		zoomOutButton.setRolloverIcon(ZOOM_OUT_OVER_IMAGE_ICON);
		rendererPane.add(zoomOutButton);

	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
		if (point.x >= 10 && point.x < 10 + 29 && point.y >= 10
				&& point.y < 10 + 26) {
			PickedObject pickedObject = new PickedObject(point, zoomInButton);
			pickedObject.setLayer(this);
			dc.addPickedObject(pickedObject);
		}
		if (point.x >= 10 && point.x < 10 + 29 && point.y >= 10 + 26
				&& point.y < 10 + 26 + 27) {
			PickedObject pickedObject = new PickedObject(point, zoomOutButton);
			pickedObject.setLayer(this);
			dc.addPickedObject(pickedObject);
		}
	}

	public Object getHighlightedObject() {
		return this.currentControl;
	}

	public void highlight(JButton control) {
		if (this.currentControl == control) {
			return;
		}

		if (this.currentControl != null) {
			this.currentControl.getModel().setRollover(false);
			this.currentControl.getModel().setArmed(false);
			this.currentControl.getModel().setPressed(false);
			this.currentControl = null;
		}

		if (control != null) {
			this.currentControl = control;
			this.currentControl.getModel().setRollover(true);
		}
	}

	public void selected(SelectEvent event) {
		boolean repaint = false;

		if (getHighlightedObject() != null) {
			highlight(null);
			repaint = true;
		}

		if (event.getMouseEvent() == null
				|| !event.getMouseEvent().isConsumed()) {
			if (event.getTopObject() != null
					&& event.getTopPickedObject().getLayer() == this) {
				JButton button = (JButton) event.getTopObject();
				if (event.isRollover()) {
					highlight(button);
					repaint = true;
				}
				if (event.isDrag()) {
					pressedControl = null;

					highlight(null);
					repaint = true;
					event.consume();
				} else if (event.isLeftPress()) {
					ButtonModel model = button.getModel();
					if (model.isEnabled()) {
						pressedControl = button;
						model.setPressed(true);
						event.consume();
					}
				} else if (event.isLeftClick()) {
					ButtonModel model = button.getModel();
					if (model.isEnabled()) {
						if (pressedControl == zoomInButton) {
							zoomInActionPerformed();
						} else {
							zoomOutActionPerformed();
						}
						model.setPressed(false);
						pressedControl = null;
						event.consume();
					}
				}

				if (pressedControl != null) {
					highlight(button);

					repaint = true;
				}
			}
		}
		if (repaint) {
			mapViewer.repaint();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("zoomEnabled".equals(evt.getPropertyName())) {
			boolean enabled = (Boolean) evt.getNewValue();
			zoomInButton.getModel().setEnabled(enabled);
			zoomOutButton.getModel().setEnabled(enabled);
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setColor(backgroundColor);
		RoundRectangle2D rect = new RoundRectangle2D.Double(10, 10, 29 - 1,
				26 + 27 - 1, 24, 24);
		g2.fill(rect);

		rendererPane.paintComponent(g2, zoomInButton, null, 10, 10, 29, 26);

		rendererPane.paintComponent(g2, zoomOutButton, null, 10, 10 + 26, 29,
				27);

		g2.dispose();

		if (isPickEnabled() && dc.isPickingMode()) {
			doPick(dc, dc.getPickPoint());
		}
	}

	private void zoomInActionPerformed() {
		Animator enterController = PropertySetter.createAnimator(
				DEFAULT_TIMER_DELAY, mapViewer, "realZoom",
				mapViewer.getRealZoom(), mapViewer.getRealZoom() - 1);
		enterController.addTarget(new MyTimingTargetAdapter(mapViewer));
		enterController.start();
	}

	private void zoomOutActionPerformed() {
		Animator enterController = PropertySetter.createAnimator(
				DEFAULT_TIMER_DELAY, mapViewer, "realZoom",
				mapViewer.getRealZoom(), mapViewer.getRealZoom() + 1);
		enterController.addTarget(new MyTimingTargetAdapter(mapViewer));
		enterController.start();
	}

	private final class MyTimingTargetAdapter extends TimingTargetAdapter {

		private JXMapViewer mapViewer;

		public MyTimingTargetAdapter(JXMapViewer mapViewer) {
			super();

			this.mapViewer = mapViewer;
		}

		public void begin() {
			mapViewer.setZoomEnabled(false);
		}

		public void end() {
			mapViewer.setZoomEnabled(true);
		}
	}
}
