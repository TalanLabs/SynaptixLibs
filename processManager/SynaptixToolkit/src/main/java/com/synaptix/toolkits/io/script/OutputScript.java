/*
 * Fichier : OutputScript.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io.script;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public interface OutputScript {
	public void exec(OutputStream os) throws IOException,InterruptedException;
}
