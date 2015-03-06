package com.synaptix.user;

public interface UserListener {

	public enum Result {
		NONE, RESET_ALL, RESET_PASSWORD
	}

	public Result errorAuthentification(UserException e);

}
