package com.synaptix.pmgr.core.lib;

import com.synaptix.pmgr.core.apis.Channel;
import com.synaptix.pmgr.core.apis.Engine;

public abstract class AbstractChannel implements Channel {
	private Engine engine;
	
	private String name;
	public AbstractChannel(String name, Engine engine){
		this.name = name;
		this.engine = engine;
	}
	/* (non-Javadoc)
	 * @see com.synaptix.pmgr2.apis.Channel#getName()
	 */
	public String getName() {
		return name;
	}

	public Engine getProcessEngine(){
		return engine;
	}

	public void activate(){
		
	}

}
