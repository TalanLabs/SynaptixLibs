package com.synaptix.pmgr.registry;

public class BaseServiceProvider implements ServiceProvider {
	private String uri;
	private boolean isLocal;

	public BaseServiceProvider(String uri, boolean isLocal) {
		this.uri = uri;
		this.isLocal = isLocal;
	}

	// public BaseServiceProvider(String uri){
	// this.uri = uri;
	// this.isLocal = true;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.ServiceProvider#attach(com.synaptix.registry.Registry
	 * , java.lang.String)
	 */
	public void attach(Registry registry, String service) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.ServiceProvider#detach(com.synaptix.registry.Registry
	 * , java.lang.String)
	 */
	public void detach(Registry registry, String service) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.ServiceProvider#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.ServiceProvider#isLocal()
	 */
	public boolean isLocal() {
		return isLocal;
	}

}
