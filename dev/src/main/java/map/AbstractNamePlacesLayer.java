package map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.LineMetrics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;
import org.jdesktop.swingx.util.GraphicsUtilities;

public abstract class AbstractNamePlacesLayer extends AbstractLayer {

	private static final Log LOG = LogFactory.getLog(AbstractNamePlacesLayer.class);

	private static final Font loadingFont = new Font("Arial", Font.BOLD, 12);

	private static final Color loadingColor = new Color(128, 0, 0);

	private File dirFile;

	private JXMapViewer mapViewer;

	private MyImage currentImage;

	private MyImage loadingImage;

	private boolean loading;

	private long maxFileCacheTime = 7 * 24 * 60 * 60 * 1000; // 7 days

	private int maxFileCount = 50000;

	private String baseUrl;

	private String realBaseUrl;

	private String layer;

	private ExecutorService executorService = Executors.newFixedThreadPool(1);

	public AbstractNamePlacesLayer(JXMapViewer mapViewer, String baseUrl, String layer) {
		super();

		this.loading = false;
		this.baseUrl = baseUrl;
		this.realBaseUrl = baseUrl;
		this.layer = layer;

		this.setAntialiasing(true);
		this.setDirty(true);

		this.mapViewer = mapViewer;

		try {
			String name = "JXMapViewer/" + URLEncoder.encode(realBaseUrl, "UTF-8");
			this.dirFile = new File(System.getProperty("java.io.tmpdir"), name);
			this.dirFile.mkdirs();

			LOG.info("Répertoire de cache des fichiers : " + dirFile);
		} catch (Exception e) {
			LOG.error("Impossible de créer le répertoire de cache", e);
		}
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		TileFactory tileFactory = mapViewer.getTileFactory();

		double zoom = mapViewer.getRealZoom();
		Point2D center = tileFactory.geoToPixel(mapViewer.getCenterPosition(), zoom);
		double viewportX = (center.getX() - dc.getDrawableWidth() / 2.0);
		double viewportY = (center.getY() - dc.getDrawableHeight() / 2.0);

		int tileSize = tileFactory.getTileSize(zoom);
		int imageX = (int) (Math.floor(viewportX / tileSize) * tileSize);
		int imageW = (int) ((Math.floor(dc.getDrawableWidth() / tileSize) + 2) * tileSize);
		int imageY = (int) (Math.floor(viewportY / tileSize) * tileSize);
		int imageH = (int) ((Math.floor(dc.getDrawableHeight() / tileSize) + 2) * tileSize);

		if ((currentImage == null && !loading)
				|| (currentImage != null
						&& (zoom != currentImage.zoom || imageX != currentImage.imageX || imageY != currentImage.imageY || imageW != currentImage.imageW || imageH != currentImage.imageH) && (loadingImage == null || (loadingImage != null && ((zoom != loadingImage.zoom
						|| imageX != loadingImage.imageX || imageY != loadingImage.imageY || imageW != loadingImage.imageW || imageH != loadingImage.imageH)))))) {
			loadImage(tileFactory, imageX, imageY, imageW, imageH, zoom);
		}
		if (currentImage != null) {
			double x = currentImage.imageX - viewportX;
			double y = currentImage.imageY - viewportY;
			g.drawImage(currentImage.image, (int) x, (int) y, currentImage.imageW, currentImage.imageH, null);
			// g.setColor(Color.red);
			// g.drawRect((int) x, (int) y, currentImage.imageW,
			// currentImage.imageH);
		}
		if (loading) {
			String text = "Chargement en cours...";
			g.setColor(loadingColor);
			g.setFont(loadingFont);
			LineMetrics lm = loadingFont.getLineMetrics(text, g.getFontRenderContext());
			g.drawString(text, 1, lm.getAscent() + 1);
		}
	}

	private SwingWorker<BufferedImage, Void> swingWorker;

	private void loadImage(final TileFactory tileFactory, final int imageX, final int imageY, final int imageW, final int imageH, final double zoom) {
		if (loadingImage != null && zoom == loadingImage.zoom && imageX == loadingImage.imageX && imageY == loadingImage.imageY && imageW == loadingImage.imageW && imageH == loadingImage.imageH) {
			return;
		}
		loading = true;
		loadingImage = new MyImage();
		loadingImage.zoom = zoom;
		loadingImage.imageX = imageX;
		loadingImage.imageY = imageY;
		loadingImage.imageW = imageW;
		loadingImage.imageH = imageH;

		if (currentImage != null && zoom != currentImage.zoom) {
			currentImage = null;
		}
		if (swingWorker != null) {
			swingWorker.cancel(true);
			swingWorker = null;
		}

		swingWorker = new SwingWorker<BufferedImage, Void>() {

			private URI uri;

			@Override
			protected BufferedImage doInBackground() throws Exception {
				GeoPosition gp1 = tileFactory.pixelToGeo(new Point2D.Double(imageX, imageY), zoom);
				GeoPosition gp2 = tileFactory.pixelToGeo(new Point2D.Double(imageX + imageW, imageY + imageH), zoom);
				Point2D p1 = convertGeoPositiontoPoint2D(gp1);
				Point2D p2 = convertGeoPositiontoPoint2D(gp2);

				String res = toWMSURL(p1.getX(), p2.getY(), p2.getX(), p1.getY(), imageW, imageH);
				uri = new URI(new StringBuilder(baseUrl).append(res).toString());
				BufferedImage bi = getImageCache(p1.getX(), p2.getY(), p2.getX(), p1.getY(), imageW, imageH);
				if (bi == null) {
					bi = GraphicsUtilities.loadCompatibleImage(uri.toURL());
					addToImageCache(p1.getX(), p2.getY(), p2.getX(), p1.getY(), imageW, imageH, bi);
				}
				return bi;
			}

			@Override
			protected void done() {
				try {
					BufferedImage image = get();
					loading = false;
					currentImage = new MyImage();
					currentImage.image = image;
					currentImage.zoom = zoom;
					currentImage.imageX = imageX;
					currentImage.imageY = imageY;
					currentImage.imageW = imageW;
					currentImage.imageH = imageH;

					loadingImage = null;

					mapViewer.repaint();
				} catch (CancellationException e) {
					// Ignore
				} catch (Exception e) {
					LOG.error("Not read image " + uri);
					loading = false;
					mapViewer.repaint();
				}
			}
		};
		swingWorker.execute();
	}

	protected abstract Point2D convertGeoPositiontoPoint2D(GeoPosition gp);

	protected String toWMSURL(double ulx, double uly, double lrx, double lry, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append("?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application/vnd.ogc.se_inimage&TRANSPARENT=true&FORMAT=image/gif");
		sb.append("&SRS=").append(getSRS());
		sb.append("&layers=").append(layer);
		sb.append("&width=").append(width);
		sb.append("&height=").append(height);
		sb.append("&bbox=").append(ulx).append(",").append(uly).append(",").append(lrx).append(",").append(lry);
		return sb.toString();
	}

	protected abstract String getSRS();

	public BufferedImage getImageCache(double ulx, double uly, double lrx, double lry, int width, int height) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("&width=").append(width);
		sb.append("&height=").append(height);
		sb.append("&bbox=").append(ulx).append(",").append(uly).append(",").append(lrx).append(",").append(lry).append(".gif");
		BufferedImage res = null;
		String name = URLEncoder.encode(sb.toString(), "UTF-8");
		File file = new File(dirFile, name);
		if (file.exists()) {
			long time = new Date().getTime();
			if (time - file.lastModified() < maxFileCacheTime) {
				BufferedImage img = GraphicsUtilities.loadCompatibleImage(new FileInputStream(file));
				return img;
			}
		}
		return res;
	}

	private void addToImageCache(double ulx, double uly, double lrx, double lry, int width, int height, BufferedImage img) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("&width=").append(width);
			sb.append("&height=").append(height);
			sb.append("&bbox=").append(ulx).append(",").append(uly).append(",").append(lrx).append(",").append(lry).append(".gif");
			String name = URLEncoder.encode(sb.toString(), "UTF-8");
			File file = new File(dirFile, name);
			if (file.exists()) {
				file.delete();
			}
			ImageIO.write(img, "gif", file);

			executorService.execute(fileCacheRunnable);
		} catch (Exception e) {
			LOG.error("Impossible d'ecrire le fichier", e);
		}
	}

	private FileCacheRunnable fileCacheRunnable = new FileCacheRunnable();

	private final class FileCacheRunnable implements Runnable {

		@Override
		public void run() {
			File[] files = dirFile.listFiles(fileFilter);
			if (files.length > maxFileCount) {
				int nb = files.length - maxFileCount;
				Arrays.sort(files, fileComparator);

				LOG.info("Suppression de " + nb + " image(s) du cache");

				for (int i = 0; i < nb; i++) {
					try {
						files[i].delete();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	private FileFilter fileFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return pathname.isFile();
		}
	};

	private Comparator<File> fileComparator = new Comparator<File>() {
		@Override
		public int compare(File o1, File o2) {
			if (o1.lastModified() < o2.lastModified()) {
				return -1;
			} else if (o1.lastModified() > o2.lastModified()) {
				return 1;
			}
			return 0;
		}
	};

	private final class MyImage {

		BufferedImage image;

		double zoom;

		int imageX;

		int imageY;

		int imageW;

		int imageH;

	}
}
