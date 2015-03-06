package com.synaptix.deployer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.deployer.worker.IShellWorker;

/* package protected */abstract class ShellWorker implements IShellWorker {

	private final static Log LOG = LogFactory.getLog(ShellWorker.class);

	private volatile Boolean shouldRun = true;

	private volatile Boolean isRunning = false;

	private final ShellRunnable runnable;

	private final SSHManager sshManager;

	public ShellWorker(final SSHManager sshManager) {

		this.sshManager = sshManager;

		runnable = new ShellRunnable() {

			private String workResult;

			@Override
			public void run() {
				sshManager.connect();
				try {
					workResult = work();
				} catch (Exception e) {
					LOG.error("", e);
				} finally {
					stop();
				}
			}

			@Override
			public String getWorkResult() {
				return workResult;
			}

			@Override
			public void stop() {
				shouldRun = false;
				isRunning = false;
				sshManager.close();
			}
		};
	}

	/**
	 * Start the shell worker
	 */
	public final void start() {
		isRunning = true;
		Thread worker = new Thread(runnable);
		worker.start();
	}

	protected abstract String work();

	@Override
	public final void stop() {
		runnable.stop();
	}

	@Override
	public final String synchronize() {
		while (shouldRun) {
			// do nothing
		}
		while (isRunning) {
			// do nothing
		}
		return runnable.getWorkResult();
	}

	private interface ShellRunnable extends Runnable {

		public String getWorkResult();

		public void stop();

	}

	public String sendCommand(String command) {
		return sshManager.sendCommand(command);
	}
}
