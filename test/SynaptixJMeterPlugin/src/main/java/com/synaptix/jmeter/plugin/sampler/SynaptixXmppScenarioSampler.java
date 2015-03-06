package com.synaptix.jmeter.plugin.sampler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;

import com.synaptix.jmeter.plugin.sampler.packet.RequestJMeterServiceIQ;
import com.synaptix.jmeter.plugin.sampler.packet.ResultJMeterServiceIQ;
import com.synaptix.scenario.service.ScenarioHelper;
import com.synaptix.scenario.service.model.IScenario;
import com.synaptix.scenario.service.model.IScenarioAct;

public class SynaptixXmppScenarioSampler extends AbstractSampler {

	private static final long serialVersionUID = 240L;

	private static final Logger log = LoggingManager.getLoggerForClass();

	private static AtomicInteger classCount = new AtomicInteger(0);

	public static final String URL_SERVER = "urlServer";

	public static final String USERNAME = "username";

	public static final String PASSWORD = "password";

	public static final String FILE_PATH = "filePath";

	public SynaptixXmppScenarioSampler() {
		super();

		classCount.incrementAndGet();
	}

	public SampleResult sample(Entry entry) {
		SampleResult res = new SampleResult();
		boolean isOK = false;

		res.setSampleLabel("SynaptixXmmpScenarioSampler");

		XMPPConnection xmppConnection = null;

		res.sampleStart(); // Start timing
		try {
			log.info("Start XMPPConnection");

			xmppConnection = createXMPPConnection();
			xmppConnection.connect();
			xmppConnection.login(getUsername(), getPassword());

			log.info("Login " + getUsername());

			IScenario scenario = ScenarioHelper.restaureScenario(new File(getFilePath()));

			long current = System.currentTimeMillis();

			send(xmppConnection, scenario.getScenarioActList(), res);

			res.setIdleTime(System.currentTimeMillis() - current);
			res.setResponseCodeOK();
			res.setResponseMessage("OK");
			isOK = true;
		} catch (Exception ex) {
			log.error("", ex);
			res.setResponseCode("500");
			res.setResponseMessage(ex.toString());
		} finally {
			if (xmppConnection != null && xmppConnection.isConnected()) {
				try {
					log.info("Stop XMPPConnection");
					xmppConnection.disconnect();
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
		}

		res.setSuccessful(isOK);

		return res;
	}

	private XMPPConnection createXMPPConnection() {
		SmackConfiguration.setPacketReplyTimeout(60000);
		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(getUrlServer());
		connectionConfiguration.setCompressionEnabled(false);
		connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		connectionConfiguration.setVerifyChainEnabled(false);
		connectionConfiguration.setVerifyRootCAEnabled(false);
		connectionConfiguration.setDebuggerEnabled(false);
		connectionConfiguration.setSelfSignedCertificateEnabled(true);
		connectionConfiguration.setSASLAuthenticationEnabled(true);
		return new XMPPConnection(connectionConfiguration);
	}

	public String getUrlServer() {
		return this.getPropertyAsString(URL_SERVER);
	}

	public void setUrlServer(String urlServer) {
		this.setProperty(URL_SERVER, urlServer);
	}

	public String getUsername() {
		return this.getPropertyAsString(USERNAME);
	}

	public void setUsername(String username) {
		this.setProperty(USERNAME, username);
	}

	public String getPassword() {
		return this.getPropertyAsString(PASSWORD);
	}

	public void setPassword(String password) {
		this.setProperty(PASSWORD, password);
	}

	public String getFilePath() {
		return this.getPropertyAsString(FILE_PATH);
	}

	public void setFilePath(String filePath) {
		this.setProperty(FILE_PATH, filePath);
	}

	private void send(XMPPConnection xmppConnection, List<IScenarioAct> scenarioActs, SampleResult sampleResult) throws Exception {
		if (scenarioActs != null && !scenarioActs.isEmpty()) {
			ExecutorService executorService = Executors.newFixedThreadPool(scenarioActs.size());

			List<Callable<SampleResult>> tasks = new ArrayList<Callable<SampleResult>>();
			for (IScenarioAct scenarioAct : scenarioActs) {
				tasks.add(new ScenarioActCallable(xmppConnection, scenarioAct));
			}

			List<Future<SampleResult>> futures = executorService.invokeAll(tasks);
			for (Future<SampleResult> future : futures) {
				SampleResult sr2 = future.get();
				sampleResult.addSubResult(sr2);
			}
		}
	}

	private final class ScenarioActCallable implements Callable<SampleResult> {

		private final XMPPConnection xmppConnection;

		private final IScenarioAct scenarioAct;

		public ScenarioActCallable(XMPPConnection xmppConnection, IScenarioAct scenarioAct) {
			super();
			this.xmppConnection = xmppConnection;
			this.scenarioAct = scenarioAct;
		}

		@Override
		public SampleResult call() throws Exception {
			log.info("Start Scenario Act " + scenarioAct.getName());

			try {
				Thread.sleep(scenarioAct.getStartTime());
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			SampleResult res = new SampleResult();
			boolean isOK = false;
			res.sampleStart();
			res.setSampleLabel(scenarioAct.getName());
			PacketCollector collector = null;
			try {
				RequestJMeterServiceIQ requestJMeterServiceIQ = new RequestJMeterServiceIQ(scenarioAct.getXMLExtension());
				String currentID = requestJMeterServiceIQ.getPacketID();
				collector = xmppConnection.createPacketCollector(new AndFilter(new PacketTypeFilter(ResultJMeterServiceIQ.class), new PacketIDFilter(currentID)));

				log.debug("Send packet");
				xmppConnection.sendPacket(requestJMeterServiceIQ);

				ResultJMeterServiceIQ result = (ResultJMeterServiceIQ) collector.nextResult(60 * 1000);
				if (result == null) {
					throw new Exception();
				}

				log.debug("Result packet");

				res.setHeadersSize(requestJMeterServiceIQ.toXML().getBytes().length);
				res.setResponseData(requestJMeterServiceIQ.toXML().getBytes());
				res.setDataType(SampleResult.BINARY);
				res.setBytes(result.toXML().getBytes().length);
				res.setResponseCodeOK();
				res.setResponseMessage("OK");

				isOK = true;
			} catch (Exception e) {
				log.error("", e);
				res.setResponseCode("500");
				res.setResponseMessage(e.toString());
			} finally {
				if (collector != null) {
					collector.cancel();
				}
			}

			res.sampleEnd();

			res.setSuccessful(isOK);

			// Childrens
			// send(xmppConnection, scenarioActs);

			log.info("End Scenario Act " + scenarioAct.getName());

			return res;
		}
	}
}
