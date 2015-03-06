package test;

import java.io.File;

import com.synaptix.pmgr.core.lib.ProcessEngine;
import com.synaptix.pmgr.trigger.injector.AbstractMsgInjector;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask;
import com.synaptix.tmgr.libs.tasks.filesys.FolderEventTriggerTask.FileTriggerEvent;

public class SimpleMsgInjector extends AbstractMsgInjector{
	
	public SimpleMsgInjector(){
		super();
	}

	public SimpleMsgInjector(String rootPath){
		super(rootPath);
	}

	public Object inject(FileTriggerEvent evt) {
		File newFile =  (File)evt.getAttachment();
		if ((evt instanceof FolderEventTriggerTask.NewFileTriggerEvent)&&(newFile.getParentFile().equals(getWorkDir()))){
			try {
				ProcessEngine.handle("CHNL_HELLO_NTS2", "TOTO");
				getGate().logFine("LE FICHIER "+((File)evt.getAttachment()).getName()+" ENVOYE");
			} catch (Exception e) {
				e.printStackTrace();
			}
//			manageResponse(resp,(File)evt.getAttachment());
		}
		return null;
	}
}
