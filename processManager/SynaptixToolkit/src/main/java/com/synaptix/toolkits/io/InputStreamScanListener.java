/*
 * Fichier : InputStreamEventListener.java
 * Projet  : stxToolkit
 * Date    : 26 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.io;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public interface InputStreamScanListener {
	public void data(byte[] data,int offset,int len);
	public void EOD(byte[] data,int offset,int len);
}
