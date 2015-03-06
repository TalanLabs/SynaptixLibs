package com.synaptix.tmgr.libs;

import com.synaptix.tmgr.apis.TriggerEventListener;

public class ThreadedTrigger extends AbstractTrigger {
	private HostingThread hostingThread;
	private ThreadedTriggerTask task;
	private long wait;
	/**
	 * @param id
	 */
	public ThreadedTrigger(String id, ThreadedTriggerTask task, long wait) {
		super(id);
		this.task = task;
		this.wait = wait;
		this.task.setTrigger(this);
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.libs.AbstractTrigger#doDeactivate()
	 */
	public void doDeactivate() {
		//if (hostingThread!=null){
		//	hostingThread.interrupt();
		//}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.tmgr.libs.AbstractTrigger#doActivate(com.synaptix.tmgr.apis.TriggerEventListener)
	 */
	public void doActivate(TriggerEventListener telistener) {
		task.clean();
		hostingThread = new HostingThread(this,task,telistener,wait);
		hostingThread.start();
	}

	public static class HostingThread extends Thread{
		ThreadedTrigger trigger;
		ThreadedTriggerTask task;
		TriggerEventListener lstnr;
		long wait;
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		 public HostingThread(ThreadedTrigger trigger,ThreadedTriggerTask task, TriggerEventListener lstnr, long wait){
		 	super("TriggerThread_"+trigger.getID());
		 	setDaemon(true);
		 	this.trigger = trigger;
		 	this.task = task;
		 	this.lstnr = lstnr;
		 	this.wait = wait;
		 }
		public void run() {
			try{
				while (trigger.isActive()){
					task.execute(lstnr);
					Thread.sleep(wait);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			task.clean();
		}

	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "THREADED TRIGGER (task = "+task.toString()+")";
	}

}
