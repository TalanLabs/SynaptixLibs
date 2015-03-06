package com.synaptix.user;

public abstract class UserNameStore {

	public abstract String[] getUserNames();
    
    public abstract void setUserNames(String[] names);
    
    public abstract void loadUserNames();
    
    public abstract void saveUserNames();
    
    public abstract boolean containsUserName(String name);
    
    public abstract void addUserName(String userName);
    
    public abstract void removeUserName(String userName);
    
}
