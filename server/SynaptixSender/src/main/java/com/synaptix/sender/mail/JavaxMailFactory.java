package com.synaptix.sender.mail;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.smtp.SMTPSSLTransport;
import com.synaptix.sender.Attachment;

public class JavaxMailFactory implements IMailFactory {

	private static final Log log = LogFactory.getLog(JavaxMailFactory.class);

	private String smtpHost;

	private Integer smtpPort;

	private boolean useSmtpAuth;

	private String smtpLogin;

	private String smptPassword;

	private boolean useSSL;

	public JavaxMailFactory(String smtpHost) {
		this(smtpHost, null, false);
	}

	public JavaxMailFactory(String smtpHost, Integer smtpPort, boolean useSSL) {
		this(smtpHost, smtpPort, false, null, null, useSSL);
	}

	public JavaxMailFactory(String smtpHost, String smtpLogin, String smptPassword) {
		this(smtpHost, null, true, smtpLogin, smptPassword, false);
	}

	public JavaxMailFactory(String smtpHost, Integer smtpPort, String smtpLogin, String smptPassword, boolean useSSL) {
		this(smtpHost, smtpPort, true, smtpLogin, smptPassword, useSSL);
	}

	public JavaxMailFactory(String smtpHost, Integer smtpPort, boolean useSmtpAuth, String smtpLogin, String smptPassword, boolean useSSL) {
		super();
		this.smtpHost = smtpHost;
		this.useSmtpAuth = useSmtpAuth;
		this.smtpLogin = smtpLogin;
		this.smptPassword = smptPassword;
		this.smtpPort = smtpPort;
		this.useSSL = useSSL;
	}

	@Override
	public void sendMail(String from, String[] tos, String[] bccs, String[] ccs, String subject, String body, Attachment[] attachments) throws Exception {
		log.info("Create mail message from " + from + " tos " + Arrays.toString(tos) + " bccs " + Arrays.toString(bccs) + " ccs " + Arrays.toString(ccs) + " subject " + subject);
		log.info("Attachments " + (attachments != null ? attachments.length : 0));

		Session session = createSession(from);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));

		if (tos != null) {
			for (String to : tos) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}
		}
		if (bccs != null) {
			for (String bcc : bccs) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			}
		}
		if (ccs != null) {
			for (String cc : ccs) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
			}
		}

		if (subject != null) {
			message.setSubject(subject);
		}

		// message.setHeader("User-Agent",
		// "Thunderbird 2.0.0.9 (Windows/20071031)");
		// TODO utile ?
		// message.setHeader("To",tos[0].);

		Multipart mp = new MimeMultipart();

		if (body != null) {
			MimeBodyPart textBody = new MimeBodyPart();
			textBody.setContent(body, "text/plain; charset=ISO-8859-1; format=flowed");
			mp.addBodyPart(textBody);
		}

		if (attachments != null) {
			for (Attachment attachment : attachments) {
				MimeBodyPart jointPiece = new MimeBodyPart();
				jointPiece.setDataHandler(new DataHandler(new ByteArrayDataSource(new FileInputStream(attachment.getFile()), attachment.getType())));
				jointPiece.setFileName(attachment.getFileName());

				mp.addBodyPart(jointPiece);
			}
		}

		message.setContent(mp);

		log.info("Send mail message to " + Arrays.toString(tos));
		if (useSSL) {
			SMTPSSLTransport transport = (SMTPSSLTransport) session.getTransport("smtps");
			transport.connect(smtpHost, smtpPort, smtpLogin, smptPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} else {
			Transport transport = session.getTransport();
			transport.connect(smtpHost, smtpPort, smtpLogin, smptPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			// Transport.send(message, message.getAllRecipients());
		}
	}

	private Session createSession(String from) {
		Properties props = new Properties();
		props.setProperty("mail.debug", String.valueOf(log.isDebugEnabled()));
		props.setProperty("mail.from", from);

		Authenticator auth = null;
		if (useSSL) {
			props.setProperty("mail.transport.protocol", "smtps");
			props.setProperty("mail.smtps.host", smtpHost);
			if (smtpPort != null) {
				props.setProperty("mail.smtps.port", Integer.toString(smtpPort));
			}
			props.setProperty("mail.smtps.sendpartial", "false");
			// props.setProperty("mail.smtp.dsn.notify", "FAILURE,DELAY");
			if (useSmtpAuth) {
				props.setProperty("mail.smtps.auth", "true");
				auth = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtpLogin, smptPassword);
					}
				};
			}
		} else {
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.host", smtpHost);
			if (smtpPort != null) {
				props.setProperty("mail.smtp.port", Integer.toString(smtpPort));
			}
			props.setProperty("mail.smtp.sendpartial", "false");
			// props.setProperty("mail.smtp.dsn.notify", "FAILURE,DELAY");

			if (useSmtpAuth) {
				props.setProperty("mail.smtp.auth", "true");
				auth = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtpLogin, smptPassword);
					}
				};
			}
		}
		Session session = Session.getInstance(props, auth);
		session.setDebug(false);
		return session;
	}
}
