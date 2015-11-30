import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import com.synaptix.swing.JArrowScrollPane;
import com.synaptix.swing.selection.XYSelectionModel;
import com.synaptix.swingx.mapviewer.info.OpenStreetMapTileFactoryInfo;
import com.synaptix.widget.calendarday.DefaultCalendarDayRowHeaderRenderer;
import com.synaptix.widget.calendarday.DefaultYearCalendarDayModel;
import com.synaptix.widget.calendarday.JCalendarDay;
import com.synaptix.widget.calendarday.JCalendarDayRowHeader;

import helper.MainHelper;

public class MainCopyScreen {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				final JComponent[] cs = new JComponent[] { createYearCalendar(), new JScrollPane(new JTable(new DefaultTableModel(50, 5))), createMapViewer() };
				createFrame(cs);

				JFrame frame2 = MainHelper.createFrame();
				final JPanel panel = new JPanel(new VerticalLayout(10));
				panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				updatePanel(panel, cs);
				frame2.getContentPane().add(new JArrowScrollPane(panel));
				frame2.pack();
				frame2.setVisible(true);

				new Timer(2000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updatePanel(panel, cs);
					}
				}).start();
			}
		});
	}

	private static Map<JComponent, Image> map = new HashMap<JComponent, Image>();

	private static void updatePanel(JPanel panel, JComponent[] cs) {
		panel.removeAll();
		for (int i = 0; i < 10; i++) {
			panel.add(create("Titre" + i, cs[i % 3], i == 2 ? 60 : 50, i == 2 ? 60 : 50, i == 2));
		}
		panel.revalidate();
		panel.repaint();
	}

	private static void createFrame(JComponent... cs) {
		JFrame frame0 = MainHelper.createFrame();
		JTabbedPane panel = new JTabbedPane();
		int i = 0;
		for (JComponent c : cs) {
			panel.addTab("C" + i, c);
			i++;
		}
		frame0.getContentPane().add(panel);
		frame0.pack();
		frame0.setVisible(true);
	}

	private static JComponent create(String text, JComponent c, int w, int h, boolean selected) {
		JLabel label = new JLabel();
		label.setText(text);
		label.setIcon(new ImageIcon(createMiniature(c, w, h, selected)));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setIconTextGap(2);
		label.setFont(label.getFont().deriveFont(selected ? Font.BOLD : Font.PLAIN, label.getFont().getSize() - 1.0f));
		return label;
	}

	private static Image createMiniature(JComponent c, int w, int h, boolean selected) {
		if (!c.isVisible() && map.containsKey(c)) {
			return map.get(c);
		}
		int mw = w / 2;
		int mh = h / 2;
		Path2D.Float path = new Path2D.Float();
		path.moveTo(0, 0);
		path.lineTo(w - mw - 1, 0);
		path.curveTo(w - 1, 0, w - 1, 0, w - 1, mh);
		path.lineTo(w - 1, h - 1);
		path.lineTo(mw, h - 1);
		path.curveTo(0, h - 1, 0, h - 1, 0, h - mh - 1);
		path.lineTo(0, 0);

		Dimension d = c.getSize();
		Rectangle2D rect = getImageScale(d.width, d.height, w, h, false);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) bi.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Graphics2D g22 = (Graphics2D) g2.create();
		g22.setClip(path);
		g22.setPaint(new LinearGradientPaint(0, 0, w - 1, h - 1, new float[] { 0.0f, 1.0f }, new Color[] { new Color(240, 240, 240), new Color(224, 224, 224) }));
		// g22.setPaint(new RadialGradientPaint(w / 4, h / 4, w, new float[] { 0.0f, 0.2f, 1.0f }, new Color[] { new Color(240, 240, 240), new Color(208, 208, 208), new Color(224, 224, 224) }));
		g22.fillRect(0, 0, w, h);
		g22.translate(rect.getX(), rect.getY());
		g22.scale(rect.getWidth() / d.getWidth(), rect.getHeight() / d.getHeight());
		c.paint(g22);
		g22.dispose();

		if (selected) {
			g2.setColor(new Color(200, 200, 255, 64));
			// g2.fill(path);

			g2.setColor(new Color(238, 124, 32));
		} else {
			g2.setColor(Color.DARK_GRAY);
		}
		g2.setStroke(new BasicStroke(2));
		g2.draw(path);

		g2.dispose();

		map.put(c, bi);

		return bi;
	}

	private static Rectangle2D getImageScale(int w0, int h0, int width, int height, boolean upscale) {
		double widthComponent = width;
		double heightComponent = height;
		double diffComponent = widthComponent / heightComponent;

		double widthImage = w0;
		double heightImage = h0;
		double diffImage = widthImage / heightImage;

		double diff = diffImage / diffComponent;

		double w = widthComponent;
		double h = heightComponent;
		if (diff >= 1.0) {
			h = h / diff;
		} else {
			w = w * diff;
		}

		if (!upscale) {
			w = Math.min(w, widthImage);
			h = Math.min(h, heightImage);
		}

		double sx = (widthComponent - w) / 2.0;
		double sy = (heightComponent - h) / 2.0;

		return new Rectangle2D.Double(sx, sy, w, h);
	}

	private static JComponent createWrapper(JComponent c) {
		JPanel panel = new MyPanelWrapper();
		panel.add(c, BorderLayout.CENTER);
		return panel;
	}

	private static class MyPanelWrapper extends JPanel implements IMiniature {

		public MyPanelWrapper() {
			super(new BorderLayout());
		}

	}

	private static class JImage extends JComponent {

		private Image image;

		public JImage(Image image) {
			super();
			this.image = image;
			setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (image != null) {
				g.drawImage(image, 0, 0, null);
			}
		}
	}

	private interface IMiniature {

	}

	private static JCalendarDay createYearCalendar() {
		DefaultYearCalendarDayModel model = new DefaultYearCalendarDayModel(2014);
		JCalendarDay calendarDay = new JCalendarDay(model);
		calendarDay.setCalendarDayRowHeader(new JCalendarDayRowHeader(calendarDay));
		calendarDay.getCalendarDayRowHeader().setRowHeaderRenderer(new DefaultCalendarDayRowHeaderRenderer());
		calendarDay.getSelectionModel().setSelectionMode(XYSelectionModel.MULTI_SELECTION);
		// calendarDay.setSelectionMode(JCalendarDay.RECTANGLE_SELECTION_MODE);
		return calendarDay;
	}

	private static JXMapViewer createMapViewer() {
		TileFactoryInfo tileFactoryInfo = new OpenStreetMapTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(tileFactoryInfo);
		tileFactory.setTileCache(new TileCache(tileFactoryInfo, false));

		JXMapViewer mapViewer = new JXMapViewer();
		mapViewer.setTileFactory(tileFactory);
		mapViewer.setLoadingImage(null);

		mapViewer.setRealZoom(15.0f);
		mapViewer.setAddressLocation(new GeoPosition(47, 3));
		mapViewer.setDrawTileBorders(false);
		return mapViewer;
	}
}
