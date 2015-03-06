package com.synaptix.toolkits.thread;

import java.util.ArrayList;
import java.util.List;

public class SpoolableStack implements Spoolable {
	List<Object> stack;
	
	public SpoolableStack(){
		stack = new ArrayList<Object>();
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
