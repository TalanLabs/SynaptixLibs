package com.synaptix.swing.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

public class Utils {

	private static final String ELLIPSIS = "..."; //$NON-NLS-1$

	private static double[] cosTable;

	static {
		cosTable = new double[360];
		for (int i = 0; i < 360; i++) {
			cosTable[i] = Math.cos(Math.toRadians(i));
		}
	}

	public static double cos(int angdeg) {
		return cosTable[angdeg];
	}

	public static String formatIntegerToString(int value, int max) {
		String s1 = String.valueOf(value);
		String s2 = String.valueOf(max);

		int size = s2.length() - s1.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(s1);
		return sb.toString();
	}

	public static void downloadFromUrl(URL url, File localFile)
			throws Exception {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			localFile.createNewFile();
			out = new BufferedOutputStream(new FileOutputStream(localFile));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				// numWritten += numRead;
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public static File createFileFromResource(String resource, String ext)
			throws Exception {
		URL url = Utils.class.getResource(resource);
		File file = File.createTempFile("Resource", ext); //$NON-NLS-1$
		downloadFromUrl(url, file);
		return file;
	}

	public static String post(URL url, Map<String, String> map, String encoding)
			throws IOException {
		String answer = "NORESP"; //$NON-NLS-1$
		OutputStreamWriter osw = null;
		StringBuilder sbAnswer = new StringBuilder();
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection)
			((HttpURLConnection) connection).setRequestMethod("POST"); //$NON-NLS-1$
		connection.setDoOutput(true);
		connection.connect();
		try {
			OutputStream os = connection.getOutputStream();
			osw = new OutputStreamWriter(os);
			StringBuilder sbo = new StringBuilder();
			int i = 0;
			for (Entry<String, String> entry : map.entrySet()) {
				if (i != 0)
					sbo.append("&"); //$NON-NLS-1$
				sbo.append(entry.getKey());
				sbo.append("="); //$NON-NLS-1$
				sbo.append(URLEncoder.encode(entry.getValue(), encoding));
				i++;
			}
			osw.write(sbo.toString());
		} finally {
			if (osw != null)
				osw.close();
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			while ((answer = br.readLine()) != null) {
				if (answer != null)
					sbAnswer.append(answer);
			}
		} finally {
			if (br != null)
				br.close();
		}
		return sbAnswer.toString();
	}

	public static String getResourceAsString(String name, String encoded) {
		// Writer outChar = new OutputStreamWriter (System.out, "UTF-8") ;
		Writer out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Utils.class
					.getResourceAsStream(name), encoded));
			out = new StringWriter();

			char[] buffer = new char[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (out != null) {
			return out.toString();
		}
		return null;
	}

	public static String getResourceAsString(String name) {
		Writer out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(Utils.class
					.getResourceAsStream(name)));
			out = new StringWriter();

			char[] buffer = new char[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (out != null) {
			return out.toString();
		}
		return null;
	}

	public static void expandJTree(javax.swing.JTree tree, int depth) {
		javax.swing.tree.TreeModel model = tree.getModel();
		expandJTreeNode(tree, model, model.getRoot(), 0, depth);
	}

	private static int expandJTreeNode(javax.swing.JTree tree,
			javax.swing.tree.TreeModel model, Object node, int row, int depth) {
		if (node != null && !model.isLeaf(node)) {
			tree.expandRow(row);
			if (depth != 0) {
				for (int index = 0; row + 1 < tree.getRowCount()
						&& index < model.getChildCount(node); index++) {
					row++;
					Object child = model.getChild(node, index);
					if (child == null)
						break;
					javax.swing.tree.TreePath path;
					while ((path = tree.getPathForRow(row)) != null
							&& path.getLastPathComponent() != child)
						row++;
					if (path == null)
						break;
					row = expandJTreeNode(tree, model, child, row, depth - 1);
				}
			}
		}
		return row;
	}

	public static String getShortString(String text, int max) {
		if (text == null || text.length() < max) {
			return text;
		} else {
			return text.substring(0, max - 3) + "..."; //$NON-NLS-1$
		}
	}

	public static void decompressZipFile(final File file, final File folder,
			final boolean deleteZipAfter) throws IOException {
		final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				new FileInputStream(file.getCanonicalFile())));
		ZipEntry ze;
		try {
			// Parcourt tous les fichiers
			while (null != (ze = zis.getNextEntry())) {
				final File f = new File(folder.getCanonicalPath(), ze.getName());
				if (f.exists())
					f.delete();

				// Création des dossiers
				if (ze.isDirectory()) {
					f.mkdirs();
					continue;
				}
				f.getParentFile().mkdirs();
				final OutputStream fos = new BufferedOutputStream(
						new FileOutputStream(f));

				// Ecriture des fichiers
				try {
					try {
						final byte[] buf = new byte[8192];
						int bytesRead;
						while (-1 != (bytesRead = zis.read(buf)))
							fos.write(buf, 0, bytesRead);
					} finally {
						fos.close();
					}
				} catch (final IOException ioe) {
					f.delete();
					throw ioe;
				}
			}
		} finally {
			zis.close();
		}
		if (deleteZipAfter)
			file.delete();
	}

	public static String convertByteArrayToString(final byte[] data) {
		if (data == null) {
			return "null"; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(String.valueOf(data[i]));
			if (i < data.length - 1) {
				sb.append(" "); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public static byte[] convertStringToByteArray(final String string) {
		if (string.equals("null")) { //$NON-NLS-1$
			return null;
		}
		String[] ls = string.split(" "); //$NON-NLS-1$
		byte[] bs = new byte[ls.length];
		for (int i = 0; i < ls.length; i++) {
			try {
				bs[i] = Byte.parseByte(ls[i].trim());
			} catch (Exception e) {
			}
		}
		return bs;
	}

	public static byte[] convertObjectToByteArray(final Serializable o) {
		ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oOS = new ObjectOutputStream(bAOS);
			oOS.writeObject(o);
			oOS.flush();
			oOS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bAOS.toByteArray();
	}

	public static Serializable convertByteArrayToObject(final byte[] data) {
		Serializable res = null;
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(data);
			ObjectInputStream oi = new ObjectInputStream(bi);
			res = (Serializable) oi.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static String getClippedText(String text, FontMetrics fm,
			int maxWidth) {
		if ((text == null) || (text.length() == 0)) {
			return ""; //$NON-NLS-1$
		}
		int width = SwingUtilities.computeStringWidth(fm, text);
		if (width > maxWidth) {
			int totalWidth = SwingUtilities.computeStringWidth(fm, ELLIPSIS);
			for (int i = 0; i < text.length(); i++) {
				totalWidth += fm.charWidth(text.charAt(i));
				if (totalWidth > maxWidth) {
					return text.substring(0, i) + ELLIPSIS;
				}
			}
		}
		return text;
	}

	/**
	 * Ajoute un mouse listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static void addAllMouseListener(Component c, MouseListener l) {
		if (c.getMouseListeners().length > 0
				|| c.getMouseMotionListeners().length > 0
				|| c.getMouseWheelListeners().length > 0) {
			c.addMouseListener(l);
		}

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				addAllMouseListener(c1, l);
			}
		}
	}

	/**
	 * Efface un mouse listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static void removeAllMouseListener(Component c, MouseListener l) {
		c.removeMouseListener(l);

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				removeAllMouseListener(c1, l);
			}
		}
	}

	/**
	 * Ajoute un mouse motion listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static final void addAllMouseMotionListener(Component c,
			MouseMotionListener l) {
		if (c.getMouseListeners().length > 0
				|| c.getMouseMotionListeners().length > 0
				|| c.getMouseWheelListeners().length > 0) {
			c.addMouseMotionListener(l);
		}

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				addAllMouseMotionListener(c1, l);
			}
		}
	}

	/**
	 * Efface un mouse motion listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static final void removeAllMouseMotionListener(Component c,
			MouseMotionListener l) {
		c.removeMouseMotionListener(l);

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				removeAllMouseMotionListener(c1, l);
			}
		}
	}

	/**
	 * Ajoute un mouse wheel listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static final void addAllMouseWheelListener(Component c,
			MouseWheelListener l) {
		if (c.getMouseListeners().length > 0
				|| c.getMouseWheelListeners().length > 0
				|| c.getMouseWheelListeners().length > 0) {
			c.addMouseWheelListener(l);
		}

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				addAllMouseWheelListener(c1, l);
			}
		}
	}

	/**
	 * Efface un mouse wheel listener sur tous les composants fils et lui même
	 * 
	 * @param c
	 * @param l
	 */
	public static final void removeAllMouseWheelListener(Component c,
			MouseWheelListener l) {
		c.removeMouseWheelListener(l);

		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (Component c1 : cs) {
				removeAllMouseWheelListener(c1, l);
			}
		}
	}

	public static final void scrollByUnits(JScrollBar scrollbar, int direction, int units,
			boolean limitToBlock) {
		// This method is called from BasicScrollPaneUI to implement wheel
		// scrolling, as well as from scrollByUnit().
		int delta;
		int limit = -1;

		if (limitToBlock) {
			if (direction < 0) {
				limit = scrollbar.getValue()
						- scrollbar.getBlockIncrement(direction);
			} else {
				limit = scrollbar.getValue()
						+ scrollbar.getBlockIncrement(direction);
			}
		}

		for (int i = 0; i < units; i++) {
			if (direction > 0) {
				delta = scrollbar.getUnitIncrement(direction);
			} else {
				delta = -scrollbar.getUnitIncrement(direction);
			}

			int oldValue = scrollbar.getValue();
			int newValue = oldValue + delta;

			// Check for overflow.
			if (delta > 0 && newValue < oldValue) {
				newValue = scrollbar.getMaximum();
			} else if (delta < 0 && newValue > oldValue) {
				newValue = scrollbar.getMinimum();
			}
			if (oldValue == newValue) {
				break;
			}

			if (limitToBlock && i > 0) {
				assert limit != -1;
				if ((direction < 0 && newValue < limit)
						|| (direction > 0 && newValue > limit)) {
					break;
				}
			}
			scrollbar.setValue(newValue);
		}
	}
	
	public static final void scrollByBlock(JScrollBar scrollbar, int direction) {
        // This method is called from BasicScrollPaneUI to implement wheel
        // scrolling, and also from scrollByBlock().
	    int oldValue = scrollbar.getValue();
	    int blockIncrement = scrollbar.getBlockIncrement(direction);
	    int delta = blockIncrement * ((direction > 0) ? +1 : -1);
	    int newValue = oldValue + delta;
	    
	    // Check for overflow.
	    if (delta > 0 && newValue < oldValue) {
		newValue = scrollbar.getMaximum();
	    }
	    else if (delta < 0 && newValue > oldValue) {
		newValue = scrollbar.getMinimum();
	    }

	    scrollbar.setValue(newValue);			
    }
}
