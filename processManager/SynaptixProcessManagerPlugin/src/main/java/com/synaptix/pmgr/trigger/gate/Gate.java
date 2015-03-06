/*
 * Fichier : Gate.java
 * Projet  : GPSTrainsSMSCnx
 * Date    : 12 nov. 2004
 * Auteur  : sps
 * -----------------------------------------------------------------------------
 * CVS :
 * $Header: /home/cvs_gpstrains/GPSTrainsServeurs/src/com/gpstrains/serveurs/Gate.java,v 1.4 2005/01/12 16:24:05 sps Exp $
 */
package com.synaptix.pmgr.trigger.gate;

import org.apache.commons.logging.Log;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public interface Gate {

	public String getName();	
	public void createNewFile(String msgID, String data);

	public void reinject(String msgID);
	public void accept(String msgID);
	public void reject(String msgID);
	public void retry(String msgID);
	public void trash(String msgID);
	public void archive(String msgID);
	public void setLogger(Log logger);
	public void logFine(String str);
	public void logWarning(String str);
	public void logSevere(String str);
	public void log(String str,int level);

	public void close();
	public void open();
	public boolean isOpened();
}
