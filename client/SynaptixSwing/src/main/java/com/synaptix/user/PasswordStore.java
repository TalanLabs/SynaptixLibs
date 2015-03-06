package com.synaptix.user;

public abstract class PasswordStore {

	public abstract void set(String username, char[] password);

	public abstract char[] get(String username);

}
