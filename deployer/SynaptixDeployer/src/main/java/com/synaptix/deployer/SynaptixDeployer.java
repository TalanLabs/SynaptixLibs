package com.synaptix.deployer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.callback.ITrackingCallback;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.instructions.SynaptixInstructions;
import com.synaptix.deployer.model.IEnvironmentState;
import com.synaptix.deployer.model.IFileSystemSpace;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.StepEnum;
import com.synaptix.deployer.worker.IShellWorker;

/**
 * A deployer used to stop applications, rename old ones, download new, start again
 * 
 * @author Nicolas P
 * 
 */
public abstract class SynaptixDeployer {

	private final static Log LOG = LogFactory.getLog(SynaptixDeployer.class);

	private final SSHManager sshManager;

	private final ISynaptixEnvironment environment;

	/**
	 * @param environnement
	 */
	public SynaptixDeployer(ISynaptixEnvironment environment) {
		super();

		this.environment = environment;
		this.sshManager = new SSHManager(environment.getLogin(), environment.getPassword(), environment.getIp());
	}

	/**
	 * Do a deployment given instructions
	 * 
	 * @param instructions
	 * @param consoleUrl
	 * @param scripts
	 * @param db
	 * @param dbPassword
	 * @param login
	 * @param callback
	 */
	public void proceed(final SynaptixInstructions instructions, final String consoleUrl, final char[] dbPassword, final ISynaptixDatabaseSchema db, final Map<String, byte[]> scripts,
			final ITrackingCallback callback) {

		ShellWorker shellWorker = new ShellWorker(sshManager) {

			@Override
			protected String work() {
				boolean blockingError = false;
				Set<IEnvironmentInstance> runningEnvironmentInstanceList = findRunningInstanceSet();
				if ((instructions.getStopInstanceSet() != null) && (!instructions.getStopInstanceSet().isEmpty())) {
					for (IEnvironmentInstance instance : instructions.getStopInstanceSet()) {
						if (runningEnvironmentInstanceList.contains(instance)) {
							callback.markPlay(instance, StepEnum.STOP);
							if (stopInstance(instance)) {
								callback.markDone(instance, StepEnum.STOP);
							} else {
								callback.markRejected(instance, StepEnum.STOP, "Error");
							}
						} else {
							LOG.error("Could't stop instance " + instance.getName() + " since it is already stopped!");
							callback.markRejected(instance, StepEnum.STOP, "Already stopped");
						}
					}
					runningEnvironmentInstanceList = findRunningInstanceSet();
				}
				if ((instructions.getRenameInstanceSet() != null) && (!instructions.getRenameInstanceSet().isEmpty())) {
					for (IEnvironmentInstance instance : instructions.getRenameInstanceSet()) {
						if (!runningEnvironmentInstanceList.contains(instance)) {
							callback.markPlay(instance, StepEnum.RENAME);
							if (archiveWarList(instance)) {
								callback.markDone(instance, StepEnum.RENAME);
							} else {
								callback.markRejected(instance, StepEnum.RENAME, "Error");
							}
						} else {
							LOG.error("Could't rename wars of instance " + instance.getName() + " since it is still running!");
							callback.markRejected(instance, StepEnum.RENAME, "Still running");
						}
					}
				}
				if ((instructions.getDownloadInstanceSet() != null) && (!instructions.getDownloadInstanceSet().isEmpty())) {
					for (IEnvironmentInstance instance : instructions.getDownloadInstanceSet()) {
						if (!runningEnvironmentInstanceList.contains(instance)) {
							callback.markPlay(instance, StepEnum.DOWNLOAD);
							if (copyNewWars(instance, consoleUrl)) {
								callback.markDone(instance, StepEnum.DOWNLOAD);
							} else {
								callback.markRejected(instance, StepEnum.DOWNLOAD, "Error");
							}
						} else {
							LOG.error("Could't copy new .wars of instance " + instance.getName() + " since it is still running!");
							callback.markRejected(instance, StepEnum.DOWNLOAD, "Still running");
						}
					}
				}
				if ((instructions.isPlayScripts()) && (dbPassword != null) && (db != null) && (scripts != null) && (!scripts.isEmpty()) && (environment.getSqlPlusPath() != null)) {
					callback.markPlay(null, StepEnum.DATABASE);
					boolean isOK = false;
					try {
						isOK = sshManager.createFolder("./tmpScripts");
					} catch (Exception e1) {
						sshManager.sendCommand("rm -r ./tmpScripts");
						try {
							isOK = sshManager.createFolder("./tmpScripts");
						} catch (Exception e2) {
							callback.markRejected(null, StepEnum.DATABASE, "Temporary folder creation failed");
						}

					}
					if (isOK) {
						LOG.info("Playing db scripts");
						StringBuilder sb = new StringBuilder(environment.getSqlPlusPath()).append(" ");
						sb.append(db.getTablespaceName()).append("/").append(dbPassword).append("@").append(db.getService()).append(" @./tmpScripts/");
						String lastTest = null;
						try {
							sshManager.sendCommand("cd ~");
							sshManager.sendCommand("export ORACLE_HOME=" + environment.getSqlPlusPath().substring(0, environment.getSqlPlusPath().lastIndexOf("/") - 4)); // remove /bin
							StringBuilder sbDB = new StringBuilder();
							for (Entry<String, byte[]> entry : scripts.entrySet()) {
								if (isOK) {
									String file = "./tmpScripts/" + entry.getKey();
									LOG.info("Playing db script: " + file);
									lastTest = entry.getKey();
									sshManager.createTemporaryFile(entry.getValue(), file);
									sshManager.sendCommand("echo \"\" >> " + file);
									sshManager.sendCommand("echo \"exit;\" >> " + file);
									String result = sshManager.sendCommand(sb.toString() + entry.getKey());
									if (result.contains("\nORA-")) {
										isOK = false;
										callback.markRejected(null, StepEnum.DATABASE, "Error when playing " + lastTest);
									}
									LOG.info(result);
									if (sbDB.length() > 0) {
										sbDB.append("<br>");
									}
									sbDB.append("<b>").append(entry.getKey()).append("</b><br>").append(result.replaceAll("\\n", "<br>"));
								}
							}
							callback.log("<html>" + sbDB.toString().replaceAll("\\n", "<br>") + "</html>");
						} catch (Exception e) {
							callback.markRejected(null, StepEnum.DATABASE, lastTest + " has been rejected");
						}
						try {
							sshManager.sendCommand("rm -r ./tmpScripts");
						} catch (Exception e1) {
							LOG.error("", e1);
						}
					}
					if (isOK) {
						callback.markDone(null, StepEnum.DATABASE);
					} else {
						blockingError = true;
					}
				}
				if ((instructions.getLaunchInstanceSet() != null) && (!instructions.getLaunchInstanceSet().isEmpty())) {
					for (IEnvironmentInstance instance : instructions.getLaunchInstanceSet()) {
						if (!runningEnvironmentInstanceList.contains(instance)) {
							if (blockingError) {
								callback.markRejected(instance, StepEnum.LAUNCH, "Blocking error");
							} else {
								callback.markPlay(instance, StepEnum.LAUNCH);
								if (restartInstance(instance)) {
									callback.markDone(instance, StepEnum.LAUNCH);
								} else {
									callback.markRejected(instance, StepEnum.LAUNCH, "Error");
								}
							}
						} else {
							LOG.error("Could't run " + instance.getName() + " since it is already running!");
							callback.markRejected(instance, StepEnum.LAUNCH, "Already running");
						}
					}
				}
				return null;
			}
		};
		shellWorker.start();
		shellWorker.synchronize();
	}

	public IShellWorker watch(final IEnvironmentInstance instance) {
		ShellWorker shellWorker = new ShellWorker(sshManager) {

			@Override
			protected String work() {
				sshManager.sendCommand("tail -f -n2000 " + getLogFileforInstance(instance.getName()) + " &");
				synchronize();
				return null;
			}
		};
		shellWorker.start();
		return shellWorker;
	}

	public String downloadLog(final IEnvironmentInstance instance) {
		final StringBuilder builder = new StringBuilder();
		ShellWorker shellWorker = new ShellWorker(sshManager) {

			@Override
			protected String work() {
				builder.append(sshManager.sendCommand("cat " + getLogFileforInstance(instance.getName())));
				return null;
			}
		};
		shellWorker.start();
		shellWorker.synchronize();
		return builder.toString();
	}

	/**
	 * Connect to the environment
	 * 
	 * @return
	 */
	protected SSHManager connect() {
		LOG.info(String.format("Connecting to %s", environment.getIp()));

		sshManager.connect();
		return sshManager;
	}

	protected abstract boolean stopInstance(IEnvironmentInstance instance);

	private boolean copyNewWars(IEnvironmentInstance instance, String consoleUrl) {
		String[] wars = instance.getWars();
		boolean everythingOk = wars.length > 0;
		for (String war : wars) {
			everythingOk = everythingOk && downloadWar(instance, consoleUrl, war);
		}
		return everythingOk;
	}

	/**
	 * Download a war for a build, stored in the instance
	 * 
	 * @param instance
	 * @param consoleUrl
	 * @param war
	 */
	protected abstract boolean downloadWar(IEnvironmentInstance instance, String consoleUrl, String war);

	/**
	 * Launch instance
	 * 
	 * @param instance
	 */
	protected abstract boolean restartInstance(IEnvironmentInstance instance);

	/**
	 * Find the list of running instances
	 * 
	 * @return
	 */
	protected abstract Set<IEnvironmentInstance> findRunningInstanceSet();

	protected final IEnvironmentInstance getEnvironmentInstanceForName(String name) {
		for (IEnvironmentInstance environmentInstance : environment.getInstances()) {
			if (name.equals(environmentInstance.getName())) {
				return environmentInstance;
			}
		}
		return null;
	}

	private boolean archiveWarList(IEnvironmentInstance instance) {
		String path = getWarPathForInstance(instance.getName());
		String ww = sshManager.sendCommand("ls --color=none " + path).replace("\r", "").replace("\n", "");
		while (ww.matches(".*  .*")) {
			ww = ww.replaceAll("(  )+", " ");
		}
		String[] ws = ww.split(" ");

		List<String> toRenameList = new ArrayList<String>();
		for (String w : ws) {
			if (w.endsWith(".war")) {
				String w2 = w.substring(0, w.length() - 4);
				if (isConcerned(w2)) {
					toRenameList.add(path + w);
				}
			} else if (w.matches(".*\\.[0-9]{8}$")) {
				sshManager.sendCommand("rm -f " + path + w);
			}
		}
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		for (String w : toRenameList) {
			sshManager.sendCommand("mv " + w + " " + w + "." + date);
		}
		return (toRenameList.size() > 0);
	}

	/**
	 * Get the path of the war folder of given instance
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract String getWarPathForInstance(String instance);

	/**
	 * Get the log file of given instance
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract String getLogFileforInstance(String instance);

	protected final boolean isConcerned(String war) {
		for (IEnvironmentInstance instance : environment.getInstances()) {
			for (String w : instance.getWars()) {
				if (w.equals(war)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the ssh manager
	 * 
	 * @return
	 */
	protected final SSHManager getSSHManager() {
		return sshManager;
	}

	/**
	 * Get the environment for which the deployment has to be done
	 * 
	 * @return
	 */
	protected final ISynaptixEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Add a listener to be notified when new lines are to be displayed
	 * 
	 * @param readerListener
	 */
	public final void addReaderListener(ReaderListener readerListener) {
		sshManager.addReaderListener(readerListener);
	}

	/**
	 * Check the state of given environment
	 * 
	 * @return true if running, false otherwise
	 */
	public IEnvironmentState checkState() {

		final IEnvironmentState environmentStateBean = ComponentFactory.getInstance().createInstance(IEnvironmentState.class);
		environmentStateBean.setEnvironment(environment);

		ShellWorker shellWorker = new ShellWorker(sshManager) {

			@Override
			protected String work() {
				Set<IEnvironmentInstance> runningInstanceSet = findRunningInstanceSet();
				environmentStateBean.setRunningInstanceSet(runningInstanceSet);
				return null;
			}
		};
		shellWorker.start();
		shellWorker.synchronize();
		return environmentStateBean;
	}

	public List<IFileSystemSpace> getFileSystemSpace() {
		final List<IFileSystemSpace> fileSystemSpaceList = new ArrayList<IFileSystemSpace>();
		ShellWorker shellWorker = new ShellWorker(sshManager) {

			@Override
			protected String work() {
				String result = sendCommand("df -h");
				result = result.replaceAll("^.*(\n|\r)", "");
				String[] lines = result.split("(\n|\r)");
				IFileSystemSpace fileSystemSpace = null;
				for (String line : lines) {
					if (line.charAt(0) != ' ') {
						if (fileSystemSpace != null) {
							fileSystemSpaceList.add(fileSystemSpace);
						}
						fileSystemSpace = ComponentFactory.getInstance().createInstance(IFileSystemSpace.class);
						int min = line.indexOf(' ');
						if (min == -1) {
							min = line.length();
						}
						fileSystemSpace.setName(line.substring(0, min));
						if (min < line.length()) {
							line = line.substring(min + 1, line.length());
						} else {
							line = null;
						}
					}
					if (line != null) {
						line = line.trim().replaceAll(" +", " ");
						String[] split = line.split(" ");
						fileSystemSpace.setTotalSpace(split[0]);
						fileSystemSpace.setUsedSpace(split[1]);
						fileSystemSpace.setAvailableSpace(split[2]);
						fileSystemSpace.setPercentage(Integer.parseInt(split[3].replaceAll("%", "")));
						fileSystemSpace.setMountedOn(split[4]);
					}
				}
				if (fileSystemSpace != null) {
					fileSystemSpaceList.add(fileSystemSpace);
				}
				return null;
			}
		};
		shellWorker.start();
		shellWorker.synchronize();
		return fileSystemSpaceList;
	}
}
