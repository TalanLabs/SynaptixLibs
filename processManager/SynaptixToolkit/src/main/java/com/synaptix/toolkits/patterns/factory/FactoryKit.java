/*
 * Created on 31 dec 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.synaptix.toolkits.patterns.factory;

public class FactoryKit {
	public static C0Factory getC0Factory(Class forclass, String fcls) {
		ClassLoader cl = forclass.getClassLoader();
		try {
			Class fc = cl.loadClass(fcls);
			C0Factory f = (C0Factory) fc.newInstance();
			return f;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static C1Factory getC1Factory(Class forclass, String fcls) {
		ClassLoader cl = forclass.getClassLoader();
		try {
			Class fc = cl.loadClass(fcls);
			C1Factory f = (C1Factory) fc.newInstance();
			return f;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object build(C0Factory factory, Class asClass) {
		Object obj = factory.build();
		if (obj != null) {
			if (asClass.isAssignableFrom(obj.getClass()))
				return obj;
		}
		return null;
	}

	public static Object build(C1Factory factory, Object arg, Class asClass) {
		Object obj = factory.build(arg);
		if (obj != null) {
			if (asClass.isAssignableFrom(obj.getClass()))
				return obj;
		}
		return null;
	}
}
