import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.GeoPositionEvaluator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.animation.timing.interpolation.SplineInterpolator;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;

import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swingx.mapviewer.info.OpenStreetMapTileFactoryInfo;
import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;
import com.synaptix.swingx.mapviewer.layers.CrosshairLayer;
import com.synaptix.swingx.mapviewer.layers.HighlightSelectListener;
import com.synaptix.swingx.mapviewer.layers.InfoLayer;
import com.synaptix.swingx.mapviewer.layers.LegendLayer;
import com.synaptix.swingx.mapviewer.layers.RenderableLayer;
import com.synaptix.swingx.mapviewer.layers.ScalableLayer;
import com.synaptix.swingx.mapviewer.layers.Selectable;
import com.synaptix.swingx.mapviewer.layers.SelectedSelectListener;
import com.synaptix.swingx.mapviewer.layers.ToolTipLayer;
import com.synaptix.swingx.mapviewer.layers.ToolTipable;
import com.synaptix.swingx.mapviewer.layers.ViewControlsLayer;
import com.synaptix.swingx.mapviewer.layers.WaypointsLayer;
import com.synaptix.swingx.mapviewer.layers.WorldMapLayer;
import com.synaptix.swingx.mapviewer.layers.airspace.CircleAirspace;
import com.synaptix.swingx.mapviewer.layers.airspace.DefaultAirspaceAttributes;
import com.synaptix.swingx.mapviewer.layers.airspace.FixedArrowAirspace;
import com.synaptix.swingx.mapviewer.layers.render.AnnotationAttributes;
import com.synaptix.swingx.mapviewer.layers.render.GlobeAnnotation;
import com.synaptix.swingx.mapviewer.layers.waypoint.Waypoint;
import com.synaptix.swingx.mapviewer.layers.waypoint.WaypointRenderer;

public class CartePanel extends WaitComponentFeedbackPanel {

	private static final long serialVersionUID = -1706471085257292550L;

	private static final String baseUrl = "http://10.61.127.101:8180/geoserver/gwc/service/tms/1.0.0/Gefco:xmap-ajaxbg@EPSG:900913@png/";

	private JXMapViewer mapViewer;

	private JButton button;

	public CartePanel() {
		super();

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new JMenuItem("toto"));
		popupMenu.add(new JMenuItem("tata"));

		mapViewer = new JXMapViewer();
		// mapViewer.setTileFactory(new DefaultTileFactory(
		// new MicrosoftVirtualEarthTileFactoryInfo(
		// MicrosoftVirtualEarthTileFactoryInfo.Type.Satellite)));
		mapViewer.setLoadingImage(null);

		//
		// new OpenStreetMapTileFactoryInfo(2, 16)));
		// mapViewer.setTileFactory(new DefaultTileFactory(new
		// PtvLoxaneGetMapTileFactoryInfo("http://10.61.150.15:50010/WMS/GetTile/xmap-ajaxbg/")));
		// mapViewer.setTileFactory(new DefaultTileFactory(new
		// MicrosoftVirtualEarthTileFactoryInfo(MicrosoftVirtualEarthTileFactoryInfo.Type.Map)));
		mapViewer.setTileFactory(new DefaultTileFactory(new OpenStreetMapTileFactoryInfo(OpenStreetMapTileFactoryInfo.MIN_ZOOM_LEVEL, OpenStreetMapTileFactoryInfo.MAX_ZOOM_LEVEL,
				"http://192.168.56.101/osm/")));
		// mapViewer.setTileFactory(new DefaultTileFactory(new
		// GeoWebCacheTileFactoryInfo(2, 16, 19, 256,
		// "http://localhost:9090/geoserver/gwc/service/tms/1.0.0/Test@EPSG:900913@png/")));
		// mapViewer.setTileFactory(new DefaultTileFactory(new
		// GeoWebCacheTileFactoryInfo(2, 16, 19, 256,
		// "http://www-rec.geoserver.fret.sncf.fr/geoserver/gwc/service/tms/Test2@EPSG:900913@png/")));
		// mapViewer.setTileFactory(new WMSTileFactory(new
		// WMSService("http://localhost:9090/geoserver/wms?", "Test")));
		// mapViewer.setTileFactory(new WMSTileFactory(new
		// WMSService("http://www-int.geoserver.fret.sncf.fr/geoserver/gwc/service/wms?",
		// "Sncf")));
		mapViewer.setRealZoom(15.0f);
		mapViewer.setAddressLocation(new GeoPosition(45, 4));
		mapViewer.setDrawTileBorders(false);

		DefaultAirspaceAttributes haa2 = new DefaultAirspaceAttributes();
		haa2.setOutlineStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		haa2.setOutlineColor(Color.PINK);
		haa2.setDrawOutline(true);
		haa2.setDrawInterior(false);

		AirspacesLayer airspacesLayer = new AirspacesLayer();

		List<Waypoint> wpSet = new ArrayList<Waypoint>();
		wpSet.add(new MyWaypoint("Valence", 44.933333, 4.891667));
		// wpSet.add(new MyWaypoint("Tokyo / 東京都", 30, 130));
		// wpSet.add(new MyWaypoint("Paris", 48.85666666666667,
		// 2.3519444444444444));
		// wpSet.add(new MyWaypoint("Moscou / Москва", 55.45, 37.37));
		// wpSet.add(new MyWaypoint("Miami", 48.5, -56));
		// Random rand = new Random();
		// for (int i = 0; i < 500; i++) {
		// Waypoint w = new MyWaypoint("Toto" + i, rand.nextInt(180) - 90,
		// rand.nextInt(360) - 180);
		// wpSet.add(w);
		// }

		final WaypointsLayer wl = new WaypointsLayer();
		wl.setWaypoints(wpSet);
		wl.setRenderer(new MyWaypointRenderer());

		DefaultAirspaceAttributes haa1 = new DefaultAirspaceAttributes();
		haa1.setOutlineStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		haa1.setColor(Color.PINK);
		haa1.setDrawOutline(true);

		double dist = new GeoPosition(48.5, 2.5).distance(new GeoPosition(45, 4));
		CircleAirspace ca1 = new CircleAirspace(new GeoPosition(45, 4), dist);
		ca1.getAirspaceAttributes().setOutlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		ca1.getAirspaceAttributes().setColor(new Color(0, 0, 0, 0.2f));
		ca1.setToolTip("Un grand cercle de " + Math.round(dist / 1000) + "km avec un text qui est tres long. Vraiment long.");
		airspacesLayer.addAirspace(ca1);

		FixedArrowAirspace f1 = new FixedArrowAirspace(new GeoPosition(45, 4), false, new GeoPosition(45, 4).middle(new GeoPosition(48.5, -56)), true, 20);
		f1.setFineStart(false);
		f1.setFineEnd(false);
		f1.setToolTip("Gaby est ici");
		f1.setHighlightAirspaceAttributes(haa1);
		airspacesLayer.addAirspace(f1);

		FixedArrowAirspace f2 = new FixedArrowAirspace(new GeoPosition(48.5, -56), false, new GeoPosition(45, 4).middle(new GeoPosition(48.5, -56)), true, 15);
		f2.setFineStart(false);
		f2.setFineEnd(false);
		f2.setToolTip("Gaby est ici");
		f2.setHighlightAirspaceAttributes(haa1);
		airspacesLayer.addAirspace(f2);

		FixedArrowAirspace f3 = new FixedArrowAirspace(new GeoPosition(45, 4), true, new GeoPosition(45, 4).middle(new GeoPosition(30, 130)), true, 15);
		airspacesLayer.addAirspace(f3);

		FixedArrowAirspace f4 = new FixedArrowAirspace(new GeoPosition(30, 130), true, new GeoPosition(45, 4).middle(new GeoPosition(30, 130)), true, 35);
		f4.getAirspaceAttributes().setColor(new Color(255, 64, 64, 128));
		airspacesLayer.addAirspace(f4);

		// PathAirspace pa1 = new PathAirspace(Arrays.asList(
		// new GeoPosition(45, 4), new GeoPosition(30, 130),
		// new GeoPosition(48.5, 2.5), new GeoPosition(55.45, 37.37)));
		// pa1.getAirspaceAttributes().setDrawInterior(false);
		// pa1.getAirspaceAttributes().setOutlineColor(Color.RED);
		// pa1.getAirspaceAttributes().setOutlineStroke(
		// new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
		// BasicStroke.JOIN_ROUND));
		// pa1.setHighlightAirspaceAttributes(haa2);
		// pa1.setShowArrow(true);
		// airspacesLayer.addAirspace(pa1);
		//
		// PathAirspace pa2 = new PathAirspace(Arrays.asList(
		// new GeoPosition(45, 4), new GeoPosition(48.5, 2.5)));
		// pa2.getAirspaceAttributes().setDrawInterior(false);
		// pa2.getAirspaceAttributes().setOutlineColor(Color.GREEN);
		// pa2.getAirspaceAttributes().setOutlineStroke(
		// new BasicStroke(5.0f, BasicStroke.CAP_ROUND,
		// BasicStroke.JOIN_ROUND, 0, new float[] { 5, 15 }, 0));
		// pa2.setShowArrow(true);
		// airspacesLayer.addAirspace(pa2);

		ScalableLayer sl = new ScalableLayer();
		sl.setPosition(ScalableLayer.SOUTHWEST);

		Image databaseImage = new ImageIcon(CartePanel.class.getResource("/database.png")).getImage();
		Image gameImage = new ImageIcon(CartePanel.class.getResource("/games.png")).getImage();

		LegendLayer legendLayer = new LegendLayer("Légende", LegendLayer.SOUTHEAST);
		legendLayer.setLocationOffset(new Point(0, -20));
		legendLayer.addLine(databaseImage, "Position des bases de données dans le monde pour le client");
		legendLayer.addLine(new ImageIcon(CartePanel.class.getResource("/firefox.png")).getImage(), "Site internet");
		legendLayer.addLine(gameImage, "Agence de jeux video");
		legendLayer.addLine(new ImageIcon(CartePanel.class.getResource("/gkrellm2.png")).getImage(), "Une agence ailleurs");
		legendLayer.addLine(new ImageIcon(CartePanel.class.getResource("/galeon.png")).getImage(), "Agence de jeux video");

		RenderableLayer rl2 = new RenderableLayer();
		rl2.setPickEnabled(true);

		AnnotationAttributes iconAa = new AnnotationAttributes();
		iconAa.setOpacity(1);
		iconAa.setBorderColor(new Color(0, 0, 0, 0));
		iconAa.setBackgroundColor(new Color(0, 0, 0, 0));
		iconAa.setInsets(new Insets(5, 5, 5, 5));
		iconAa.setScale(1);
		iconAa.setAdjustWidthToText(AVKey.SIZE_FIXED);
		iconAa.setSize(new Dimension(32, 32));
		iconAa.setDrawOffset(new Point(0, 0));

		GlobeAnnotation dbGa1 = new GlobeAnnotation(null, new GeoPosition(53, 7));
		dbGa1.setToolTip("Un serveur a distance. Pays-bas 1");
		dbGa1.getAnnotationAttributes().setDefaults(iconAa);
		dbGa1.getAnnotationAttributes().setBackgroundImage(databaseImage);
		rl2.addRenderable(dbGa1);

		GlobeAnnotation dbGa2 = new GlobeAnnotation(null, new GeoPosition(52, 6));
		dbGa2.setToolTip("Un serveur a distance. Pays-bas 2");
		dbGa2.getAnnotationAttributes().setDefaults(iconAa);
		dbGa2.getAnnotationAttributes().setBackgroundImage(databaseImage);
		rl2.addRenderable(dbGa2);

		GlobeAnnotation dbGa3 = new GlobeAnnotation(null, new GeoPosition(51, 5));
		dbGa3.setToolTip("Belgique");
		dbGa3.getAnnotationAttributes().setDefaults(iconAa);
		dbGa3.getAnnotationAttributes().setBackgroundImage(databaseImage);
		rl2.addRenderable(dbGa3);

		GlobeAnnotation gamesGa1 = new GlobeAnnotation(null, new GeoPosition(48, 0));
		gamesGa1.setToolTip("UbiRoft");
		gamesGa1.getAnnotationAttributes().setDefaults(iconAa);
		gamesGa1.getAnnotationAttributes().setBackgroundImage(gameImage);
		rl2.addRenderable(gamesGa1);

		AnnotationAttributes aa1 = new AnnotationAttributes();
		aa1.setOpacity(1);
		aa1.setBackgroundColor(new Color(1f, 1f, 1f, 0.25f));
		aa1.setInsets(new Insets(5, 5, 5, 5));
		aa1.setFont(Font.decode("Arial-BOLD-20"));
		aa1.setScale(1);
		aa1.setTextAlign(AVKey.ALIGN_CENTER);
		aa1.setAdjustWidthToText(AVKey.SIZE_FIT_TEXT);
		aa1.setDrawOffset(new Point(0, 0));

		GlobeAnnotation ga1 = new GlobeAnnotation("Océan Atlantique", new GeoPosition(35, -30));
		ga1.setAnnotationAttributes(aa1);
		rl2.addRenderable(ga1);

		GlobeAnnotation ga2 = new GlobeAnnotation("Россия", new GeoPosition(60, 50));
		ga2.getAnnotationAttributes().setDefaults(aa1);
		rl2.addRenderable(ga2);

		GlobeAnnotation ga3 = new GlobeAnnotation("中国", new GeoPosition(40, 100));
		ga3.getAnnotationAttributes().setDefaults(aa1);
		ga3.getAnnotationAttributes().setFont(Font.decode("Arial Unicode MS-BOLD-20"));
		rl2.addRenderable(ga3);

		GlobeAnnotation ga4 = new GlobeAnnotation("الجزائر", new GeoPosition(30, 5));
		ga4.getAnnotationAttributes().setDefaults(aa1);
		ga4.getAnnotationAttributes().setFont(Font.decode("Arial Unicode MS-BOLD-20"));
		rl2.addRenderable(ga4);

		GlobeAnnotation ga5 = new GlobeAnnotation("日本の", new GeoPosition(35, 138));
		ga5.getAnnotationAttributes().setDefaults(aa1);
		ga5.getAnnotationAttributes().setFont(Font.decode("Arial Unicode MS-BOLD-20"));
		rl2.addRenderable(ga5);

		GlobeAnnotation ga6 = new GlobeAnnotation("Ελλάδα", new GeoPosition(40, 22));
		ga6.getAnnotationAttributes().setDefaults(aa1);
		ga6.getAnnotationAttributes().setFont(Font.decode("Arial Unicode MS-BOLD-20"));
		rl2.addRenderable(ga6);

		airspacesLayer.setVisible(true);
		rl2.setVisible(true);
		legendLayer.setVisible(true);

		mapViewer.setLayers(airspacesLayer, wl, rl2, legendLayer, new WorldMapLayer(mapViewer), new CrosshairLayer(), new InfoLayer(), sl, new ViewControlsLayer(mapViewer),
				new ToolTipLayer(mapViewer));

		mapViewer.addSelectListener(new HighlightSelectListener(mapViewer));

		SelectedSelectListener ssl = new SelectedSelectListener(mapViewer);
		ssl.setMode(SelectedSelectListener.MODE_MULTIPLE_SELECTION);
		ssl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			}
		});
		mapViewer.addSelectListener(ssl);

		mapViewer.addSelectListener(new SelectListener() {
			public void selected(SelectEvent event) {
				// System.out.println(event);
				if (event.isRightClick() && event.getTopObject() instanceof Selectable) {
					popupMenu.show(mapViewer, event.getMouseEvent().getX(), event.getMouseEvent().getY());
				}
			}
		});

		button = new JButton("Coucou");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Animator zoomAnimator = PropertySetter.createAnimator(5000, mapViewer, "realZoom", mapViewer.getRealZoom(), 16.0, 12.0);
				zoomAnimator.setInterpolator(new SplineInterpolator(0, 0, 1, 1));
				zoomAnimator.setAcceleration(0.7f);
				zoomAnimator.setDeceleration(0.3f);
				zoomAnimator.start();
				Animator positionAnimator = PropertySetter.createAnimator(5000, mapViewer, "centerPosition", new GeoPositionEvaluator(), mapViewer.getCenterPosition(), new GeoPosition(50, 100));
				positionAnimator.setAcceleration(0.7f);
				positionAnimator.setDeceleration(0.3f);
				positionAnimator.start();
			}
		});
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapViewer, BorderLayout.CENTER);
		panel.add(button, BorderLayout.SOUTH);
		return panel;
	}

	private static final class MyWaypoint extends Waypoint implements ToolTipable {

		private String name;

		public MyWaypoint(String name, double latitude, double longitude) {
			super(latitude, longitude);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getToolTip() {
			return "Coucou de " + name;
		}
	}

	private static final class MyWaypointRenderer implements WaypointRenderer {

		private Font textFont = new Font("Arial Unicode MS", Font.BOLD, 20);

		private Stroke doubleStroke = new BasicStroke(2.0f);

		private Stroke normalStroke = new BasicStroke(1.0f);

		private Map<String, Rectangle2D> tempMap = new HashMap<String, Rectangle2D>();

		public MyWaypointRenderer() {
			super();

		}

		@Override
		public boolean paintWaypoint(Graphics2D g, DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint) {
			Graphics2D g2 = (Graphics2D) g.create();

			if (waypoint instanceof MyWaypoint) {
				int directionX = -1;
				int directionY = -1;

				int dx = 20;
				int dy = 20;

				MyWaypoint wp = (MyWaypoint) waypoint;

				Rectangle2D rect = tempMap.get(wp.getName());
				if (rect == null) {
					rect = textFont.getStringBounds(wp.getName(), g2.getFontRenderContext());
					tempMap.put(wp.getName(), rect);
				}

				g2.setColor(Color.black);
				g2.drawLine(0, 0, directionX * dx, directionY * dy);
				g2.drawLine(directionX * dx, directionY * dy, directionX * (dx + (int) rect.getWidth()), directionY * dy);

				g2.setFont(textFont);
				g2.setColor(Color.white);
				g2.drawString(wp.getName(), directionX > 0 ? dx - 1 : -(dx + (int) rect.getWidth()) - 1, directionY * dy - 1);
				g2.setColor(Color.black);
				g2.drawString(wp.getName(), directionX > 0 ? dx - 1 : -(dx + (int) rect.getWidth()), directionY * dy);
			}

			int size = 10;
			if (waypoint.isHighlighted()) {
				size = 16;
			}
			int dsize = size / 2;
			g2.setColor(Color.red);
			g2.fillArc(-dsize, -dsize, size, size, 0, 360);

			g2.setColor(Color.black);
			g2.setStroke(doubleStroke);
			g2.drawArc(-dsize, -dsize, size, size, 0, 360);
			g2.setStroke(normalStroke);

			if (waypoint.isSelected()) {
				g2.setColor(new Color(0, 0, 255, 128));
				g2.fillRect(-10, -10, 20, 20);

				g2.setColor(new Color(0, 0, 255, 192));
				g2.drawRect(-10, -10, 20, 20);
			}

			g2.dispose();
			return false;
		}

		Rectangle boundingBox = new Rectangle(-10, -10, 20, 20);

		@Override
		public Rectangle getBoundingBox(DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint) {
			return boundingBox;
		}
	}
}