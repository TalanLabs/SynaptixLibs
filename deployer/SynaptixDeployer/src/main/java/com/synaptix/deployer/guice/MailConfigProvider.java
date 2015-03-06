package com.synaptix.deployer.guice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Provider;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.model.IMailConfig;

public class MailConfigProvider implements Provider<IMailConfig> {

	private static final Log LOG = LogFactory.getLog(MailConfigProvider.class);

	private IMailConfig mailConfig;

	public MailConfigProvider() {
		super();

		initConfig();
	}

	private void initConfig() {
		try {
			LOG.info("Config -> use internal");
			Properties p = new Properties();

			try {
				File tempFile = File.createTempFile("tempFile", null);
				File propertyFile = new File(tempFile.getParent(), "deployerMail.properties");
				if (propertyFile.exists()) {
					FileInputStream inStream = new FileInputStream(propertyFile);
					p.load(inStream);
					inStream.close();
				}
			} catch (IOException e) {
				LOG.error(e, e);
			}
			mailConfig = ComponentFactory.getInstance().createInstance(IMailConfig.class);

			mailConfig.setDefaultSend(new Boolean(p.getProperty("defaultSend", "false")));
			mailConfig.setMailSmtpHost(p.getProperty("mailSmtpHost", "smtp.gmail.com"));
			mailConfig.setMailSmtpPort(Integer.parseInt(p.getProperty("mailSmtpPort", "465")));
			mailConfig.setLogin(p.getProperty("login", ""));
			mailConfig.setPassword(p.getProperty("password", "").toCharArray());
			mailConfig.setSenderMail(p.getProperty("senderMail", ""));
			String defaultReceivers = p.getProperty("defaultReceivers", "");
			if (defaultReceivers != null) {
				mailConfig.setDefaultReceivers(defaultReceivers.split(","));
			}

			mailConfig.setSSL(new Boolean(p.getProperty("ssl", "0")));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public IMailConfig get() {
		return mailConfig;
	}
}
