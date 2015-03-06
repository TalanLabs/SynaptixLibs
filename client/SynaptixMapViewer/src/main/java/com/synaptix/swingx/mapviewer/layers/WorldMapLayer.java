package com.synaptix.swingx.mapviewer.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PickedObject;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

/**
 * Calque qui affiche une mini map en haut à droite. On peut cliquer dessus pour
 * centrer ailleurs
 * 
 * @author Gaby
 * 
 */
public class WorldMapLayer extends AbstractLayer implements SelectListener {

	private JXMapViewer mapViewer;

	private Image earthMapImage;

	private Color borderColor = Color.white;

	private Color backgroundColor = new Color(0f, 0f, 0f, 0.4f);

	private int miniMapWidth = 256;

	private int miniMapHeight = 128;

	private int leftBorder = 10;

	private int topBorder = 10;

	private int sizeCross = 10;

	public WorldMapLayer(JXMapViewer mapViewer) {
		super();

		this.mapViewer = mapViewer;

		this.setAntialiasing(true);
		this.setDirty(true);
		this.setCacheable(false);

		this.setPickEnabled(true);

		Image img = new ImageIcon(
				WorldMapLayer.class.getResource("earth-map-512x256.png"))
				.getImage();
		BufferedImage bi = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.6f));
		g2.drawImage(img, 0, 0, null);
		g2.dispose();
		earthMapImage = bi;

		mapViewer.addSelectListener(this);
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
		int x = dc.getDrawableWidth() - miniMapWidth - leftBorder;
		int y = topBorder;
		if (point.x >= x && point.x < x + miniMapWidth && point.y >= y
				&& point.y < y + miniMapHeight) {
			PickedObject pickedObject = new PickedObject(point, this);
			pickedObject.setLayer(this);
			dc.addPickedObject(pickedObject);
		}
	}

	@Override
	public void selected(SelectEvent event) {
		PickedObject po = event.getPickedObjectList().getTopPickedObject();
		if (po != null && po.getLayer() == this) {
			if (event.isLeftPress() || event.isDrag()) {
				Point p = event.getMouseEvent().getPoint();
				int px = p.x
						- (mapViewer.getWidth() - miniMapWidth - leftBorder);
				int py = p.y - topBorder;
				double longitude = ((px * 360) / (double) miniMapWidth) - 180;
				double latitude = (((miniMapHeight - (py - 1)) * 180) / (double) miniMapHeight) - 90;
				mapViewer
						.setCenterPosition(new GeoPosition(latitude, longitude));

				event.consume();
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			int px = e.getX()
					- (mapViewer.getWidth() - miniMapWidth - leftBorder);
			int py = e.getY() - topBorder;
			double longitude = ((px * 360) / (double) miniMapWidth) - 180;
			double latitude = (((miniMapHeight - (py - 1)) * 180) / (double) miniMapHeight) - 90;
			mapViewer.setCenterPosition(new GeoPosition(latitude, longitude));
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		Graphics2D g2 = (Graphics2D) g.create(dc.getDrawableWidth()
				- miniMapWidth - leftBorder, topBorder, miniMapWidth,
				miniMapHeight);

		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, miniMapWidth, miniMapHeight);
		g2.drawImage(earthMapImage, 0, 0, miniMapWidth, miniMapHeight, null);

		GeoPosition centerPosition = dc.getCenterPosition();
		if (centerPosition != null) {
			int x = (int) (miniMapWidth * (centerPosition.getLongitude() + 180) / 360);
			int y = (int) (miniMapHeight
					- (miniMapHeight * (centerPosition.getLatitude() + 90) / 180) + 1);

			g2.setColor(borderColor);
			g2.drawLine(x - sizeCross, y, x + sizeCross + 1, y);
			g2.drawLine(x, y - sizeCross, x, y + sizeCross + 1);
		}

		Rectangle viewBounds = dc.getViewportBounds();
		if (viewBounds != null) {
			TileFactory tf = dc.getTileFactory();
			GeoPosition gp1 = tf.pixelToGeo(
					new Point2D.Double(viewBounds.getMinX(), viewBounds
							.getMinY()), dc.getRealZoom());

			GeoPosition gp2 = tf.pixelToGeo(
					new Point2D.Double(viewBounds.getMaxX(), viewBounds
							.getMaxY()), dc.getRealZoom());

			int x1 = (int) (miniMapWidth * (gp1.getLongitude() + 180) / 360);
			int y1 = (int) (miniMapHeight
					- (miniMapHeight * (gp1.getLatitude() + 90) / 180) + 1);

			int x2 = (int) (miniMapWidth * (gp2.getLongitude() + 180) / 360);
			int y2 = (int) (miniMapHeight
					- (miniMapHeight * (gp2.getLatitude() + 90) / 180) + 1);

			g2.setColor(borderColor);
			g2.drawLine(x1, y1, x1, y2);
			g2.drawLine(x1, y2, x2, y2);
			g2.drawLine(x2, y2, x2, y1);
			g2.drawLine(x2, y1, x1, y1);

		}

		g2.setColor(borderColor);
		g2.drawRect(0, 0, miniMapWidth - 1, miniMapHeight - 1);

		g2.dispose();

		if (isPickEnabled() && dc.isPickingMode()) {
			doPick(dc, dc.getPickPoint());
		}
	}
}
