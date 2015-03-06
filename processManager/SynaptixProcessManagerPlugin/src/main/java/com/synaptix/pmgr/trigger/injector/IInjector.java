package com.synaptix.pmgr.trigger.injector;

import java.io.File;

import com.synaptix.pmgr.trigger.message.IImportFlux;

/**
 * An interface for injectors
 * 
 * @author Nicolas P
 * 
 */
public interface IInjector<M extends IImportFlux> {

	/**
	 * The name of the injector (and also agent)
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Inject a new singular message (file is optional)
	 * 
	 * @param content
	 * @param file
	 */
	public void injectMessage(String content, File file);

	/**
	 * Delay of refresh
	 */
	public long getDelay();

}
