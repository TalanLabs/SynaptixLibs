/*
 * Fichier : SpoolableStack.java
 * Projet  : stxToolkit
 * Date    : 21 juil. 2004
 * Auteur  : sps
 *
 */
package com.synaptix.toolkits.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * Projet GPSTrains
 * 
 * @author sps
 */
public class SpoolableStack implements Spoolable {
	List stack;
	
	public SpoolableStack(){
		stack = new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.thread.Spoolable#push(java.lang.Object)
	 */
	public void push(Object data) {
		synchronized (stack) {
			stack.add(data);
		}
		synchronized (this) {
			notifyAll();
		}
	}

	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.thread.Spoolable#pop()
	 */
	public Object pop() {
		synchronized (stack) {
			if (stack.size() > 0)
				return stack.remove(0);
		}
		return null;
	}

}
