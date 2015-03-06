package test;

import com.synaptix.pmgr.core.apis.Engine;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

public class SimplePrinterAgent implements Agent{
	protected String name;
	public SimplePrinterAgent(String nm){
		name = nm;
	}
	public void work(Object message, Engine processEngine) {
		System.out.println(name+" says Hello Mr "+((String)message)+"!");
	}
}
