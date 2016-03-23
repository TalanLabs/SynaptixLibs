/*
 * Fichier : AbstractFileSysGate.java
 * Projet  : GPSTrainsServeurs
 * Date    : 2 dec. 2004
 * Auteur  : sps
 * -----------------------------------------------------------------------------
 * CVS :
 * $Header: /home/cvs_gpstrains/GPSTrainsServeurs/src/com/gpstrains/serveurs/AbstractFileSysGate.java,v 1.6 2005/02/16 11:05:15 bpt Exp $
 */
package com.synaptix.pmgr.trigger.gate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.trigger.injector.MessageInjector;
import com.synaptix.tmgr.TriggerEngine;
import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEvent;
import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.tmgr.libs.ThreadedTrigger;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask;

/**
 * Projet GPSTrains
 *
 * @author sps
 */
public abstract class AbstractFileSysGate implements Gate {

	public long retryPeriod = -1;
	private String name;
	private File entranceFolder;
	private File acceptedFolder;
	private File rejectedFolder;
	private File retryFolder;
	private File archiveFolder;
	private MessageInjector messageInjector;
	private Log logger;

	private boolean opened;

	/**
	 *
	 */
	public AbstractFileSysGate(String name, File entranceFolder, File acceptedFolder, File rejectedFolder, File retryFolder, File archiveFolder, long retryPeriod, MessageInjector injector) {
		this.name = name;
		this.entranceFolder = entranceFolder;
		this.acceptedFolder = acceptedFolder;
		this.rejectedFolder = rejectedFolder;
		this.archiveFolder = archiveFolder;
		this.retryFolder = retryFolder;
		this.retryPeriod = retryPeriod;
		this.messageInjector = injector;
		this.messageInjector.setGate(this);
	}

	@Override
	public void setLogger(Log log) {
		logger = log;
	}

	@Override
	public void logFine(String str) {
		log(str, 0);
	}

	@Override
	public void logWarning(String str) {
		log(str, 1);
	}

	@Override
	public void logSevere(String str) {
		log(str, 2);
	}

	@Override
	public void log(String str, int level) {
		switch (level) {
			case 0:
				if (logger != null) {
					logger.info(str);
				} else {
					System.out.println((new Date()) + "INFO : " + str);
				}
				break;
			case 1:
				if (logger != null) {
					logger.warn(str);
				} else {
					System.out.println((new Date()) + "WARNING : " + str);
				}
				break;
			default:
				if (logger != null) {
					logger.error(str);
				} else {
					System.out.println((new Date()) + "SEVERE : " + str);
				}
				break;
		}
	}

	protected File getEntranceFolder() {
		return entranceFolder;
	}

	File getAcceptedFolder() {
		return acceptedFolder;
	}

	File getRejectedFolder() {
		return rejectedFolder;
	}

	File getArchivedFolder() {
		return archiveFolder;
	}

	File getRetryFolder() {
		return retryFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.serveurs.Gate#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	protected void init() {
		// logFine("init de la GATE : " +entranceFolder.getAbsolutePath());
		if (!entranceFolder.exists()) {
			if (!entranceFolder.mkdirs()) {
				logWarning("Entrance Folder not created for " + name);
			}
		}
		if (!acceptedFolder.exists()) {
			if (!acceptedFolder.mkdirs()) {
				logWarning("Accepted Folder not created for " + name);
			}
		}
		if (!rejectedFolder.exists()) {
			if (!rejectedFolder.mkdirs()) {
				logWarning("Rejected Folder not created for " + name);
			}
		}
		if (!archiveFolder.exists()) {
			if (!archiveFolder.mkdirs()) {
				logWarning("Archive Folder not created for " + name);
			}
		}
		if (!retryFolder.exists()) {
			if (!retryFolder.mkdirs()) {
				logWarning("Retry Folder not created for " + name);
			}
		}

		open();

		if (retryPeriod > 0) {
			// installer la thread sur retry.
			new RetryThread(retryPeriod, retryFolder, this).start();
		}
		// logFine("fin init de la GATE : " +entranceFolder.getAbsolutePath());
	}

	protected File resolveEntranceFile(String msgID) {
		return new File(entranceFolder, msgID);
	}

	protected File resolveAcceptedFile(String msgID) {
		return new File(acceptedFolder, msgID);
	}

	protected File resolveRejectedFile(String msgID) {
		return new File(rejectedFolder, msgID);
	}

	protected File resolveRetryFile(String msgID) {
		return new File(retryFolder, msgID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.serveurs.Gate#createNewFile(java.lang.String, java.lang.String)
	 */
	@Override
	public void createNewFile(String msgID, String data) {
		File nf = new File(entranceFolder, msgID);
		logFine("Creating new File : " + nf.getAbsolutePath());
		File lck = new File(entranceFolder, msgID + ".lck");
		try {
			lck.createNewFile();
			OutputStream os = new FileOutputStream(nf);
			try {
				os.write(data.getBytes());
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		lck.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.serveurs.Gate#reinject(java.lang.String)
	 */
	@Override
	public void reinject(String msgID) {
		File f = resolveRetryFile(msgID);
		if (f.exists()) {
			f.renameTo(resolveEntranceFile(msgID));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.serveurs.Gate#accept(java.lang.String)
	 */
	@Override
	public void accept(String msgID) {
		File f = resolveEntranceFile(msgID);
		if (f.exists()) {
			boolean remove = f.renameTo(new File(acceptedFolder, msgID));
			if (!remove) {
				logSevere("IMPOSSIBLE DE DEPLACER LE MESSAGE " + msgID + " DANS " + f.getAbsolutePath());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#reject(java.lang.String)
	 */
	@Override
	public void reject(String msgID) {
		File f = resolveEntranceFile(msgID);
		if (f.exists()) {
			boolean remove = f.renameTo(new File(rejectedFolder, msgID));
			if (remove) {
				logFine("REJET DU MESSAGE " + msgID + " (" + f.getAbsolutePath() + ")");
			} else {
				logSevere("IMPOSSIBLE DE DEPLACER LE MESSAGE " + msgID + " DANS " + f.getAbsolutePath());
			}
		} else {
			logWarning("LE MESSAGE " + msgID + " N'EXISTE PAS DANS " + f.getAbsolutePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.serveurs.Gate#retry(java.lang.String)
	 */
	@Override
	public void retry(String msgID) {
		File f = resolveEntranceFile(msgID);
		logFine("REESSAI DU MESSAGE " + msgID + " (" + f.getAbsolutePath() + ")");
		if (f.exists()) {
			boolean remove = f.renameTo(new File(retryFolder, msgID));
			if (remove) {
				logFine("REESSAI DU MESSAGE " + msgID + " (" + f.getAbsolutePath() + ")");
			} else {
				logSevere("IMPOSSIBLE DE DEPLACER LE MESSAGE " + msgID + " DANS " + f.getAbsolutePath());
			}
		} else {
			logWarning("LE MESSAGE " + msgID + " N'EXISTE PAS DANS " + f.getAbsolutePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#trash(java.lang.String)
	 */
	@Override
	public void trash(String msgID) {
		File f = resolveAcceptedFile(msgID);
		if (f.exists()) {
			f.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#archive(java.lang.String)
	 */
	@Override
	public void archive(String msgID) {
		File f = resolveAcceptedFile(msgID);
		if (f.exists()) {
			f.renameTo(new File(archiveFolder, msgID));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#close()
	 */
	@Override
	public void close() {
		TriggerEngine.getInstance().uninstallTrigger(name);
		opened = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#isOpened()
	 */
	@Override
	public boolean isOpened() {
		return opened;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gpstrains.smscnx.Gate#open()
	 */
	@Override
	public void open() {
		// installe et demarre le trigger...
		TriggerEngine te = TriggerEngine.getInstance();

		// TriggerEventListener tel = new SimpleTriggerEventListener();
		TriggerEventListener tel = new TriggerEventListener() {
			@Override
			public void notifyEvent(TriggerEvent arg0) {
				messageInjector.inject((FolderEventTriggerTask.FileTriggerEvent) arg0);
			}
		};

		// Correction BUG: Pas de possibilite d'ajouter un Trigger
		// supplementaire sur un meme serveur
		logFine("AJOUT DU LISTENER AU TRIGGER ENGINE SUR : " + entranceFolder.getAbsolutePath());
		te.addListener(tel);
		// Correction BUG: Pas de possibilite d'ajouter un Trigger
		// supplementaire sur un meme serveur

		// logFine("Starting file Trigger on : " + entranceFolder.getAbsolutePath());

		Trigger t = new ThreadedTrigger(name, new FolderEventTriggerTask(entranceFolder, ".lck"), 200);
		te.installTrigger(t, true);

		opened = true;
	}

	public static class RetryThread extends Thread {
		long retryPeriod;
		File retryFolder;
		Gate parentGate;

		public RetryThread(long retryPeriod, File retryFolder, Gate parentGate) {
			super();
			setDaemon(true);
			this.retryPeriod = retryPeriod;
			this.retryFolder = retryFolder;
			this.parentGate = parentGate;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				while (true) {
					sleep(retryPeriod);
					File[] f = retryFolder.listFiles();
					for (int i = 0; i < f.length; i++) {
						if (f[i].isFile()) {
							parentGate.reinject(f[i].getName());
						}
					}
				}
			} catch (InterruptedException ex) {
				parentGate.logSevere("ERROR " + parentGate.getName() + " : " + ex.getMessage());
				ex.printStackTrace();
			} catch (Exception ex) {
				parentGate.logSevere("ERROR " + parentGate.getName() + " : " + ex.getMessage());
				ex.printStackTrace();
			}
		}

	}

}
