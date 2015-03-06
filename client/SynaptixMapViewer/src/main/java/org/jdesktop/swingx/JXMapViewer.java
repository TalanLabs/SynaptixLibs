/*
 * MapViewer.java
 *
 * Created on March 14, 2006, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.DesignMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.mapviewer.DragSelectEvent;
import org.jdesktop.swingx.mapviewer.DrawContextImpl;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.PickedObject;
import org.jdesktop.swingx.mapviewer.PickedObjectList;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;
import org.jdesktop.swingx.mapviewer.Tile;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.cache.BasicMemoryCache;
import org.jdesktop.swingx.mapviewer.cache.MemoryCache;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;
import org.jdesktop.swingx.mapviewer.layers.Layer;
import org.jdesktop.swingx.painter.AbstractPainter;

/**
 * A tile oriented map component that can easily be used with tile sources on
 * the web like Google and Yahoo maps, satellite data such as NASA imagery, and
 * also with file based sources like pre-processed NASA images. A known map
 * provider can be used with the SLMapServerInfo, which will connect to a 2km
 * resolution version of NASA's Blue Marble Next Generation imagery. @see
 * SLMapServerInfo for more information.
 * 
 * Note, the JXMapViewer has three center point properties. The
 * <B>addressLocation</B> property represents an abstract center of the map.
 * This would usually be something like the first item in a search result. It is
 * a {@link GeoPosition}. The <b>centerPosition</b> property represents the
 * current center point of the map. If the user pans the map then the
 * centerPosition point will change but the <B>addressLocation</B> will not.
 * Calling <B>recenterToAddressLocation()</B> will move the map back to that
 * center address. The <B>center</B> property represents the same point as the
 * centerPosition property, but as a Point2D in pixel space instead of a
 * GeoPosition in lat/long space. Note that the center property is a Point2D in
 * the entire world bitmap, not in the portion of the map currently visible. You
 * can use the <B>getViewportBounds()</B> method to find the portion of the map
 * currently visible and adjust your calculations accordingly. Changing the
 * <B>center</B> property will change the <B>centerPosition</B> property and
 * vice versa. All three properties are bound.
 * 
 * @author Joshua.Marinacci@sun.com
 * @see org.jdesktop.swingx.mapviewer.bmng.SLMapServerInfo
 */
public class JXMapViewer extends JXPanel implements DesignMode {

	private static final long serialVersionUID = -3530746298586937321L;

	/**
	 * The zoom level. Generally a value between 1 and 15 (TODO Is this true for
	 * all the mapping worlds? What does this mean if some mapping system
	 * doesn't support the zoom level?
	 */
	private double realZoom = 1;

	/**
	 * The position, in <I>map coordinates</I> of the center point. This is
	 * defined as the distance from the top and left edges of the map in pixels.
	 * Dragging the map component will change the center position. Zooming
	 * in/out will cause the center to be recalculated so as to remain in the
	 * center of the new "map".
	 */
	private Point2D center = new Point2D.Double(0, 0);

	/**
	 * Indicates whether or not to draw the borders between tiles. Defaults to
	 * false.
	 * 
	 * TODO Generally not very nice looking, very much a product of testing
	 * Consider whether this should really be a property or not.
	 */
	private boolean drawTileBorders = false;

	/**
	 * Factory used by this component to grab the tiles necessary for painting
	 * the map.
	 */
	private TileFactory factory;

	/**
	 * The position in latitude/longitude of the "address" being mapped. This is
	 * a special coordinate that, when moved, will cause the map to be moved as
	 * well. It is separate from "center" in that "center" tracks the current
	 * center (in pixels) of the viewport whereas this will not change when
	 * panning or zooming. Whenever the addressLocation is changed, however, the
	 * map will be repositioned.
	 */
	private GeoPosition addressLocation;

	/**
	 * Specifies whether panning is enabled. Panning is being able to click and
	 * drag the map around to cause it to move
	 */
	private boolean panEnabled = true;

	/**
	 * Specifies whether zooming is enabled (the mouse wheel, for example,
	 * zooms)
	 */
	private boolean zoomEnabled = true;

	/**
	 * Indicates whether the component should recenter the map when the "middle"
	 * mouse button is pressed
	 */
	private boolean recenterOnClickEnabled = true;

	private boolean designTime;

	private Image loadingImage;

	private boolean restrictOutsidePanning = false;

	private boolean horizontalWrapped = true;

	private Rectangle tempRect = new Rectangle();

	private List<Layer> layerList;

	private MemoryCache memoryCache = new BasicMemoryCache((long) (16777216L * 0.85), 16777216L);

	// a property change listener which forces repaints when tiles finish
	// loading
	private TileLoadListener tileLoadListener = new TileLoadListener();

	private PickedObjectList pickedObjectList = new PickedObjectList();

	private Point pickPoint;

	private DrawContextImpl dc;

	/**
	 * Create a new JXMapViewer. By default it will use the EmptyTileFactory
	 */
	public JXMapViewer() {
		super();

		this.dc = new DrawContextImpl(this);

		factory = new EmptyTileFactory();
		// setTileFactory(new GoogleTileFactory());
		MouseInputListener mia = new PanMouseInputListener();
		setRecenterOnClickEnabled(false);
		this.addMouseListener(mia);
		this.addMouseMotionListener(mia);
		this.addMouseWheelListener(new ZoomMouseWheelListener());
		this.addKeyListener(new PanKeyListener());

		this.layerList = new ArrayList<Layer>();

		// make a dummy loading image
		try {
			URL url = this.getClass().getResource("mapviewer/resources/loading.png");
			this.setLoadingImage(ImageIO.read(url));
		} catch (Throwable ex) {
			System.out.println("could not load 'loading.png'");
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img.createGraphics();
			g2.setColor(Color.black);
			g2.fillRect(0, 0, 16, 16);
			g2.dispose();
			this.setLoadingImage(img);
		}

		// setAddressLocation(new GeoPosition(37.392137,-121.950431)); // Sun
		// campus

		setBackgroundPainter(new AbstractPainter<JXPanel>() {
			protected void doPaint(Graphics2D g, JXPanel component, int width, int height) {
				doPaintComponent(g);
			}
		});
	}

	// the method that does the actual painting
	private void doPaintComponent(Graphics g) {
		if (isDesignTime()) {

		} else {
			double realZoom = getRealZoom();
			Rectangle viewportBounds = getViewportBounds();
			drawMapTiles(g, realZoom, viewportBounds);
			drawOverlays(g, realZoom, viewportBounds);
		}

		super.paintBorder(g);
	}

	/**
	 * Indicate that the component is being used at design time, such as in a
	 * visual editor like NetBeans' Matisse
	 * 
	 * @param b
	 *            indicates if the component is being used at design time
	 */
	public void setDesignTime(boolean b) {
		this.designTime = b;
	}

	/**
	 * Indicates whether the component is being used at design time, such as in
	 * a visual editor like NetBeans' Matisse
	 * 
	 * @return boolean indicating if the component is being used at design time
	 */
	public boolean isDesignTime() {
		return designTime;
	}

	public void addSelectListener(SelectListener l) {
		listenerList.add(SelectListener.class, l);
	}

	public void removeSelectListener(SelectListener l) {
		listenerList.remove(SelectListener.class, l);
	}

	protected void fireSelected(SelectEvent event) {
		for (SelectListener l : listenerList.getListeners(SelectListener.class)) {
			l.selected(event);
		}
	}

	/**
	 * Draw the map tiles. This method is for implementation use only.
	 * 
	 * @param g
	 *            Graphics
	 * @param zoom
	 *            zoom level to draw at
	 * @param viewportBounds
	 *            the bounds to draw within
	 */
	protected void drawMapTiles(final Graphics g, final double realZoom, Rectangle viewportBounds) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int zoom = (int) Math.floor(realZoom);
		double scale = realZoom - (int) Math.floor(realZoom);
		// Si scale sup à 0.5, on prend le zoom sup et on retrécie
		if (scale >= 0.5) {
			zoom++;
			scale = scale - 1.0f;
		} else {
			scale = scale / 2.0;
		}
		int tileSize = getTileFactory().getTileSize(zoom);

		double realSize = tileSize - tileSize * scale;
		int realSizeImage = (int) Math.ceil(realSize);

		// calculate the "visible" viewport area in tiles
		int numWide = (int) (viewportBounds.width / realSize) + 2;
		int numHigh = (int) (viewportBounds.height / realSize) + 2;

		int tpx = (int) Math.floor(viewportBounds.getX() / realSize);
		int tpy = (int) Math.floor(viewportBounds.getY() / realSize);

		g2.translate(-(int) viewportBounds.getX(), -(int) viewportBounds.getY());

		double oxd = tpx * realSize;
		double oyd;
		for (int x = tpx; x <= numWide + tpx; x++) {
			oyd = tpy * realSize;
			for (int y = tpy; y <= numHigh + tpy; y++) {
				int itpx = x;
				int itpy = y;
				int ox = (int) Math.floor(oxd);
				int oy = (int) Math.floor(oyd);

				// On regarde si on affiche le tile celon le clip du Graphics
				tempRect.setBounds(ox, oy, realSizeImage, realSizeImage);
				if (g2.getClipBounds().intersects(tempRect)) {
					Tile tile = getTileFactory().getTile(itpx, itpy, zoom);
					tile.addUniquePropertyChangeListener("loaded", tileLoadListener); // this
																						// is
																						// a
																						// filthy
																						// hack

					// Si le tile a été chargé on l'affiche
					if (tile.isLoaded()) {
						g2.drawImage(tile.getImage(), ox, oy, realSizeImage, realSizeImage, null);
					} else { // Sinon, on cherche un tile au dessus dans le
								// cache
						boolean loading = true;

						int imageX = 0;
						int imageY = 0;
						if (getLoadingImage() != null) {
							imageX = (int) (realSize - getLoadingImage().getWidth(null)) / 2;
							imageY = (int) (realSize - getLoadingImage().getHeight(null)) / 2;
						}

						// On cherche dans le cache un tile qui peut
						// correspondre
						Tile tile2 = getTileFactory().getCacheTile(itpx, itpy, zoom);
						if (tile2 != null) {
							BufferedImage bi = tile2.getImage();
							if (bi != null) {
								loading = false;
								// le tile a un zoom plus grand que le zoom
								// actuel
								int d = tile2.getZoom() - zoom;
								d = (int) Math.pow(2, d);
								int dtpx = itpx - tile2.getX() * d;
								int dtpy = itpy - tile2.getY() * d;
								int dd = bi.getWidth() / d;
								int sx1 = dtpx * dd;
								int sy1 = dtpy * dd;
								g2.drawImage(bi, ox, oy, ox + realSizeImage, oy + realSizeImage, sx1, sy1, sx1 + dd, sy1 + dd, null);

								if (getLoadingImage() != null) {
									g2.drawImage(getLoadingImage(), ox + imageX, oy + imageY, null);
								}
							}
						}
						if (loading && getLoadingImage() != null) {
							g2.setColor(Color.GRAY);
							g2.fillRect(ox, oy, realSizeImage, realSizeImage);
							g2.drawImage(getLoadingImage(), ox + imageX, oy + imageY, null);
						}
					}
					// Permet d'afficher une aide au tile
					if (isDrawTileBorders()) {
						g2.setColor(Color.black);
						g2.drawRect(ox, oy, realSizeImage, realSizeImage);
						g2.drawRect(ox + realSizeImage / 2 - 5, oy + realSizeImage / 2 - 5, 10, 10);
						g2.setColor(Color.white);
						g2.drawRect(ox + 1, oy + 1, realSizeImage, realSizeImage);

						String text = itpx + ", " + itpy + ", " + (int) Math.floor(getRealZoom());
						g2.setColor(Color.BLACK);
						g2.drawString(text, ox + 10, oy + 30);
						g2.drawString(text, ox + 10 + 2, oy + 30 + 2);
						g2.setColor(Color.WHITE);
						g2.drawString(text, ox + 10 + 1, oy + 30 + 1);
					}
				}
				oyd += realSize;
			}
			oxd += realSize;
		}

		g2.dispose();
	}

	private void drawOverlays(final Graphics g, final double realZoom, final Rectangle viewportBounds) {
		// PickedObject selectionAtStart =
		// pickedObjectList.getTopPickedObject();
		//
		// pickedObjectList.clear();
		//
		// dc.setPickPoint(pickPoint);
		dc.setPickingMode(false);
		dc.setViewportBounds(viewportBounds);
		// dc.setPickedObjectList(pickedObjectList);
		if (layerList != null && !layerList.isEmpty()) {
			for (Layer layer : layerList) {
				if (layer.isVisible()) {
					layer.paint((Graphics2D) g, dc);
				}
			}
		}
		//
		// PickedObject selectionAtEnd = pickedObjectList.getTopPickedObject();
		// if (selectionAtStart != null || selectionAtEnd != null) {
		// fireSelected(new SelectEvent(this, SelectEvent.ROLLOVER, pickPoint,
		// pickedObjectList));
		// }
	}

	private void toto(Rectangle viewportBounds) {
		PickedObject selectionAtStart = pickedObjectList.getTopPickedObject();

		pickedObjectList.clear();

		dc.setPickPoint(pickPoint);
		dc.setPickingMode(pickPoint != null);
		dc.setViewportBounds(viewportBounds);
		dc.setPickedObjectList(pickedObjectList);
		if (layerList != null && !layerList.isEmpty()) {
			for (Layer layer : layerList) {
				if (layer.isVisible() && layer.isPickEnabled()) {
					layer.pick(dc, pickPoint);
				}
			}
		}

		PickedObject selectionAtEnd = pickedObjectList.getTopPickedObject();
		if (selectionAtStart != null || selectionAtEnd != null) {
			fireSelected(new SelectEvent(this, SelectEvent.ROLLOVER, pickPoint, pickedObjectList));
		}
	}

	/**
	 * Sets the map overlay. This is a Painter which will paint on top of the
	 * map. It can be used to draw waypoints, lines, or static overlays like
	 * text messages.
	 * 
	 * @param overlay
	 *            the map overlay to use
	 * @see org.jdesktop.swingx.painters.Painter
	 */
	public void setLayers(Layer... layers) {
		List<Layer> oldLayers = this.layerList;
		layerList.clear();
		layerList.addAll(Arrays.asList(layers));
		repaint();

		firePropertyChange("layers", Collections.unmodifiableList(oldLayers), Collections.unmodifiableList(layerList));
	}

	/**
	 * Set layers
	 * 
	 * @param layers
	 */
	public void setLayers(List<Layer> layers) {
		List<Layer> oldLayers = this.layerList;
		layerList.clear();
		layerList.addAll(layers);
		repaint();

		firePropertyChange("layers", Collections.unmodifiableList(oldLayers), Collections.unmodifiableList(layerList));
	}

	/**
	 * Gets the current map overlay
	 * 
	 * @return the current map overlay
	 */
	public List<Layer> getLayers() {
		return Collections.unmodifiableList(layerList);
	}

	/**
	 * Returns the bounds of the viewport in pixels. This can be used to
	 * transform points into the world bitmap coordinate space.
	 * 
	 * @return the bounds in <em>pixels</em> of the "view" of this map
	 */
	public Rectangle getViewportBounds() {
		return calculateViewportBounds(getCenter());
	}

	private Rectangle calculateViewportBounds(Point2D center) {
		Insets insets = getInsets();
		// calculate the "visible" viewport area in pixels
		int viewportWidth = getWidth() - insets.left - insets.right;
		int viewportHeight = getHeight() - insets.top - insets.bottom;
		double viewportX = (center.getX() - viewportWidth / 2);
		double viewportY = (center.getY() - viewportHeight / 2);
		return new Rectangle((int) viewportX, (int) viewportY, viewportWidth, viewportHeight);
	}

	/**
	 * Sets whether the map should recenter itself on mouse clicks (middle mouse
	 * clicks?)
	 * 
	 * @param b
	 *            if should recenter
	 */
	public void setRecenterOnClickEnabled(boolean b) {
		boolean old = isRecenterOnClickEnabled();
		recenterOnClickEnabled = b;
		firePropertyChange("recenterOnClickEnabled", old, isRecenterOnClickEnabled());
	}

	/**
	 * Indicates if the map should recenter itself on mouse clicks.
	 * 
	 * @return boolean indicating if the map should recenter itself
	 */
	public boolean isRecenterOnClickEnabled() {
		return recenterOnClickEnabled;
	}

	public MemoryCache getMemoryCache() {
		return memoryCache;
	}

	public void setRealZoom(double realZoom) {
		TileFactoryInfo info = getTileFactory().getInfo();
		if (info != null) {
			if (realZoom < info.getMinimumZoomLevel()) {
				realZoom = info.getMinimumZoomLevel();
			}
			if (realZoom > info.getMaximumZoomLevel()) {
				realZoom = info.getMaximumZoomLevel();
			}
		}

		if (realZoom == this.realZoom) {
			return;
		}

		GeoPosition gp = getCenterPosition();

		double oldRealZoom = getRealZoom();
		this.realZoom = realZoom;

		setCenter(getTileFactory().geoToPixel(gp, realZoom));

		repaint();

		this.firePropertyChange("realZoom", oldRealZoom, realZoom);
	}

	public double getRealZoom() {
		return realZoom;
	}

	/**
	 * Gets the current address location of the map. This property does not
	 * change when the user pans the map. This property is bound.
	 * 
	 * @return the current map location (address)
	 */
	public GeoPosition getAddressLocation() {
		return addressLocation;
	}

	/**
	 * Gets the current address location of the map
	 * 
	 * @param addressLocation
	 *            the new address location
	 * @see getAddressLocation()
	 */
	public void setAddressLocation(GeoPosition addressLocation) {
		GeoPosition old = getAddressLocation();
		this.addressLocation = addressLocation;
		setCenter(getTileFactory().geoToPixel(addressLocation, getRealZoom()));

		firePropertyChange("addressLocation", old, getAddressLocation());
		repaint();
	}

	/**
	 * Re-centers the map to have the current address location be at the center
	 * of the map, accounting for the map's width and height.
	 * 
	 * @see getAddressLocation
	 */
	public void recenterToAddressLocation() {
		setCenter(getTileFactory().geoToPixel(getAddressLocation(), getRealZoom()));
		repaint();
	}

	/**
	 * Indicates if the tile borders should be drawn. Mainly used for debugging.
	 * 
	 * @return the value of this property
	 */
	public boolean isDrawTileBorders() {
		return drawTileBorders;
	}

	/**
	 * Set if the tile borders should be drawn. Mainly used for debugging.
	 * 
	 * @param drawTileBorders
	 *            new value of this drawTileBorders
	 */
	public void setDrawTileBorders(boolean drawTileBorders) {
		boolean old = isDrawTileBorders();
		this.drawTileBorders = drawTileBorders;
		firePropertyChange("drawTileBorders", old, isDrawTileBorders());
		repaint();
	}

	/**
	 * A property indicating if the map should be pannable by the user using the
	 * mouse.
	 * 
	 * @return property value
	 */
	public boolean isPanEnabled() {
		return panEnabled;
	}

	/**
	 * A property indicating if the map should be pannable by the user using the
	 * mouse.
	 * 
	 * @param panEnabled
	 *            new property value
	 */
	public void setPanEnabled(boolean panEnabled) {
		boolean old = isPanEnabled();
		this.panEnabled = panEnabled;
		firePropertyChange("panEnabled", old, isPanEnabled());
	}

	/**
	 * A property indicating if the map should be zoomable by the user using the
	 * mouse wheel.
	 * 
	 * @return the current property value
	 */
	public boolean isZoomEnabled() {
		return zoomEnabled;
	}

	/**
	 * A property indicating if the map should be zoomable by the user using the
	 * mouse wheel.
	 * 
	 * @param zoomEnabled
	 *            the new value of the property
	 */
	public void setZoomEnabled(boolean zoomEnabled) {
		boolean old = isZoomEnabled();
		this.zoomEnabled = zoomEnabled;
		firePropertyChange("zoomEnabled", old, isZoomEnabled());
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @param geoPosition
	 *            the new property value
	 */
	public void setCenterPosition(GeoPosition geoPosition) {
		GeoPosition oldVal = getCenterPosition();
		setCenter(getTileFactory().geoToPixel(geoPosition, getRealZoom()));
		repaint();
		GeoPosition newVal = getCenterPosition();
		firePropertyChange("centerPosition", oldVal, newVal);
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @return the current center position
	 */
	public GeoPosition getCenterPosition() {
		return getTileFactory().pixelToGeo(getCenter(), getRealZoom());
	}

	/**
	 * Get the current factory
	 * 
	 * @return the current property value
	 */
	public TileFactory getTileFactory() {
		return factory;
	}

	/**
	 * Set the current tile factory
	 * 
	 * @param factory
	 *            the new property value
	 */
	public void setTileFactory(TileFactory factory) {
		TileFactory oldFactory = getTileFactory();
		this.factory = factory;
		this.setRealZoom(factory.getInfo().getDefaultZoomLevel());

		firePropertyChange("tileFactory", oldFactory, this.factory);
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @return the current property value
	 */
	public Image getLoadingImage() {
		return loadingImage;
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @param loadingImage
	 *            the new property value
	 */
	public void setLoadingImage(Image loadingImage) {
		this.loadingImage = loadingImage;
	}

	public PickedObjectList getObjectsAtCurrentPosition() {
		return pickedObjectList;
	}

	protected PickedObject getCurrentSelection() {
		return pickedObjectList.getTopPickedObject();
	}

	/**
	 * Gets the current pixel center of the map. This point is in the global
	 * bitmap coordinate system, not as lat/longs.
	 * 
	 * @return the current center of the map as a pixel value
	 */
	public Point2D getCenter() {
		return center;
	}

	/**
	 * Sets the new center of the map in pixel coordinates.
	 * 
	 * @param center
	 *            the new center of the map in pixel coordinates
	 */
	public void setCenter(Point2D center) {
		Point2D old = this.getCenter();

		// if (isRestrictOutsidePanning()) {
		// Insets insets = getInsets();
		// int viewportHeight = getHeight() - insets.top - insets.bottom;
		// int viewportWidth = getWidth() - insets.left - insets.right;
		//
		// // don't let the user pan over the top edge
		// Rectangle newVP = calculateViewportBounds(center);
		// if (newVP.getY() < 0) {
		// double centerY = viewportHeight / 2;
		// center = new Point2D.Double(center.getX(), centerY);
		// }
		//
		// // don't let the user pan over the left edge
		// if (!isHorizontalWrapped() && newVP.getX() < 0) {
		// double centerX = viewportWidth / 2;
		// center = new Point2D.Double(centerX, center.getY());
		// }
		//
		// int zoom = (int) Math.floor(getRealZoom());
		// double scale = getRealZoom() - zoom;
		//
		// // don't let the user pan over the bottom edge
		// Dimension mapSize = getTileFactory().getMapSize(getRealZoom());
		// int tileSize = getTileFactory().getTileSize(getRealZoom());
		// double realSize = tileSize + tileSize * scale;
		// int realSizeImage = (int) Math.ceil(realSize);
		//
		// int mapHeight = (int) mapSize.getHeight() * realSizeImage;
		// if (newVP.getY() + newVP.getHeight() > mapHeight) {
		// double centerY = mapHeight - viewportHeight / 2;
		// center = new Point2D.Double(center.getX(), centerY);
		// }
		//
		// // don't let the user pan over the right edge
		// int mapWidth = (int) mapSize.getWidth() * realSizeImage;
		// if (!isHorizontalWrapped()
		// && (newVP.getX() + newVP.getWidth() > mapWidth)) {
		// double centerX = mapWidth - viewportWidth / 2;
		// center = new Point2D.Double(centerX, center.getY());
		// }
		//
		// // if map is to small then just center it vert
		// if (mapHeight < newVP.getHeight()) {
		// double centerY = mapHeight / 2;// viewportHeight/2;// -
		// // mapHeight/2;
		// center = new Point2D.Double(center.getX(), centerY);
		// }
		//
		// // if map is too small then just center it horiz
		// if (!isHorizontalWrapped() && mapWidth < newVP.getWidth()) {
		// double centerX = mapWidth / 2;
		// center = new Point2D.Double(centerX, center.getY());
		// }
		// }

		// joshy: this is an evil hack to force a property change event
		// i don't know why it doesn't work normally
		old = new Point(5, 6);

		GeoPosition oldGP = this.getCenterPosition();
		this.center = center;
		firePropertyChange("center", old, this.center);// .getCenter());
		firePropertyChange("centerPosition", oldGP, this.getCenterPosition());
		repaint();
	}

	/**
	 * Calculates a zoom level so that all points in the specified set will be
	 * visible on screen. This is useful if you have a bunch of points in an
	 * area like a city and you want to zoom out so that the entire city and
	 * it's points are visible without panning.
	 * 
	 * @param positions
	 *            A set of GeoPositions to calculate the new zoom from
	 */
	public void calculateZoomFrom(Set<GeoPosition> positions) {
		// u.p("calculating a zoom based on: ");
		// u.p(positions);
		if (positions.size() < 2) {
			return;
		}

		double realZoom = getRealZoom();
		Rectangle2D rect = generateBoundingRect(positions, realZoom);
		// Rectangle2D viewport = map.getViewportBounds();
		int count = 0;
		while (!getViewportBounds().contains(rect)) {
			// u.p("not contained");
			Point2D center = new Point2D.Double(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
			GeoPosition px = getTileFactory().pixelToGeo(center, getRealZoom());
			// u.p("new geo = " + px);
			setCenterPosition(px);
			count++;
			if (count > 30)
				break;

			if (getViewportBounds().contains(rect)) {
				// u.p("did it finally");
				break;
			}
			realZoom = realZoom + 1;
			if (realZoom > getTileFactory().getInfo().getMaximumZoomLevel()) {
				break;
			}
			setRealZoom(realZoom);
			rect = generateBoundingRect(positions, realZoom);
		}
	}

	private Rectangle2D generateBoundingRect(final Set<GeoPosition> positions, double realZoom) {
		Point2D point1 = getTileFactory().geoToPixel(positions.iterator().next(), getRealZoom());
		Rectangle2D rect = new Rectangle2D.Double(point1.getX(), point1.getY(), 0, 0);

		for (GeoPosition pos : positions) {
			Point2D point = getTileFactory().geoToPixel(pos, getRealZoom());
			rect.add(point);
		}
		return rect;
	}

	private final class TileLoadListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if ("loaded".equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue())) {
				Tile t = (Tile) evt.getSource();
				int zoom = (int) Math.round(realZoom);
				if (t.getZoom() == zoom) {
					repaint();
					/*
					 * this optimization doesn't save much and it doesn't work
					 * if you wrap around the world Rectangle viewportBounds =
					 * getViewportBounds(); TilePoint tilePoint =
					 * t.getLocation(); Point point = new Point(tilePoint.getX()
					 * * getTileFactory().getTileSize(), tilePoint.getY() *
					 * getTileFactory().getTileSize()); Rectangle tileRect = new
					 * Rectangle(point, new
					 * Dimension(getTileFactory().getTileSize(),
					 * getTileFactory().getTileSize())); if
					 * (viewportBounds.intersects(tileRect)) { //convert
					 * tileRect from world space to viewport space repaint(new
					 * Rectangle( tileRect.x - viewportBounds.x, tileRect.y -
					 * viewportBounds.y, tileRect.width, tileRect.height )); }
					 */
				}
				t.removePropertyChangeListener("loaded", tileLoadListener);
			}
		}
	}

	// used to pan using the arrow keys
	private class PanKeyListener extends KeyAdapter {
		private static final int OFFSET = 10;

		@Override
		public void keyPressed(KeyEvent e) {
			int delta_x = 0;
			int delta_y = 0;

			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				delta_x = -OFFSET;
				break;
			case KeyEvent.VK_RIGHT:
				delta_x = OFFSET;
				break;
			case KeyEvent.VK_UP:
				delta_y = -OFFSET;
				break;
			case KeyEvent.VK_DOWN:
				delta_y = OFFSET;
				break;
			}

			if (delta_x != 0 || delta_y != 0) {
				Rectangle bounds = getViewportBounds();
				double x = bounds.getCenterX() + delta_x;
				double y = bounds.getCenterY() + delta_y;
				setCenter(new Point2D.Double(x, y));
				repaint();
			}
		}
	}

	// used to pan using press and drag mouse gestures
	private class PanMouseInputListener implements MouseInputListener {

		private Point mousePoint;

		private boolean localDrag = false;

		private boolean isDragging = false;

		private boolean isHovering = false;

		private PickedObjectList hoverObjects;

		private PickedObjectList objectsAtButtonPress;

		public PanMouseInputListener() {
		}

		private void doHover(boolean reset) {
		}

		private void cancelHover() {
		}

		private void cancelDrag() {
			if (this.isDragging) {
				fireSelected(new DragSelectEvent(JXMapViewer.this, SelectEvent.DRAG_END, null, this.objectsAtButtonPress, this.mousePoint));
			}

			this.isDragging = false;
		}

		public void mousePressed(MouseEvent mouseEvent) {
			localDrag = false;
			mousePoint = mouseEvent.getPoint();

			this.cancelHover();
			this.cancelDrag();

			this.objectsAtButtonPress = new PickedObjectList(pickedObjectList);

			if (this.objectsAtButtonPress != null && objectsAtButtonPress.getTopPickedObject() != null) {
				// Something is under the cursor, so it's deemed "selected".
				if (MouseEvent.BUTTON1 == mouseEvent.getButton()) {
					fireSelected(new SelectEvent(JXMapViewer.this, SelectEvent.LEFT_PRESS, mouseEvent, this.objectsAtButtonPress));
				} else if (MouseEvent.BUTTON3 == mouseEvent.getButton()) {
					fireSelected(new SelectEvent(JXMapViewer.this, SelectEvent.RIGHT_PRESS, mouseEvent, this.objectsAtButtonPress));
				}

				repaint();
			} else {
				if (!mouseEvent.isConsumed()) {

				}
			}
		}

		public void mouseDragged(MouseEvent mouseEvent) {
			Point prevMousePoint = this.mousePoint;
			this.mousePoint = mouseEvent.getPoint();

			if (MouseEvent.BUTTON1_DOWN_MASK == mouseEvent.getModifiersEx()) {
				PickedObjectList pickedObjects = this.objectsAtButtonPress;
				if (this.isDragging || (pickedObjects != null && pickedObjects.getTopPickedObject() != null)) {
					this.isDragging = true;
					DragSelectEvent selectEvent = new DragSelectEvent(JXMapViewer.this, SelectEvent.DRAG, mouseEvent, pickedObjects, prevMousePoint);
					fireSelected(selectEvent);

					// If no listener consumed the event, then cancel the drag.
					if (!selectEvent.isConsumed()) {
						this.cancelDrag();
					}
				}
			}

			if (!this.isDragging) {
				if (!mouseEvent.isConsumed()) {
					if (isPanEnabled() && prevMousePoint != null) {
						localDrag = true;
						Point current = mouseEvent.getPoint();
						double x = getCenter().getX() - (current.x - prevMousePoint.x);
						double y = getCenter().getY() - (current.y - prevMousePoint.y);

						int maxHeight = (int) (getTileFactory().getMapSize(getRealZoom()).getHeight() * getTileFactory().getTileSize(getRealZoom()));
						if (y > maxHeight) {
							y = maxHeight;
						}

						setCenter(new Point2D.Double(x, y));
						repaint();
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						mousePoint = current;
					}
				}
			}

			pickPoint = mouseEvent.getPoint();
			repaint();
		}

		public void mouseReleased(MouseEvent mouseEvent) {
			mousePoint = mouseEvent.getPoint();

			if (!mouseEvent.isConsumed()) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			this.doHover(true);
			this.cancelDrag();
		}

		public void mouseMoved(MouseEvent e) {
			pickPoint = e.getPoint();
			// repaint();
			toto(getViewportBounds());

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					requestFocusInWindow();
				}
			});
		}

		public void mouseClicked(MouseEvent mouseEvent) {
			PickedObjectList pickedObjects = getObjectsAtCurrentPosition();

			if (pickedObjects != null && pickedObjects.getTopPickedObject() != null) {
				// Something is under the cursor, so it's deemed "selected".
				if (MouseEvent.BUTTON1 == mouseEvent.getButton()) {
					if (mouseEvent.getClickCount() <= 1) {
						fireSelected(new SelectEvent(JXMapViewer.this, SelectEvent.LEFT_CLICK, mouseEvent, pickedObjects));
					} else {
						fireSelected(new SelectEvent(JXMapViewer.this, SelectEvent.LEFT_DOUBLE_CLICK, mouseEvent, pickedObjects));
					}
				} else if (MouseEvent.BUTTON3 == mouseEvent.getButton()) {
					fireSelected(new SelectEvent(JXMapViewer.this, SelectEvent.RIGHT_CLICK, mouseEvent, pickedObjects));
				}

				repaint();
			} else {
				if (!mouseEvent.isConsumed()) {

				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			localDrag = false;

			this.cancelHover();
			this.cancelDrag();
		}

		public void mouseExited(MouseEvent e) {
			localDrag = false;

			pickPoint = null;
			repaint();

			this.cancelHover();
			this.cancelDrag();
		}
	}

	// zooms using the mouse wheel
	private class ZoomMouseWheelListener implements MouseWheelListener {

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (isZoomEnabled()) {
				setRealZoom(getRealZoom() + e.getWheelRotation() * 0.1f);
			}
		}
	}

	public boolean isRestrictOutsidePanning() {
		return restrictOutsidePanning;
	}

	public void setRestrictOutsidePanning(boolean restrictOutsidePanning) {
		this.restrictOutsidePanning = restrictOutsidePanning;
	}

	public boolean isHorizontalWrapped() {
		return horizontalWrapped;
	}

	public void setHorizontalWrapped(boolean horizontalWrapped) {
		this.horizontalWrapped = horizontalWrapped;
	}

	/**
	 * Converts the specified GeoPosition to a point in the JXMapViewer's local
	 * coordinate space. This method is especially useful when drawing lat/long
	 * positions on the map.
	 * 
	 * @param pos
	 *            a GeoPosition on the map
	 * @return the point in the local coordinate space of the map
	 */
	public Point2D convertGeoPositionToPoint(GeoPosition pos) {
		// convert from geo to world bitmap
		Point2D pt = getTileFactory().geoToPixel(pos, getRealZoom());
		// convert from world bitmap to local
		Rectangle bounds = getViewportBounds();
		return new Point2D.Double(pt.getX() - bounds.getX(), pt.getY() - bounds.getY());
	}

	/**
	 * Converts the specified Point2D in the JXMapViewer's local coordinate
	 * space to a GeoPosition on the map. This method is especially useful for
	 * determining the GeoPosition under the mouse cursor.
	 * 
	 * @param pt
	 *            a point in the local coordinate space of the map
	 * @return the point converted to a GeoPosition
	 */
	public GeoPosition convertPointToGeoPosition(Point2D pt) {
		// convert from local to world bitmap
		Rectangle bounds = getViewportBounds();
		Point2D pt2 = new Point2D.Double(pt.getX() + bounds.getX(), pt.getY() + bounds.getY());

		// convert from world bitmap to geo
		GeoPosition pos = getTileFactory().pixelToGeo(pt2, getRealZoom());
		return pos;
	}

	public double computePixelSize() {
		Point2D pt = getCenter();
		GeoPosition gp1 = getTileFactory().pixelToGeo(new Point2D.Double(pt.getX(), pt.getY()), getRealZoom());
		GeoPosition gp2 = getTileFactory().pixelToGeo(new Point2D.Double(pt.getX() + 1, pt.getY()), getRealZoom());
		return gp1.distance(gp2);
	}
}
