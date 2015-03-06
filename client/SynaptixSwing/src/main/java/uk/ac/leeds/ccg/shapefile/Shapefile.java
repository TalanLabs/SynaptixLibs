package uk.ac.leeds.ccg.shapefile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import com.synaptix.swing.SwingMessages;

import cmp.LEDataStream.LEDataInputStream;
import cmp.LEDataStream.LEDataOutputStream;

/**
 * 
 * This class represnts an ESRI Shape file.
 * <p>
 * You construct it with a file name, and later you can read the file's
 * propertys, i.e. Sizes, Types, and the data itself.
 * <p>
 * Copyright 1998 by James Macgill.
 * <p>
 * 
 * Version 1.0beta1.1 (added construct with inputstream) 1.0beta1.2 (made Shape
 * type constants public 18/Aug/98)
 * 
 * This class supports the Shape file as set out in :-<br>
 * <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf"><b>
 * "ESRI(r) Shapefile - A Technical Description"</b><br>
 * <i>'An ESRI White Paper . May 1997'</i></a>
 * <p>
 * 
 * This code is coverd by the GPL.
 * 
 * <a href="mailto:j.macgill@geog.leeds.ac.uk">Mail the Author</a>
 */

public class Shapefile implements Serializable {

	// Some constants

	private final static boolean DEBUG = false;

	static final int SHAPEFILE_ID = 9994;
	static final int VERSION = 1000;

	public static final int NULL = 0;
	public static final int POINT = 1;
	public static final int ARC = 3;
	public static final int POLYGON = 5;
	public static final int MULTIPOINT = 8;
	public static final int ARC_M = 23;
	public static final int UNDEFINED = -1;
	// Types 2,4,6,7 and 9 were undefined at time or writeing

	protected ShapefileHeader mainHeader;
	protected Vector records;

	/**
	 * Creates and initialised a shapefile from disk
	 * 
	 * @param filename
	 *            The filename (including path) of the shapefile
	 */
	public Shapefile(String filename) throws java.io.IOException,
			ShapefileException {
		if (DEBUG)
			System.out
					.println("---->uk.ac.leeds.ccg.shapefile.Shapefile constructed. Will identify itself as Sf-->"); //$NON-NLS-1$
		InputStream in = new FileInputStream(filename);
		LEDataInputStream sfile = new LEDataInputStream(in);
		init(sfile);
	}

	/**
	 * Creates and initialises a shapefile from a url
	 * 
	 * @param url
	 *            The url of the shapefile
	 */
	public Shapefile(URL url) throws java.io.IOException, ShapefileException {
		if (DEBUG)
			System.out
					.println("---->uk.ac.leeds.ccg.shapefile.Shapefile constructed. Will identify itself as Sf-->"); //$NON-NLS-1$
		URLConnection uc = url.openConnection();
		int len = uc.getContentLength();
		if (len <= 0) {
			throw new IOException("Sf-->File feched from URL " + url //$NON-NLS-1$
					+ " was of zero length or could not be found"); //$NON-NLS-1$
		}
		byte data[];
		data = new byte[len];
		BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
		int j = 0, k = 0;
		while (k < len || j == -1) {
			j = in.read(data, k, len - k);
			k += j;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		LEDataInputStream sfile = new LEDataInputStream(bais);
		init(sfile);
	}

	/**
	 * Creates and initialised a shapefile from disk
	 * 
	 * @param file
	 *            a File that represents the shapefile
	 */
	public Shapefile(File file) throws java.io.IOException, ShapefileException {
		InputStream in = new FileInputStream(file);
		LEDataInputStream sfile = new LEDataInputStream(in);
		init(sfile);
	}

	/**
	 * Creates and initalises a shapefile from an inputstream
	 * 
	 * @param inputstream
	 *            with the shapefile is at the other end
	 */
	public Shapefile(InputStream in) throws java.io.IOException,
			ShapefileException {
		LEDataInputStream sfile = new LEDataInputStream(in);
		init(sfile);
	}

	public Shapefile(int shapeType, double[] bbox, ShapefileShape[] shapes) {
		mainHeader = new ShapefileHeader(shapeType, bbox, shapes);
		records = new Vector();
		for (int i = 0; i < shapes.length; i++) {
			records.addElement(new ShapeRecord(i + 1, shapes[i]));
		}
	}

	/**
	 * Initialises a shapefile from disk. Use Shapefile(String) if you don't
	 * want to use LEDataInputStream directly (recomened)
	 * 
	 * @param file
	 *            A LEDataInputStream that conects to the shapefile to read
	 */
	public synchronized void init(LEDataInputStream file) throws IOException,
			ShapefileException {
		mainHeader = new ShapefileHeader(file);
		if (mainHeader.getVersion() < VERSION) {
			System.err.println("Sf-->Warning, Shapefile format (" //$NON-NLS-1$
					+ mainHeader.getVersion() + ") older that supported (" //$NON-NLS-1$
					+ VERSION + "), attempting to read anyway"); //$NON-NLS-1$
		}
		if (mainHeader.getVersion() > VERSION) {
			System.err.println("Sf-->Warning, Shapefile format (" //$NON-NLS-1$
					+ mainHeader.getVersion() + ") newer that supported (" //$NON-NLS-1$
					+ VERSION + "), attempting to read anyway"); //$NON-NLS-1$
		}

		records = new Vector();
		ShapefileShape body;
		RecordHeader header;
		int type = mainHeader.getShapeType();
		try {
			for (;;) {
				header = new RecordHeader(file);
				switch (type) {
				case (POINT):
					body = new ShapePoint(file);
					break;
				case (ARC):
					body = new ShapeArc(file);
					break;
				case (POLYGON):
					body = new ShapePolygon(file);
					break;
				case (ARC_M):
					body = new ShapeArcM(file);
					break;
				default:
					throw new ShapeTypeNotSupportedException("Sf-->Shape type " //$NON-NLS-1$
							+ getShapeTypeDescription() + " [" + type //$NON-NLS-1$
							+ "] not suported"); //$NON-NLS-1$
				}
				records.addElement(new ShapeRecord(header, body));
			}
		} catch (EOFException e) {
			if (DEBUG)
				System.out.println("Finished reading " + records.size() //$NON-NLS-1$
						+ " shapes now at EOF"); //$NON-NLS-1$
		}
	}

	/**
	 * Saves a shapefile to and output stream.
	 * 
	 * @param file
	 *            A LEDataInputStream that conects to the shapefile to read
	 */
	public synchronized void writeShapefile(OutputStream os) throws IOException {
		LEDataOutputStream file = null;
		try {
			BufferedOutputStream out = new BufferedOutputStream(os);
			file = new LEDataOutputStream(out);
		} catch (Exception e) {
			System.err.println(e);
		}
		// System.out.println("Writing header");
		mainHeader.write(file);
		int pos = 50; // header length in WORDS
		// records;
		// body;
		// header;
		if (DEBUG)
			System.out.println("Sf-->Saving " + records.size()); //$NON-NLS-1$
		for (int i = 0; i < records.size(); i++) {
			// System.out.println("Writing Record");
			ShapeRecord item = (ShapeRecord) records.elementAt(i);
			item.mainindex = pos;
			item.header.write(file);
			pos += 4; // length of header in WORDS
			item.shape.write(file);
			pos += item.header.getContentLength(); // length of shape in WORDS

		}
		// System.out.println("Position "+pos);

		file.flush();
		file.close();
	}

	public synchronized void writeIndex(OutputStream os) throws IOException {
		LEDataOutputStream file = null;
		try {
			BufferedOutputStream out = new BufferedOutputStream(os);
			file = new LEDataOutputStream(out);
		} catch (Exception e) {
			System.err.println(e);
		}
		if (DEBUG)
			System.out.println("Writing index file"); //$NON-NLS-1$
		mainHeader.writeToIndex(file);
		int pos = 50;
		int len = 0;
		file.setLittleEndianMode(false);
		for (int i = 0; i < records.size(); i++) {
			// if(DEBUG)System.out.println("Writing index Record "+i);
			ShapeRecord item = (ShapeRecord) records.elementAt(i);
			file.writeInt(item.mainindex); // this should be the offset into the
											// shp file
			len = item.header.getContentLength();
			file.writeInt(len);
			// System.out.println(pos+" "+len+4);
			pos += len + 4;

		}
		file.flush();
		file.close();
	}

	/**
	 * Gets the number of records stored in this shapefile
	 * 
	 * @return Number of records
	 */
	public int getRecordCount() {
		return records.size();
	}

	public Vector getRecords() {
		return records;
	}

	/**
	 * Returns a ShapefileShape If index is out of range a null ShapefileShape
	 * will be returned. (As an alternative I could throw an
	 * ArrayIndexOutOfBoundsException, comments please...)
	 * 
	 * @param index
	 *            The index of the record from which to extract the shape.
	 * @return A ShapefileShape from the given index.
	 */
	public ShapefileShape getShape(int index) {
		ShapeRecord r;
		try {
			r = (ShapeRecord) records.elementAt(index);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return null;
		}
		return r.getShape();
	}

	/**
	 * Returns an array of all the shapes in this shapefile.
	 * 
	 * @return An array of all the shapes
	 */
	public ShapefileShape[] getShapes() {
		ShapefileShape[] shapes = new ShapefileShape[records.size()];
		ShapeRecord r;
		for (int i = 0; i < records.size(); i++) {
			r = (ShapeRecord) records.elementAt(i);
			shapes[i] = r.getShape();
		}
		return shapes;
	}

	/**
	 * Gets the bounding box for the whole shape file.
	 * 
	 * @return An array of four doubles in the form {x1,y1,x2,y2}
	 */
	public double[] getBounds() {
		return mainHeader.getBounds();
	}

	/**
	 * Gets the type of shape stored in this shapefile.
	 * 
	 * @return An int indicating the type
	 * @see #getShapeTypeDescription()
	 * @see #getShapeTypeDescription(int type)
	 */
	public int getShapeType() {
		return mainHeader.getShapeType();
	}

	/**
	 * Returns a string for the shape type of index.
	 * 
	 * @param index
	 *            An int coresponding to the shape type to be described
	 * @return A string descibing the shape type
	 */
	public static String getShapeTypeDescription(int index) {
		switch (index) {
		case (NULL):
			return ("Null"); //$NON-NLS-1$
		case (POINT):
			return ("Points"); //$NON-NLS-1$
		case (ARC):
			return ("Arcs"); //$NON-NLS-1$
		case (ARC_M):
			return ("ArcsM"); //$NON-NLS-1$
		case (POLYGON):
			return ("Polygons"); //$NON-NLS-1$
		case (MULTIPOINT):
			return ("Multipoints"); //$NON-NLS-1$
		default:
			return ("Undefined"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns a description of the shape type stored in this shape file.
	 * 
	 * @return String containing description
	 */
	public String getShapeTypeDescription() {
		return getShapeTypeDescription(mainHeader.getShapeType());
	}

	public synchronized void readIndex(InputStream is) throws IOException {
		LEDataInputStream file = null;
		try {
			BufferedInputStream in = new BufferedInputStream(is);
			file = new LEDataInputStream(in);
		} catch (Exception e) {
			System.err.println(e);
		}
		if (DEBUG)
			System.out.println("Reading index file"); //$NON-NLS-1$
		ShapefileHeader head = new ShapefileHeader(file);

		int pos = 0, len = 0;
		file.setLittleEndianMode(false);
		// for(int i=0;i<records.size();i++){
		// if(DEBUG)System.out.println("Sf-->Reading index Record");
		// ShapeRecord item = (ShapeRecord)records.elementAt(i);
		// if(DEBUG)System.out.println("Sf-->Offset"+file.readShort());
		// len = item.header.getContentLength();
		// if(DEBUG)System.out.println("Sf-->Length"+file.readShort());
		// if(DEBUG)System.out.println("Sf-->"+pos+" "+len);
		// pos+=len;

		// }
		file.close();
	}
}

class ShapefileHeader implements Serializable {
	private final static boolean DEBUG = false;
	private int fileCode = -1;
	private int fileLength = -1;
	private int indexLength = -1;
	private int version = -1;
	private int shapeType = -1;
	private double[] bounds = new double[4];

	public ShapefileHeader(LEDataInputStream file) throws IOException {
		file.setLittleEndianMode(false);
		fileCode = file.readInt();
		if (DEBUG)
			System.out.println("Sfh->Filecode " + fileCode); //$NON-NLS-1$
		if (fileCode != Shapefile.SHAPEFILE_ID)
			System.err.println("Sfh->WARNING filecode " + fileCode //$NON-NLS-1$
					+ " not a match for documented shapefile code " //$NON-NLS-1$
					+ Shapefile.SHAPEFILE_ID);

		// throw new
		// IOException("File ID in header not that of a Shapefile  (Found "+
		// fileCode+" : Expected "+Shapefile.SHAPEFILE_ID+")");

		// file.skipBytes(20);//Skip unused part of header
		for (int i = 0; i < 5; i++) {
			int tmp = file.readInt();
			if (DEBUG)
				System.out.println("Sfh->blank " + tmp); //$NON-NLS-1$
		}
		fileLength = file.readInt();
		if (DEBUG)
			System.out
					.println("Sfh->***************IN FileLength" + fileLength); //$NON-NLS-1$

		file.setLittleEndianMode(true);
		version = file.readInt();
		shapeType = file.readInt();
		if (DEBUG)
			System.out.println("Sfh->IN Type" + shapeType); //$NON-NLS-1$

		// read in the bounding box
		for (int i = 0; i < 4; i++) {
			bounds[i] = file.readDouble();
		}

		// skip remaining unused bytes
		file.setLittleEndianMode(false);// well they may not be unused
										// forever...
		file.skipBytes(32);
	}

	public ShapefileHeader(int shapeType, double[] bbox, ShapefileShape[] shapes) {
		if (DEBUG)
			System.out.println("Sfh->ShapefileHeader constructed with type " //$NON-NLS-1$
					+ shapeType);
		this.shapeType = shapeType;
		version = Shapefile.VERSION;
		fileCode = Shapefile.SHAPEFILE_ID;
		bounds = bbox;
		fileLength = 0;
		for (int i = 0; i < shapes.length; i++) {
			fileLength += shapes[i].getLength();
			fileLength += 4;// for each header
		}
		fileLength += 50;// space used by this, the main header
		indexLength = 50 + (4 * shapes.length);
		if (DEBUG)
			System.out.println("Sfh->********************OUT fileLength = " //$NON-NLS-1$
					+ fileLength);
	}

	public void setFileLength(int fileLength) {
		this.fileLength = fileLength;
	}

	public void setBounds(double[] bbox) {
		bounds = bbox;
	}

	public void write(LEDataOutputStream file) throws IOException {
		int pos = 0;
		file.setLittleEndianMode(false);
		file.writeInt(fileCode);
		pos += 4;
		for (int i = 0; i < 5; i++) {
			file.writeInt(0);// Skip unused part of header
			pos += 4;
		}
		file.writeInt(fileLength);
		pos += 4;
		file.setLittleEndianMode(true);
		file.writeInt(version);
		pos += 4;
		file.writeInt(shapeType);
		pos += 4;
		// read in the bounding box
		for (int i = 0; i < 4; i++) {
			pos += 8;
			file.writeDouble(bounds[i]);
		}

		// skip remaining unused bytes
		// file.setLittleEndianMode(false);//well they may not be unused
		// forever...
		for (int i = 0; i < 4; i++) {
			file.writeDouble(0.0);// Skip unused part of header
			pos += 8;
		}
		if (DEBUG)
			System.out.println("Sfh->Position " + pos); //$NON-NLS-1$
	}

	public void writeToIndex(LEDataOutputStream file) throws IOException {
		int pos = 0;
		file.setLittleEndianMode(false);
		file.writeInt(fileCode);
		pos += 4;
		for (int i = 0; i < 5; i++) {
			file.writeInt(0);// Skip unused part of header
			pos += 4;
		}
		file.writeInt(indexLength);
		pos += 4;
		file.setLittleEndianMode(true);
		file.writeInt(version);
		pos += 4;
		file.writeInt(shapeType);
		pos += 4;
		// write the bounding box
		for (int i = 0; i < 4; i++) {
			pos += 8;
			file.writeDouble(bounds[i]);
		}

		// skip remaining unused bytes
		// file.setLittleEndianMode(false);//well they may not be unused
		// forever...
		for (int i = 0; i < 4; i++) {
			file.writeDouble(0.0);// Skip unused part of header
			pos += 8;
		}
		if (DEBUG)
			System.out.println("Sfh->Index Position " + pos); //$NON-NLS-1$
	}

	public int getShapeType() {
		return shapeType;
	}

	public int getVersion() {
		return version;
	}

	public double[] getBounds() {
		return bounds;
	}

	public String toString() {
		String res = new String("Sf-->type " + fileCode + " size " + fileLength //$NON-NLS-1$ //$NON-NLS-2$
				+ " version " + version + " Shape Type " + shapeType); //$NON-NLS-1$ //$NON-NLS-2$
		return res;
	}
}

class ShapeRecord implements Serializable {
	RecordHeader header;
	ShapefileShape shape;
	int mainindex = -1;

	public ShapeRecord(RecordHeader header, ShapefileShape shape) {
		this.header = header;
		this.shape = shape;
	}

	public ShapeRecord(int index, ShapefileShape shape) {
		this.header = new RecordHeader(index, shape);
		this.shape = shape;
	}

	public int getShapeType() {
		return shape.getShapeType();
	}

	public ShapefileShape getShape() {
		return shape;
	}
}

class RecordHeader implements Serializable {
	private int recordNumber = -1;
	private int contentLength = -1;

	public RecordHeader(LEDataInputStream file) throws IOException {
		file.setLittleEndianMode(false);
		recordNumber = file.readInt();
		contentLength = file.readInt();
		// System.out.println("In contentLength = "+recordNumber+"   "+
		// contentLength);
	}

	public RecordHeader(int count, ShapefileShape shape) {
		recordNumber = count;
		contentLength = shape.getLength();
		// System.out.println("OUT contentLength = "+recordNumber+"   "+
		// contentLength);

	}

	public void write(LEDataOutputStream file) throws IOException {
		file.setLittleEndianMode(false);
		file.writeInt(recordNumber);
		file.writeInt(contentLength);
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public int getContentLength() {
		return contentLength;
	}
}
