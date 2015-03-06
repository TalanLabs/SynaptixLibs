package com.synaptix.constants;

import java.util.List;

import com.synaptix.constants.shared.ConstantsBundle;

/**
 * 
 * @author Gaby
 * 
 */
public abstract class ConstantsBundleManager {

	/**
	 * Add a constants bundle file properties.
	 * 
	 * Example : <code>addBundle("EXAMPLE","/com/compagny/Example")</code>. Exist a file Example.properties or Example_fr.properties etc
	 * 
	 * @param bundleName
	 *            a bundle name
	 * @param baseName
	 */
	public abstract void addBundle(String bundleName, String baseName);

	/**
	 * Add a constants bundle interface. Search a properties file in same package and same name. Not inherits class
	 * 
	 * @param bundleClass
	 */
	public abstract <E extends ConstantsBundle> void addBundle(Class<E> bundleClass);

	/**
	 * Get a instance of interface bundle
	 * 
	 * @param bundleClass
	 * @return
	 */
	public abstract <E extends ConstantsBundle> E getBundle(Class<E> bundleClass);

	/**
	 * Get a instance of interface bundle name
	 * 
	 * @param bundleName
	 * @return
	 */
	public abstract ConstantsBundle getBundle(String bundleName);

	/**
	 * Get a all bundle names
	 * 
	 * @return
	 */
	public abstract List<String> getAllBundleNames();

}
