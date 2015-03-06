/*
 * Fichier : AbstractOutputScript.java
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
public abstract class AbstractOutputScript implements OutputScript {
	long delay_before;
	long delay_after;
	
	/**
	 * 
	 */
	public AbstractOutputScript() {
		this.delay_before = 0;
		this.delay_after = 0;
	}
	public AbstractOutputScript(long delay_after) {
		this.delay_before = 0;
		this.delay_after = delay_after;
	}
	public AbstractOutputScript(long delay_before, long delay_after) {
		this.delay_before = delay_before;
		this.delay_after = delay_after;
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.io.script.OutputScript#exec(java.io.OutputStream)
	 */
	public void exec(OutputStream os) throws IOException,InterruptedException{
//		System.out.println("Waiting before "+delay_before);
		Thread.sleep(delay_before);
		doOut(os);
//		System.out.println("Waiting after "+delay_after);
		Thread.sleep(delay_after);
	}
	
	public abstract void doOut(OutputStream os) throws IOException;

	/**
	 * @param l
	 */
	public void setDelay_after(long l) {
		delay_after = l;
	}

	/**
	 * @param l
	 */
	public void setDelay_before(long l) {
		delay_before = l;
	}

}
