package com.synaptix.sender.mail;

import com.synaptix.sender.Attachment;

public interface IMailFactory {

	public abstract void sendMail(String from, String[] tos, String[] bccs,
			String[] ccs, String subject, String body, Attachment[] attachments)
			throws Exception;

}
