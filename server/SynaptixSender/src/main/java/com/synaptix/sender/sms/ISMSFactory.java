package com.synaptix.sender.sms;

public interface ISMSFactory {

	public abstract String sendSMS(String phoneNumber, String text, boolean ar)
			throws SMSException;

}
