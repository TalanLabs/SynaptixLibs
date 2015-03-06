/*
 * Fichier : DataBag.java
 * Projet  : stxToolkit
 * Date    : 21 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.thread;


/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public interface Spoolable {
	public void push(Object data);
	public Object pop(); 
}
