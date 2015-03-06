import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.synaptix.client.view.AbstractViewWorker;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swingx.mapviewer.info.GeoWebCacheTileFactoryInfo;
import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;
import com.synaptix.swingx.mapviewer.layers.CrosshairLayer;
import com.synaptix.swingx.mapviewer.layers.InfoLayer;
import com.synaptix.swingx.mapviewer.layers.ToolTipLayer;
import com.synaptix.swingx.mapviewer.layers.ViewControlsLayer;
import com.synaptix.swingx.mapviewer.layers.WorldMapLayer;
import com.synaptix.swingx.mapviewer.layers.airspace.PathAirspace;
import com.synaptix.view.swing.WaitFullComponentSwingWaitWorker;

public class MainCarteRoutePanel extends WaitComponentFeedbackPanel {

	private static final long serialVersionUID = -1706471085257292550L;

	private static final String baseUrl = "http://10.61.127.101:8180/geoserver/gwc/service/tms/1.0.0/Gefco:xmap-ajaxbg@EPSG:900913@png/";

	private JXMapViewer mapViewer;

	private AirspacesLayer airspacesLayer;

	public MainCarteRoutePanel() {
		super();

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		mapViewer = new JXMapViewer();
		mapViewer.setLoadingImage(null);

		mapViewer.setTileFactory(new DefaultTileFactory(new GeoWebCacheTileFactoryInfo(5, 17, 20, 256, baseUrl)));
		mapViewer.setRealZoom(15.0f);
		mapViewer.setAddressLocation(new GeoPosition(45, 4));
		mapViewer.setDrawTileBorders(false);

		airspacesLayer = new AirspacesLayer();

		mapViewer.setLayers(airspacesLayer, new WorldMapLayer(mapViewer), new CrosshairLayer(), new InfoLayer(), new ViewControlsLayer(mapViewer), new ToolTipLayer(mapViewer));
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapViewer, BorderLayout.CENTER);
		return panel;
	}

	public void setGeoPositions(List<GeoPosition> gs) {
		airspacesLayer.removeAllAirspaces();

		PathAirspace pa1 = new PathAirspace(gs);
		pa1.getAirspaceAttributes().setDrawInterior(false);
		pa1.getAirspaceAttributes().setOutlineColor(new Color(128, 128, 255, 196));
		pa1.getAirspaceAttributes().setOutlineStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		pa1.setShowArrow(true);
		airspacesLayer.addAirspace(pa1);

		PathAirspace pa2 = new PathAirspace(Arrays.asList(gs.get(0), gs.get(gs.size() - 1)), false);
		pa2.getAirspaceAttributes().setDrawInterior(false);
		pa2.getAirspaceAttributes().setOutlineColor(new Color(128, 128, 255, 196));
		pa2.getAirspaceAttributes().setOutlineStroke(new BasicStroke(5.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		pa2.setShowArrow(true);
		airspacesLayer.addAirspace(pa2);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFrame frame = new JFrame("Map Tests");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());

				final MainCarteRoutePanel panel = new MainCarteRoutePanel();

				frame.getContentPane().add(panel, BorderLayout.CENTER);

				// frame.pack();
				frame.setSize(1280, 800);

				frame.setVisible(true);

				WaitFullComponentSwingWaitWorker.waitFullComponentSwingWaitWorker(panel, new AbstractViewWorker<List<GeoPosition>, String>() {

					@Override
					public List<GeoPosition> doBackground() throws Exception {
						// BufferedWriter bw = new BufferedWriter(new
						// FileWriter("D:/path.txt"));

						List<GeoPosition> gs = new ArrayList<GeoPosition>();

						BufferedReader br = new BufferedReader(new InputStreamReader(MainCarteRoutePanel.class.getResourceAsStream("path.txt")));
						String line = null;
						while ((line = br.readLine()) != null) {
							String[] ss = line.split("\\|");

							double lat = Double.parseDouble(ss[0]);
							double lon = Double.parseDouble(ss[1]);

							gs.add(new GeoPosition(lat, lon));
						}

						br.close();

						System.out.println(gs.size());

						return gs;
					}

					public void success(List<GeoPosition> e) {

						panel.setGeoPositions(e);
					}

					@Override
					public void fail(Throwable t) {
						t.printStackTrace();
					}

				});
			}
		});
	}
}
