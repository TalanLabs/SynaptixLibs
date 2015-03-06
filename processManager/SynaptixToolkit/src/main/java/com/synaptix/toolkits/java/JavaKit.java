/*
 * Fichier : JavaKit.java
 * Projet  : stxToolkit
 * Date    : 6 sept. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.java;

import java.lang.reflect.Field;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class JavaKit {

	public static int getInt(String classname, String fieldname, ClassLoader cl) throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		Class cls = cl.loadClass(classname);
		Field f = cls.getField(fieldname);
		return f.getInt(null);
	}
}
