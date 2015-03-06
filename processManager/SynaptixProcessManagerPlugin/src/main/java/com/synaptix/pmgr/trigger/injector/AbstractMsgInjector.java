package com.synaptix.pmgr.trigger.injector;

import java.io.File;

import com.synaptix.pmgr.trigger.gate.Gate;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask.FileTriggerEvent;

public abstract class AbstractMsgInjector implements MessageInjector {
	private Gate gate;
	private File workDir;

	public AbstractMsgInjector() {

	}

	public AbstractMsgInjector(String rootPath) {
		workDir = new File(rootPath);
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public void setGate(Gate g) {
		this.gate = g;
	}

	public Gate getGate() {
		return gate;
	}

	public abstract Object inject(FileTriggerEvent evt);

	public final void manageResponse(String resp, File file2move) {
		// if(this.gate==null)
		// System.out.println(resp);
		if (resp != null) {
			if (resp.equalsIgnoreCase("ok")) {
				this.gate.accept(file2move.getName());
			} else if (resp.equalsIgnoreCase("nok")) {
				this.gate.reject(file2move.getName());
			} else if (resp.equalsIgnoreCase("noanswer")) {
				this.gate.retry(file2move.getName());
			}
		} else {
			this.gate.retry(file2move.getName());
		}
	}
}
