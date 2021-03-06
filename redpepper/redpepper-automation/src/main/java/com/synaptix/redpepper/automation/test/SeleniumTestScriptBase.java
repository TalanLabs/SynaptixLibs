package com.synaptix.redpepper.automation.test;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.repository.WebRepository;

/**
 * abstraction for test cases dealing with selenium
 * 
 * @author skokaina
 * 
 * @param <D>
 * @param <W>
 */
public abstract class SeleniumTestScriptBase<D extends SynchronizedDriver, W extends WebRepository<?>> extends TestScriptBase<D, W> {

}
