package com.synaptix.pmgr.core.lib;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import com.synaptix.pmgr.core.apis.Channel;
import com.synaptix.pmgr.core.apis.ChannelSlot;
import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.apis.HandleReport;
import com.synaptix.pmgr.core.apis.PluggableChannel;
import com.synaptix.pmgr.core.lib.handlereport.BaseLocalHandleReport;

public class ProcessingChannel extends AbstractChannel implements PluggableChannel {

	private final static int NO_LOG = -1;

	private final static int UNDERLOAD_LOG = 0;
	private final static int OVERLOAD_LOG = 1;

	private final static int IDLING_LOG = 0;
	private final static int BUSY_LOG = 1;

	private int maxWorkingAgents;
	private int maxWaitingAgents;
	// private List agentThreads;
	private Agent agent;

	private boolean overLoaded = false;
	private boolean busy = false;
	private int lastLoggedLoad = NO_LOG;
	private int lastLoggedBusy = NO_LOG;

	private final Status status;
	private ProcessPool processPool;

	/**
	 * @param name
	 */
	public ProcessingChannel(String name, int maxWorking, int maxWaiting, Agent agent, Engine engine) {
		super(name, engine);

		this.maxWaitingAgents = maxWaiting;
		this.maxWorkingAgents = maxWorking;
		this.agent = agent;

		status = new Status(this);
	}

	private boolean available = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#activate()
	 */
	@Override
	public void activate() {
		ProcessMonitor pm = new ProcessMonitor(this);
		processPool = new ProcessPool(status, pm);
		pm.start();
		available = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#acceptMessage(java.lang.Object)
	 */
	@Override
	public HandleReport acceptMessage(Object message, ChannelSlot slot) {

		AgentRunnable agt = new AgentRunnable(agent, message, processPool);
		// AgentThread agt = new AgentThread(agent,message,processPool);
		processPool.newProcess(agt);
		return new BaseLocalHandleReport("0000", slot, agt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return available;
	}

	@Override
	public void setAvailable(boolean available) {
		this.available = available && processPool != null;
	}

	@Override
	public int getNbWorking() {
		return status != null ? status.runningCount() : -1;
	}

	@Override
	public int getNbWaiting() {
		return status != null ? status.pendingCount() : -1;
	}

	public Agent getAgent() {
		return agent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.Channel#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.pmgr2.apis.PluggableChannel#isOverloaded()
	 */
	@Override
	public boolean isOverloaded() {
		return overLoaded;
	}

	@Override
	public boolean isBusy() {
		return busy;
	}

	/**
	 * Change the number of max working agents<br/>
	 * Use with care
	 */
	public void setMaxWorkingAgents(int maxWorkingAgents) {
		this.maxWorkingAgents = maxWorkingAgents;
	}

	/**
	 * Change the number of max waiting agents<br/>
	 * Use with care
	 */
	public void setMaxWaitingAgents(int maxWaitingAgents) {
		this.maxWaitingAgents = maxWaitingAgents;
	}

	/**
	 * Wake up a monitor which has been stopped
	 */
	public void wakeUp() {
		if (processPool.monitor.isAlive()) {
			Log log = getProcessEngine().getLogger();
			if (log != null) {
				log.warn(getName() + " MONITOR is already awake");
			}
		} else {
			processPool.monitor.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Processing Channel " + maxWorkingAgents + "/" + maxWaitingAgents + ", " + agent.getClass().getName() + ", " + status.runningCount() + " running, " + status.pendingCount() + " pending";
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Methodes privees
	// ////////////////////////////////////////////////////////////////////////////

	private void notifyOverload() {
		overLoaded = true;
		getProcessEngine().getRegistry().getSynchronizer().sendServiceOverload(getName());

		switch (lastLoggedLoad) {
		case NO_LOG:
		case UNDERLOAD_LOG:
			Log log = getProcessEngine().getLogger();
			if (log != null) {
				log.warn("OVERLOAD for service " + getName());
			}
			break;

		default:
			break;
		}
		lastLoggedLoad = OVERLOAD_LOG;
	}

	private void notifyUnderload() {
		overLoaded = false;

		getProcessEngine().getRegistry().getSynchronizer().sendServiceUnderload(getName());
		switch (lastLoggedLoad) {
		case NO_LOG:
		case OVERLOAD_LOG:
			Log log = getProcessEngine().getLogger();
			if (log != null) {
				log.debug("UNDERLOAD for service " + getName());
			}
			break;

		default:
			break;
		}
		lastLoggedLoad = UNDERLOAD_LOG;
	}

	private void notifyIdling() {
		busy = false;

		getProcessEngine().getRegistry().getSynchronizer().sendServiceIdling(getName());
		switch (lastLoggedBusy) {
		case NO_LOG:
		case BUSY_LOG:
			Log log = getProcessEngine().getLogger();
			if (log != null) {
				log.debug("Idling for service " + getName());
			}
			break;

		default:
			break;
		}
		lastLoggedBusy = IDLING_LOG;
	}

	private void notifyBusy() {
		busy = true;

		getProcessEngine().getRegistry().getSynchronizer().sendServiceBusy(getName());
		switch (lastLoggedBusy) {
		case NO_LOG:
		case IDLING_LOG:
			Log log = getProcessEngine().getLogger();
			if (log != null) {
				log.debug("Busy for service " + getName());
			}
			break;

		default:
			break;
		}
		lastLoggedBusy = BUSY_LOG;
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Classes
	// ////////////////////////////////////////////////////////////////////////////

	/**
	 * Projet GPSTrains Interface des Agents
	 * 
	 * @author sps
	 */
	public static interface Agent {
		public void work(Object message, Engine processEngine);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// Classes privees
	// ////////////////////////////////////////////////////////////////////////////

	private class AgentRunnable implements Runnable {
		private Agent agent;
		private Object message;
		private ProcessPool processPool;

		private AgentRunnable(Agent agent, Object message, ProcessPool processPool) {
			this.agent = agent;
			this.message = message;
			this.processPool = processPool;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			try {
				agent.work(message, getProcessEngine());
			} catch (Throwable ex) {
				Log l = getProcessEngine().getLogger();
				if (l != null) {
					l.error("Uncatched exception in agent work : " + agent.getClass().getName());
					ex.printStackTrace();
					// l.throwing(agent.getClass().getName(),"work",ex);
				} else {
					System.err.println("Uncatched exception in agent work : " + agent.getClass().getName());
					ex.printStackTrace(System.err);
				}
			} finally {
				processPool.doneProcess(Thread.currentThread());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#finalize()
		 */
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			message = null;
		}

	}

	private static class AgentThread extends Thread {

		private ProcessingChannel pchannel;

		private AgentThread(AgentRunnable runnable, ProcessingChannel pchannel) {
			super(runnable, "AGTWORK_" + pchannel.getName());

			this.pchannel = pchannel;
		}

		@Override
		public void run() {
			pchannel.notifyBusy();

			super.run();
		}

	}

	// private class AgentThread extends Thread {
	// private Agent agent;
	// private Object message;
	// private ProcessPool processPool;
	//
	// private AgentThread(
	// Agent agent,
	// Object message,
	// ProcessPool processPool) {
	// super("AGTWORK_" + ProcessingChannel.this.getName());
	// setDaemon(true);
	// this.agent = agent;
	// this.message = message;
	// this.processPool = processPool;
	// }
	// /* (non-Javadoc)
	// * @see java.lang.Thread#run()
	// */
	// public void run() {
	// agent.work(message,getProcessEngine());
	// processPool.doneProcess(this);
	// }
	//
	// /* (non-Javadoc)
	// * @see java.lang.Object#finalize()
	// */
	// protected void finalize() throws Throwable {
	// super.finalize();
	// message = null;
	// }
	//
	// }

	private class Status {

		private final List<AgentRunnable> pendingList;
		private final List<Thread> runningList;
		private final ProcessingChannel pchannel;

		private Status(ProcessingChannel pchannel) {
			this.pendingList = new ArrayList<AgentRunnable>();
			this.runningList = new ArrayList<Thread>();
			this.pchannel = pchannel;
		}

		public boolean pushToWork(int i) {
			AgentRunnable runnable = null;
			synchronized (pendingList) {
				if (pendingList.size() <= i) {
					return false;
				}
				runnable = pendingList.remove(i);
			}
			Thread thread = new AgentThread(runnable, pchannel);
			synchronized (runningList) {
				runningList.add(thread);
			}
			thread.start();

			// System.out.println("RUNNNING ON "+pchannel.getName()+" = "+runningCount());

			return true;
		}

		public void pushToWait(AgentRunnable runnable) {
			synchronized (pendingList) {
				pendingList.add(runnable);
				if (pendingList.size() >= maxWaitingAgents) {
					pchannel.notifyOverload();
				}
			}
		}

		public void workDone(Thread thread) {
			synchronized (runningList) {
				runningList.remove(thread);
				if (runningList.size() == 0) {
					pchannel.notifyIdling();
				}
			}
		}

		public int pendingCount() {
			return pendingList.size();
		}

		public int runningCount() {
			return runningList.size();
		}

		public void checkLoad() {
			synchronized (pendingList) {
				if (pendingList.size() < maxWaitingAgents) {
					pchannel.notifyUnderload();
				}
			}
		}
	}

	/**
	 * Projet GPSTrains Thread de monitoring des threads d'execution des agents.
	 * 
	 * @author sps
	 */
	private class ProcessMonitor extends Thread {

		private ProcessMonitor(Channel channel) {
			super("PMONITOR_" + channel.getName());
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					synchronized (this) {
						wait(5000);
					}
					while (status.runningCount() < maxWorkingAgents) {
						try {
							if (!status.pushToWork(0)) {
								break;
							}
						} catch (Throwable e) {
							Log log = getProcessEngine().getLogger();
							if (log != null) {
								log.error("PUSH TO WORK " + this.getName(), e);
							}
							wait(5000);
						}
					}
					status.checkLoad();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				Log log = getProcessEngine().getLogger();
				if (log != null) {
					log.error("InterruptedException " + this.getName(), e);
				}
			}
		}
	}

	private class ProcessPool {

		private final Status status;
		private final ProcessMonitor monitor;

		private ProcessPool(Status status, ProcessMonitor monitor) {
			this.status = status;
			this.monitor = monitor;
		}

		public void newProcess(AgentRunnable runnable) {
			status.pushToWait(runnable);
			notifyMonitor();
		}

		public void notifyMonitor() {
			synchronized (monitor) {
				monitor.notify();
			}
		}

		public void doneProcess(Thread thread) {
			status.workDone(thread);
			notifyMonitor();
		}
	}
}
