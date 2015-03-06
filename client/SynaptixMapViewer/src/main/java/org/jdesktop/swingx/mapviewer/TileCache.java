/*
 * TileCache.java
 *
 * Created on January 2, 2007, 7:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.util.GraphicsUtilities;

/**
 * An implementation only class for now. For internal use only.
 * 
 * @author joshua.marinacci@sun.com
 * @author Gaby
 */
public class TileCache {

	private static final Log LOG = LogFactory.getLog(TileCache.class);

	private final TileFactoryInfo tileFactoryInfo;

	private final boolean useFileAsCache;

	private File dirFile;

	private Map<URI, BufferedImage> imgmap = new HashMap<URI, BufferedImage>();

	private LinkedList<URI> imgmapAccessQueue = new LinkedList<URI>();

	private int imagesize = 0;

	private int imagesizeMax = 1000 * 1000 * 50;

	private Map<URI, byte[]> bytemap = new HashMap<URI, byte[]>();

	private LinkedList<URI> bytemapAccessQueue = new LinkedList<URI>();

	private int bytesize = 0;

	private int bytesizeMax = 1000 * 1000 * 50;

	private long maxFileCacheTime = 7 * 24 * 60 * 60 * 1000; // 7 days

	private int maxFileCount = 50000;

	private ExecutorService executorService = Executors.newFixedThreadPool(1);

	public TileCache(TileFactoryInfo tileFactoryInfo) {
		this(tileFactoryInfo, true);
	}

	/**
	 * Identifiant qui permet de retrouver le répertoire de cache
	 * 
	 * @param id
	 */
	public TileCache(TileFactoryInfo tileFactoryInfo, boolean useFileAsCache) {
		super();

		this.tileFactoryInfo = tileFactoryInfo;
		this.useFileAsCache = useFileAsCache;

		if (useFileAsCache) {
			try {
				String name = "JXMapViewer/" + URLEncoder.encode(tileFactoryInfo.getBaseURL(), "UTF-8");
				this.dirFile = new File(System.getProperty("java.io.tmpdir"), name);
				this.dirFile.mkdirs();

				LOG.info("Répertoire de cache des fichiers : " + dirFile);
			} catch (Exception e) {
				LOG.error("Impossible de créer le répertoire de cache", e);
			}
		}
	}

	public void dispose() {
		executorService.shutdown();
	}

	/**
	 * Répertoire d'enregistrement du cache
	 * 
	 * @return
	 */
	public File getDirFile() {
		return dirFile;
	}

	/**
	 * Répertoire d'enregistrement du cache
	 * 
	 * @param dirFile
	 */
	public void setDirFile(File dirFile) {
		this.dirFile = dirFile;
	}

	/**
	 * Taille max du cache local pour la version compressée
	 * 
	 * @return
	 */
	public int getBytesizeMax() {
		return bytesizeMax;
	}

	/**
	 * Taille max du cache local pour la version compressée
	 * 
	 * @param bytesizeMax
	 */
	public void setBytesizeMax(int bytesizeMax) {
		this.bytesizeMax = bytesizeMax;
	}

	/**
	 * Taille max du cache local pour la version non compressée
	 * 
	 * @return
	 */
	public int getImagesizeMax() {
		return imagesizeMax;
	}

	/**
	 * Taille max du cache local pour la version non compressée
	 * 
	 * @param imagesizeMax
	 */
	public void setImagesizeMax(int imagesizeMax) {
		this.imagesizeMax = imagesizeMax;
	}

	/**
	 * Temps max en milliseconde du cache fichier
	 * 
	 * @return
	 */
	public long getMaxFileCacheTime() {
		return maxFileCacheTime;
	}

	/**
	 * Temps max en milliseconde du cache fichier
	 * 
	 * @param maxCacheTime
	 */
	public void setMaxFileCacheTime(long maxCacheTime) {
		this.maxFileCacheTime = maxCacheTime;
	}

	/**
	 * Nombre max de fichier dans le cache local
	 * 
	 * @return
	 */
	public int getMaxFileCount() {
		return maxFileCount;
	}

	/**
	 * Nombre max de fichier dans le cache local
	 * 
	 * @param maxFileCount
	 */
	public void setMaxFileCount(int maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public TileFactoryInfo getTileFactoryInfo() {
		return tileFactoryInfo;
	}

	/**
	 * Put a tile image into the cache. This puts both a buffered image and array of bytes that make up the compressed image.
	 * 
	 * @param uri
	 *            URI of image that is being stored in the cache
	 * @param bimg
	 *            bytes of the compressed image, ie: the image file that was loaded over the network
	 * @param img
	 *            image to store in the cache
	 */
	public void put(URI uri, byte[] bimg, BufferedImage img) {
		synchronized (bytemap) {
			while (bytesize > bytesizeMax) {
				URI olduri = bytemapAccessQueue.removeFirst();
				byte[] oldbimg = bytemap.remove(olduri);
				bytesize -= oldbimg.length;
				p("removed 1 img from byte cache");
			}

			bytemap.put(uri, bimg);
			bytesize += bimg.length;
			bytemapAccessQueue.addLast(uri);
		}
		addToImageCache(uri, img, true);
	}

	/**
	 * Returns a buffered image for the requested URI from the cache. This method must return null if the image is not in the cache. If the image is unavailable but it's compressed version *is*
	 * available, then the compressed version will be expanded and returned.
	 * 
	 * @param uri
	 *            URI of the image previously put in the cache
	 * @return the image matching the requested URI, or null if not available
	 * @throws java.io.IOException
	 */
	public BufferedImage get(URI uri) throws IOException {
		BufferedImage res = null;
		synchronized (imgmap) {
			res = imgmap.get(uri);
			if (res != null) {
				imgmapAccessQueue.remove(uri);
				imgmapAccessQueue.addLast(uri);
				return res;
			}
		}
		synchronized (bytemap) {
			byte[] bs = bytemap.get(uri);
			if (bs != null) {
				p("retrieving from bytes");
				bytemapAccessQueue.remove(uri);
				bytemapAccessQueue.addLast(uri);
				BufferedImage img = GraphicsUtilities.loadCompatibleImage(new ByteArrayInputStream(bs));
				addToImageCache(uri, img, true);
				return img;
			}
		}
		if (useFileAsCache) {
			try {
				String name = getFilename(uri);
				File file = new File(dirFile, name);
				if (file.exists()) {
					long time = new Date().getTime();
					if (time - file.lastModified() < maxFileCacheTime) {
						BufferedImage img = GraphicsUtilities.loadCompatibleImage(new FileInputStream(file));
						addToImageCache(uri, img, false);
						return img;
					}
				}
			} catch (Exception e) {
				LOG.error("Impossible de lire le fichier", e);
			}
		}
		return res;
	}

	/**
	 * Request that the cache free up some memory. How this happens or how much memory is freed is up to the TileCache implementation. Subclasses can implement their own strategy. The default strategy
	 * is to clear out all buffered images but retain the compressed versions.
	 */
	public void needMoreMemory() {
		imgmap.clear();
		p("HACK! need more memory: freeing up memory");
	}

	protected String getFilename(URI uri) throws Exception {
		String s = uri.toString();
		s = s.startsWith(tileFactoryInfo.getBaseURL()) ? s.substring(tileFactoryInfo.getBaseURL().length()) : s;
		return URLEncoder.encode(s, "UTF-8");
	}

	protected void addToImageCache(final URI uri, final BufferedImage img, boolean saveTempFile) {
		synchronized (imgmap) {
			while (imagesize > imagesizeMax) {
				URI olduri = imgmapAccessQueue.removeFirst();
				BufferedImage oldimg = imgmap.remove(olduri);
				imagesize -= oldimg.getWidth() * oldimg.getHeight() * 4;
				p("removed 1 img from image cache");
			}

			imgmap.put(uri, img);
			imagesize += img.getWidth() * img.getHeight() * 4;
			imgmapAccessQueue.addLast(uri);

		}
		if (useFileAsCache && saveTempFile) {
			try {
				String name = getFilename(uri);
				File file = new File(dirFile, name);
				if (file.exists()) {
					file.delete();
				}
				ImageIO.write(img, tileFactoryInfo.getFormatImage(), file);

				executorService.execute(fileCacheRunnable);
			} catch (Exception e) {
				LOG.error("Impossible d'ecrire le fichier", e);
			}
		}
		p("added to cache: " + " uncompressed = " + imgmap.keySet().size() + " / " + imagesize / 1000 + "k" + " compressed = " + bytemap.keySet().size() + " / " + bytesize / 1000 + "k");
	}

	private void p(String string) {
		LOG.debug(string);
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
}
