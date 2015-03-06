package com.synaptix.user;

public interface IUserAuth<T> {

	public T authenticate(String login, char[] password) throws UserException;
	
}
