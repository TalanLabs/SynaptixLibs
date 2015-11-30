/*
 * Fichier : TestScript.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io;

import java.io.IOException;

import com.synaptix.toolkits.io.script.OutputScript;
import com.synaptix.toolkits.io.script.OutputScriptFactory;
import com.synaptix.toolkits.io.script.factories.XMLOuputScriptFactory;
import com.synaptix.toolkits.nanoxml.NanoXMLKit;

import nanoxml.XMLElement;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class TestScript {

	/**
	 * 
	 */
	public TestScript() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args){
		XMLElement elem = NanoXMLKit.load("com/synaptix/toolkits/io/script2.xml",TestScript.class,true);
		if (elem!=null){
			OutputScriptFactory osf = new XMLOuputScriptFactory(elem);
			OutputScript os = osf.build();
			try {
				os.exec(System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
