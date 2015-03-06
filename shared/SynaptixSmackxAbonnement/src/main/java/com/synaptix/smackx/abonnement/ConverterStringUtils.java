package com.synaptix.smackx.abonnement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jivesoftware.smack.util.Base64;

public final class ConverterStringUtils {

	private ConverterStringUtils() {
	}

	public static String objectToString(Object o) throws Exception {
		String res = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.flush();
			baos.flush();

			res = Base64.encodeBytes(baos.toByteArray(), Base64.GZIP);
		} finally {
			if (oos != null) {
				try {
					oos.close();
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
		return res;
	}

	public static Object stringToObject(String data) throws Exception {
		Object res = null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			byte[] bytes = Base64.decode(data, Base64.GZIP);

			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			res = ois.readObject();
		} finally {
			if (ois != null) {
				try {
					ois.close();
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
