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
public class OutOutputScript extends AbstractOutputScript {
	byte[] out;
	/**
	 * 
	 */
	public OutOutputScript(byte[]out) {
		super();
		this.out = out;
	}

	/**
	 * @param delay_after
	 */
	public OutOutputScript(long delay_after,byte[]out) {
		super(delay_after);
		this.out = out;
	}

	/**
	 * @param delay_before
	 * @param delay_after
	 */
	public OutOutputScript(long delay_before, long delay_after,byte[]out) {
		super(delay_before, delay_after);
		this.out = out;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.io.script.AbstractOutputScript#doOut(java.io.OutputStream)
	 */
	public void doOut(OutputStream os) throws IOException {
		os.write(out);
	}

}
