package com.synaptix.tmgr.libs.tasks.filesys;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.synaptix.tmgr.apis.Trigger;
import com.synaptix.tmgr.apis.TriggerEventListener;
import com.synaptix.tmgr.libs.BaseTriggerEvent;
import com.synaptix.tmgr.libs.tasks.AbstractTriggerTask;

public class FolderEventTriggerTask extends AbstractTriggerTask {

	public static final String STOP_FILE = "trigger.stop"; //$NON-NLS-1$

	final Map<String, Long> lmdates;
	private final File folder;
	private final String lockExtension;
	private final File stopFile;

	public FolderEventTriggerTask(File folder, String lockExtension) {
		this.folder = folder;
		this.lmdates = new HashMap<String, Long>();
		this.lockExtension = lockExtension;
		this.stopFile = new File(folder, STOP_FILE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.tmgr.libs.ThreadedTriggerTask#execute(com.synaptix.tmgr.apis.TriggerEventListener)
	 */
	@Override
	public void execute(TriggerEventListener lstnr) {
		if (stopFile.exists()) {
			return;
		}
		long taskts = System.currentTimeMillis();
		File[] files = folder.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (stopFile.exists()) {
					return;
				}
				File f = files[i];
				if (acceptScan(f)) {
					propagateEvent(taskts, f, lstnr);
				}
			}
		}
		List<String> toRemove = new ArrayList<String>();
		Iterator<String> it = lmdates.keySet().iterator();
		while (it.hasNext()) {
			String fname = it.next();
			File file = new File(folder, fname);
			if (!file.exists()) {
				toRemove.add(fname);
			}
		}
		for (int i = 0; i < toRemove.size(); i++) {
			String fname = toRemove.get(i);
			lmdates.remove(fname);
			lstnr.notifyEvent(new RemovedFileTriggerEvent(new File(folder, fname), getTrigger()));
		}
	}

	boolean acceptScan(File f) {
		if (f.isDirectory()) {
			return false;
		}
		if (f.getName().equals(STOP_FILE)) {
			return false;
		}
		if (!f.getName().endsWith(lockExtension)) {
			// c'est un fichier standard, y a t il un lock?
			return (!(new File(folder, f.getName() + lockExtension)).exists());
		} else {
			// c'est un fichier de lock... ignorer
			return false;
		}
	}

	void propagateEvent(long taskts, File f, TriggerEventListener lstnr) {
		long lmdiff = getLastModifDateDiff(f);
		if (lmdiff < 0) {
			// Ce fichier est nouveau...
			// Propager un message de type "nouveau fichier"
			lstnr.notifyEvent(new NewFileTriggerEvent(f, getTrigger()));
		} else if (lmdiff > 0) {
			// Ce fichier a été modifié...
			// Propager un message de type "fichier modifié"
			lstnr.notifyEvent(new ModifiedFileTriggerEvent(f, getTrigger()));
		}
	}

	long getLastModifDateDiff(File f) {
		long lmd = f.lastModified();
		Object obj = lmdates.get(f.getName());
		if (obj == null) {
			lmdates.put(f.getName(), lmd);
			return -1;
		} else {
			long delta = lmd - (Long) obj;

			if (delta > 0) {
				lmdates.put(f.getName(), lmd);
			}
			return delta;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.tmgr.libs.ThreadedTriggerTask#clean()
	 */
	@Override
	public void clean() {
		lmdates.clear();
	}

	public abstract static class FileTriggerEvent extends BaseTriggerEvent {
		public FileTriggerEvent(File f, Trigger source) {
			super(source, f);
		}

	}

	public static class NewFileTriggerEvent extends FileTriggerEvent {
		public NewFileTriggerEvent(File f, Trigger source) {
			super(f, source);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "NEW FILE : " + getAttachment();
		}
	}

	public static class ModifiedFileTriggerEvent extends FileTriggerEvent {
		public ModifiedFileTriggerEvent(File f, Trigger source) {
			super(f, source);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MODIFIED FILE : " + getAttachment();
		}
	}

	public static class RemovedFileTriggerEvent extends FileTriggerEvent {
		public RemovedFileTriggerEvent(File f, Trigger source) {
			super(f, source);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "REMOVED FILE : " + getAttachment();
		}
	}

}
