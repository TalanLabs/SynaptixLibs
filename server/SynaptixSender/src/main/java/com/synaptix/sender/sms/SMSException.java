package com.synaptix.sender.sms;

public class SMSException extends Exception {

	private static final long serialVersionUID = 8803568571654609982L;

	public enum Type {
		ErreurSysteme, ErreurSMS
	}

	private Type type;

	private String code;

	private String description;

	public SMSException(Exception e) {
		super(e);
		type = Type.ErreurSysteme;
	}

	public SMSException(String code, String description) {
		super(new Exception(code));
		type = Type.ErreurSMS;

		this.code = code;
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
