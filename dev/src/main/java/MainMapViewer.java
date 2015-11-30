import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synaptix.swingx.mapviewer.info.OpenStreetMapTileFactoryInfo;
import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;
import com.synaptix.swingx.mapviewer.layers.HighlightSelectListener;
import com.synaptix.swingx.mapviewer.layers.SelectedSelectListener;
import com.synaptix.swingx.mapviewer.layers.ToolTipLayer;
import com.synaptix.swingx.mapviewer.layers.WaypointsLayer;
import com.synaptix.swingx.mapviewer.layers.airspace.AreaAirspace;
import com.synaptix.swingx.mapviewer.layers.airspace.DefaultAirspaceAttributes;
import com.synaptix.swingx.mapviewer.layers.waypoint.Waypoint;

import helper.MainHelper;

public class MainMapViewer {

	public static void main(String[] args) throws Exception {
		Gson gson = new GsonBuilder().create();

		final List<State> states = new ArrayList<State>();
		File dir = new File("D:/World/test");
		for (File file : dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return Files.getFileExtension(pathname.getAbsolutePath()).equals("json");
			}
		})) {
			states.add(gson.fromJson(new FileReader(file), State.class));
		}
		// states.add(gson.fromJson(new FileReader(new File(dir, "79_82_26.json")), State.class));
		System.out.println(states.size());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainHelper.init();

				// TileFactoryInfo tileFactoryInfo = new PtvWMSTileFactoryInfo("http://vip-ptv-xserver:50010/WMS/WMS", "xmap-ajaxbg");
				TileFactoryInfo tileFactoryInfo = new OpenStreetMapTileFactoryInfo();
				DefaultTileFactory tileFactory = new DefaultTileFactory(tileFactoryInfo);
				tileFactory.setTileCache(new TileCache(tileFactoryInfo, false));

				JXMapViewer mapViewer = new JXMapViewer();
				mapViewer.setTileFactory(tileFactory);
				mapViewer.setLoadingImage(null);

				mapViewer.setRealZoom(15.0f);
				mapViewer.setAddressLocation(new GeoPosition(47, 3));
				mapViewer.setDrawTileBorders(false);

				mapViewer.setBorder(BorderFactory.createEtchedBorder());

				// EPSG505456NamePlacesLayer cityNamePlacesLayer = new EPSG505456NamePlacesLayer(mapViewer, "http://vip-ptv-xserver:50010/WMS/WMS", "xmap-ajaxfg");
				// cityNamePlacesLayer.setMaxAcceptZoom(14.5);
				// EPSG900913NamePlacesLayer countryNamePlacesLayer = new EPSG900913NamePlacesLayer(mapViewer, "http://10.61.127.101:8180/geoserver/Gefco/wms", "Gefco:gadm1_lev0");
				// countryNamePlacesLayer.setMinAcceptZoom(13.0);

				Random rand = new Random();

				AirspacesLayer al = new AirspacesLayer();

				for (State state : states) {
					System.out.println("Total " + state.paths.size());

					boolean createNew = false;
					List<GeoPosition> locations = new ArrayList<GeoPosition>();
					List<GeoPosition> currents = new ArrayList<GeoPosition>();
					List<Integer> groups = new ArrayList<Integer>();
					for (State.Path p : state.paths) {
						if (createNew) {
							if (currents.size() >= 10) {
								locations.addAll(currents);
								System.out.println("Group " + groups.size() + " -> " + locations.size() + " pos : " + locations.size());

								groups.add(locations.size());
							}
							createNew = false;
							currents.clear();
						}

						currents.add(new GeoPosition(p.y, p.x));
						createNew = "SEG_CLOSE".equals(p.type);
					}
					if (currents.size() >= 10) {
						locations.addAll(currents);
					}
					System.out.println(locations.size());

					AreaAirspace aa1 = new AreaAirspace(locations, groups);
					aa1.getAirspaceAttributes().setOutlineStroke(new BasicStroke(1));
					aa1.getAirspaceAttributes().setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 128));
					aa1.setHighlightable(true);
					aa1.setHighlightAirspaceAttributes(new DefaultAirspaceAttributes.Builder().color(Color.RED).outlineStroke(new BasicStroke(1)).build());
					aa1.setSelectable(true);
					aa1.setToolTip(state.name);
					al.addAirspace(aa1);
				}

				WaypointsLayer wl = new WaypointsLayer();
				wl.setWaypoints(Arrays.asList(new Waypoint(48.856578, 2.351828), new Waypoint(41.888732, 12.48657)));
				mapViewer.addSelectListener(new HighlightSelectListener(mapViewer));
				mapViewer.addSelectListener(new SelectedSelectListener(mapViewer));
				mapViewer.setLayers(wl, al, new ToolTipLayer(mapViewer));

				JFrame frame = MainHelper.createFrame();

				frame.getContentPane().add(mapViewer);

				frame.setVisible(true);
			}
		});
	}

	public static class State {

		public String name;

		public List<Path> paths;

		public static class Path {

			public double x;

			public double y;

			public String type;

		}

	}
}
