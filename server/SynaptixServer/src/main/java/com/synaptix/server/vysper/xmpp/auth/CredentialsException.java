package com.synaptix.server.vysper.xmpp.auth;

public class CredentialsException extends RuntimeException {

	private static final long serialVersionUID = 1965227549307401287L;

	public CredentialsException(String code) {
		super(code);
	}
}
