/*
 * Fichier : OutOutputScript.java
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
public class CloseOutputScript extends AbstractOutputScript {

	/**
	 * @param delay_after
	 */
	public CloseOutputScript() {
		super();
	}
	/**
	 * @param delay_after
	 */
	public CloseOutputScript(long delay_after) {
		super(delay_after);
	}

	/**
	 * @param delay_before
	 * @param delay_after
	 */
	public CloseOutputScript(long delay_before, long delay_after) {
		super(delay_before, delay_after);
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.io.script.AbstractOutputScript#doOut(java.io.OutputStream)
	 */
	public void doOut(OutputStream os) throws IOException {
		os.close();
	}

}
