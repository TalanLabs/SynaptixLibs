package com.synaptix.toolkits.thread;

public class FilteringSpoolableStack extends SpoolableStack {
	private Filter filter;
	
	public FilteringSpoolableStack(Filter filter){
		super();
		this.filter = filter;
	}
	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.thread.Spoolable#push(java.lang.Object)
	 */
	public void push(Object data) {
		if (filter.filter(data)){
			doPush(data);
		}
	}

	public void doPush(Object data){
		System.out.println("DoPush!");
		super.push(data);
	}
	/* (non-Javadoc)
	 * @see com.synaptix.toolkits.thread.Spoolable#pop()
	 */

	public static interface Filter {
		public boolean filter(Object data);
	}
}
