package com.synaptix.deployer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class SSHManager {

	private static final Log LOG = LogFactory.getLog(SSHManager.class);

	private volatile Boolean shouldRun;
	private volatile Boolean shouldYield;

	private final String strUserName;
	private final String strConnectionIP;
	private final int intConnectionPort;
	private final String strPassword;
	private final int intTimeOut;

	private final JSch jschSSHChannel;
	private Session sesConnection;
	private OutputStream consoleInput;
	private InputStream consoleOutput;
	private Thread reader;
	private List<ReaderListener> readerListenerList;

	private ChannelShell channel;

	public SSHManager(String userName, String password, String connectionIP) {

		shouldRun = true;
		shouldYield = false;

		jschSSHChannel = new JSch();

		File file = null;
		File parentFile = null;
		try {
			parentFile = File.createTempFile("temp", "").getParentFile();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		if (parentFile != null) {
			file = new File(parentFile, "knownHosts.data");
		} else {
			file = new File("knownHosts.data");
		}
		String knownHostsFilename = file.getAbsolutePath();
		LOG.debug("Known hosts file : " + knownHostsFilename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		try {
			jschSSHChannel.setKnownHosts(knownHostsFilename);
		} catch (JSchException e) {
			LOG.error("KNOWN HOSTS ERROR", e);
		}

		strUserName = userName;
		strPassword = password;
		strConnectionIP = connectionIP;
		intConnectionPort = 22;
		intTimeOut = 60000;
	}

	public final String connect() {
		String errorMessage = null;

		try {
			sesConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);
			sesConnection.setPassword(strPassword);

			initializeConnection();

			sesConnection.connect(intTimeOut);
			sesConnection.setDaemonThread(true);

			channel = (ChannelShell) sesConnection.openChannel("shell");
			channel.setPtySize(1024, 20, 1024, 20); // commands should be < 1024 char

			PipedInputStream inputStream = new PipedInputStream();
			consoleInput = new PipedOutputStream(inputStream);
			consoleOutput = inputStream;
			channel.setOutputStream(consoleInput);
			channel.setInputStream(consoleOutput);

			consoleOutput = channel.getInputStream();
			consoleInput = channel.getOutputStream();

			channel.connect();

			// Waiting for a good connection to go further
			boolean initialized = false;
			// reader.start();
			StringBuilder builder = new StringBuilder();
			while (!initialized) {
				// if (channel.isConnected()) {
				synchronized (this) {
					String line = read();
					if (StringUtils.isNotBlank(line)) {
						builder.append(line.replaceAll("\\r|\\n", ""));
						if (builder.toString().matches(".*\\[.*\\]\\$.*")) { // TODO : improve
							initialized = true;
						}
						fireLine(line);
					}
				}
				waitThread();
			}

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					while (shouldRun) {
						synchronized (jschSSHChannel) {
							try {
								String line = read();
								fireLine(line);
							} catch (IOException e) {
								LOG.error("IOException", e);
							}
						}
						waitThread();
					}
				}
			};
			reader = new Thread(runnable);
			reader.start();

		} catch (JSchException jschX) {
			errorMessage = jschX.getMessage();
			LOG.error("CONNECTION ERROR", jschX);
		} catch (IOException e) {
			errorMessage = e.getMessage();
			LOG.error("IOException", e);
		}

		return errorMessage;
	}

	protected void initializeConnection() {
		UserInfo ui = new StandardUserInfo() {

			private volatile Integer answ = null;

			@Override
			public void showMessage(String message) {
				JOptionPane.showMessageDialog(null, message);
			}

			@Override
			public boolean promptYesNo(final String message) {
				answ = null;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						Object[] options = { "yes", "no" };
						answ = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
					}
				});
				while (answ == null) {
					waitThread();
					// do nothing, wait for an answer
				}
				return answ == 0;
			}
		};

		sesConnection.setUserInfo(ui);
	}

	protected String logError(String errorMessage) {
		if (errorMessage != null) {
			LOG.error(String.format("%s : %s - %s", strConnectionIP, intConnectionPort, errorMessage));
		}
		return errorMessage;
	}

	protected String logWarning(String warnMessage) {
		if (warnMessage != null) {
			LOG.warn(String.format("%s : %s - %s", strConnectionIP, intConnectionPort, warnMessage));
		}
		return warnMessage;
	}

	public final String sendCommand(String command) {
		StringBuilder outputBuffer = new StringBuilder();

		synchronized (jschSSHChannel) {
			try {
				String line = read();
				fireLine(line);
				shouldYield = true;
				consoleInput.write((command + "\n").getBytes());
				consoleInput.flush();
				while (consoleOutput.available() <= 0) {
					waitThread();
					// wait for an answer
				}
				Boolean end = false;
				List<String> lastLineList = new LinkedList<String>();
				do {
					if (consoleOutput.available() > 0) { // TODO timeout
						byte[] a = new byte[1024];
						int i = consoleOutput.read(a, 0, 1024);
						if (i < 0) {
							break;
						}
						String str = new String(a, 0, i);
						lastLineList.add(str.replaceAll("\\n", "").replaceAll("\\r", ""));
						if (lastLineList.size() > 10) {
							lastLineList.remove(0);
						}
						outputBuffer.append(str);
						String lastLines = getLastLines(lastLineList);
						if (lastLines.matches(".*\\[.*\\]\\$.*")) {
							end = true;
						} else {
							end = false;
						}
					}
					if ((!sesConnection.isConnected()) || (!shouldRun)) {
						end = true;
					}
					waitThread();
				} while (!end);

			} catch (IOException ioX) {
				ioX.printStackTrace();
				logWarning(ioX.getMessage());
				return null;
			} finally {
				shouldYield = false;
			}
		}
		String result = outputBuffer.toString();
		fireLine(result);

		List<String> lineList = new ArrayList<String>();
		String[] lines = result.replaceAll("\\r", "").split("\n");
		Collections.addAll(lineList, lines);
		while ((lineList.size() > 0) && (command.startsWith(lineList.get(0)))) {
			command = command.substring(lineList.get(0).length());
			lineList.remove(0);
		}
		StringBuilder builder = new StringBuilder();
		Iterator<String> ite = lineList.iterator();
		boolean first = true;
		while (ite.hasNext()) {
			String line = ite.next();
			if (ite.hasNext()) { // isn't enough
				if (!first) {
					builder.append("\n");
				} else {
					first = false;
				}
				builder.append(line);
			}
		}
		result = builder.toString();

		// result = result.replaceFirst("^(.*(\\n|\\r)+)", "");
		// result = result.replaceFirst("((\\n|\\r)+.*)$", "");

		return result;
	}

	private final void waitThread() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread.yield();
	}

	private String getLastLines(List<String> lastLineList) {
		StringBuilder builder = new StringBuilder();
		for (String lastLine : lastLineList) {
			builder.append(lastLine);
		}
		return builder.toString();
	}

	public final void close() {
		synchronized (jschSSHChannel) {
			shouldRun = false;
			try {
				// consoleInput.write(("exit\n").getBytes());
				// consoleInput.flush();
				consoleOutput.close();
				consoleInput.close();
			} catch (IOException e) {
				LOG.error("IOException", e);
			}
			sesConnection.disconnect();
		}
	}

	private abstract class StandardUserInfo implements UserInfo {

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public boolean promptYesNo(String str) {
			return false;
		}

		@Override
		public String getPassphrase() {
			return null;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return false;
		}

		@Override
		public boolean promptPassword(String message) {
			return false;
		}

		@Override
		public void showMessage(String message) {
		}
	}

	private String read() throws IOException {
		StringBuilder outputBuffer = new StringBuilder();
		if (!shouldYield) {
			try {
				if (consoleOutput.available() > 0) {
					byte[] a = new byte[100];
					int i = consoleOutput.read(a, 0, 100);
					if (i > 0) {
						String str = new String(a, 0, i);
						outputBuffer.append(str);
					}
				}
			} catch (Exception e) {
				LOG.error("EXCEPTION", e);
			}
		}
		return outputBuffer.toString();
	}

	private void fireLine(String line) {
		if (StringUtils.isNotBlank(line)) {
			if ((readerListenerList != null) && (!readerListenerList.isEmpty())) {
				for (ReaderListener readerListener : readerListenerList) {
					readerListener.write(line);
				}
			}
		}
	}

	public final void addReaderListener(ReaderListener readerListener) {
		if (readerListener != null) {
			if (readerListenerList == null) {
				readerListenerList = new ArrayList<ReaderListener>();
			}
			readerListenerList.add(readerListener);
		}
	}

	public final boolean createFolder(String folder) throws JSchException, SftpException {
		ChannelSftp channel = (ChannelSftp) sesConnection.openChannel("sftp");
		channel.connect();
		channel.mkdir(folder);
		channel.disconnect();
		return true;
	}

	public final boolean createTemporaryFile(byte[] content, String filename) throws JSchException, SftpException {
		ChannelSftp channel = (ChannelSftp) sesConnection.openChannel("sftp");
		ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
		channel.connect();
		channel.put(inputStream, filename);
		channel.disconnect();
		return true;
	}

	public final boolean deleteFolder(String folder) throws JSchException, SftpException {
		ChannelSftp channel = (ChannelSftp) sesConnection.openChannel("sftp");
		channel.connect();
		channel.rmdir(folder);
		channel.disconnect();
		return true;
	}
}
