package com.synaptix.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class XmlObjectUtils {

	private XmlObjectUtils() {
	}

	// private static String encodeBase64(byte[] data) {
	// return encodeBase64(data, false);
	// }
	//
	// private static String encodeBase64(byte[] data, boolean lineBreaks) {
	// return encodeBase64(data, 0, data.length, lineBreaks);
	// }
	//
	// private static String encodeBase64(byte[] data, int offset, int len,
	// boolean lineBreaks) {
	// return Base64.encodeBytes(data, offset, len,
	// (lineBreaks ? Base64.NO_OPTIONS : Base64.DONT_BREAK_LINES));
	// }
	//
	// private static byte[] decodeBase64(String data) {
	// return Base64.decode(data);
	// }

	public static String objectToString(Object o) throws Exception {
		ByteArrayOutputStream baos = null;
		GZIPOutputStream gzos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			// gzos = new GZIPOutputStream(baos);
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.flush();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (Exception e) {
				}
			}
			if (gzos != null) {
				try {
					gzos.close();
				} catch (Exception e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
		}
		return new String(Base64Coder.encode(baos.toByteArray()));
	}

	public static Object stringToObject(String data) throws Exception {
		Object res = null;

		byte[] buffer = Base64Coder.decode(data);
		ByteArrayInputStream bais = null;
		GZIPInputStream gzis = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(buffer);
			// gzis = new GZIPInputStream(bais);
			ois = new ObjectInputStream(bais);
			res = ois.readObject();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (Exception e) {
				}
			}
			if (gzis != null) {
				try {
					gzis.close();
				} catch (Exception e) {
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (Exception e) {
				}
			}
		}
		return res;
	}
}
