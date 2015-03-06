/*
 * Created on 31 dec. 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.synaptix.toolkits.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author sps
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PropertiesKit {

	public static boolean fill(
		Properties props,
		InputStream is,
		boolean close) {
		try {
			props.load(is);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (close)
					is.close();
			} catch (IOException e) {
			}
		}
	}

	public static Properties load(InputStream is, boolean nullOnError) {
		Properties props = new Properties();
		
		boolean r = fill(props, is, true);
		if ((!r) && (nullOnError)) {
			return null;
		}
		return props;
	}
	public static Properties load(File file, boolean nullOnError) {
		try {
			FileInputStream fis = new FileInputStream(file);
			return load(fis, nullOnError);
		} catch (FileNotFoundException e) {
			if (nullOnError)
				return null;
			else
				return new Properties();
		}
	}

	public static boolean fill(Properties props, String ressource, Class cls) {
		ClassLoader cl = cls.getClassLoader();
		InputStream is = cl.getResourceAsStream(ressource);
		if (is != null) {
			return fill(props, is, true);
		} else
			return false;
	}
	public static boolean fill(Properties props, String ressource, ClassLoader cl) {
		InputStream is = cl.getResourceAsStream(ressource);
		if (is != null) {
			return fill(props, is, true);
		} else
			return false;
	}

	public static Properties load(
			String ressource,
			Class cls,
			boolean nullOnError) {
			Properties props = new Properties();
			boolean r = fill(props, ressource, cls);
			if ((!r) && (nullOnError)) {
				return null;
			}
			return props;
		}

	public static Properties load(
			String ressource,
			ClassLoader cl,
			boolean nullOnError) {
			Properties props = new Properties();
			boolean r = fill(props, ressource, cl);
			if ((!r) && (nullOnError)) {
				return null;
			}
			return props;
		}

	public static Properties extract(Object data, Map mapping) {
		Properties props = new Properties();
		Iterator it = mapping.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			Object val = mapping.get(k);
			Method mth = null;
			if (val instanceof String) {
				String mthname = (String) val;
				try {
					mth = data.getClass().getMethod(mthname, new Class[0]);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				if (mth != null)
					mapping.put(k, mth);
			} else {
				mth = (Method) val;
				System.out.println("found method "+mth.getName());
			}
			if (mth != null) {
				try {
					Object result = mth.invoke(data, new Object[0]);
					if (result != null) {
						props.put(k, result);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}

	public static File save(Properties props, File path, String idKey, String generatedFileKey, String extension){
		String filePrefix = "";
		if (idKey != null){
			filePrefix = props.getProperty(idKey);
		}
		if (extension == null)
			extension = "properties";
		String tstamp = Long.toString(System.currentTimeMillis());
		String filename = filePrefix+tstamp+"."+extension;
		File f = new File(path,filename);
		if (generatedFileKey!= null){
			props.put(generatedFileKey+".name",f.getName());
			props.put(generatedFileKey+".path",f.getParent());
		}
		try{
			FileOutputStream fos = new FileOutputStream(f);
			try{
				props.store(fos,"auto generated");
				return f;
			}catch (IOException e) {
				e.printStackTrace();
			}finally{
				try{
					fos.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public interface PropertiesChecker {
		public boolean check(Properties props);
	}
}
