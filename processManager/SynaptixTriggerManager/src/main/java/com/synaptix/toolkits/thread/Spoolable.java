package com.synaptix.toolkits.thread;

public interface Spoolable {
	public void push(Object data);
	public Object pop(); 
}
