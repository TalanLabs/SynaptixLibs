/*
 * Fichier : BlockOutputScript.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io.script;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class BlockOutputScript extends AbstractOutputScript {
	List scripts;
	/**
	 * 
	 */
	public BlockOutputScript() {
		super();
		scripts =new ArrayList();
	}

	/**
	 * @param delay_after
	 */
	public BlockOutputScript(long delay_after) {
		super(delay_after);
		scripts =new ArrayList();
	}

	/**
	 * @param delay_before
	 * @param delay_after
	 */
	public BlockOutputScript(long delay_before, long delay_after) {
		super(delay_before, delay_after);
		scripts =new ArrayList();
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.io.script.AbstractOutputScript#doOut(java.io.OutputStream)
	 */
	public void doOut(OutputStream os) throws IOException {
		for (int i=0; i<scripts.size(); i++){
			OutputScript oscr = (OutputScript) scripts.get(i);
			try{
				oscr.exec(os);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

	}

	public void appendScript(OutputScript script){
		scripts.add(script);
	}
}
