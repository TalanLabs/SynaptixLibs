package com.synaptix.pmgr.trigger.injector;

import java.io.File;

import com.synaptix.pmgr.trigger.gate.Gate;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask;

public interface MessageInjector {

	public Object inject(FolderEventTriggerTask.FileTriggerEvent evt);

	public void manageResponse(String resp, File file);

	public void setGate(Gate g);

	public Gate getGate();

	public File getWorkDir();
}
