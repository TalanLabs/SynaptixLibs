package com.synaptix.deployer.model;

import com.synaptix.entity.IEntity;

public interface IMailConfig extends IEntity {

	/**
	 * Are the mails to be send by default?
	 */
	public boolean isDefaultSend();

	public void setDefaultSend(boolean defaultSend);

	public String getMailSmtpHost();

	public void setMailSmtpHost(String mailSmtpHost);

	public Integer getMailSmtpPort();

	public void setMailSmtpPort(Integer mailSmtpPort);

	public String getLogin();

	public void setLogin(String login);

	public char[] getPassword();

	public void setPassword(char[] password);

	public String getSenderMail();

	public void setSenderMail(String senderMail);

	public String[] getDefaultReceivers();

	public void setDefaultReceivers(String[] defaultReceivers);

	public boolean isSSL();

	public void setSSL(boolean ssl);

}
