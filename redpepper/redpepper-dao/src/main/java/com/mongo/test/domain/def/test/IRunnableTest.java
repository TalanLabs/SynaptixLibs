/**
 * 
 */
package com.mongo.test.domain.def.test;

import org.joda.time.LocalDateTime;

import com.mongo.test.domain.impl.test.TestResult;

/**
 * @author E413544
 * 
 */
public interface IRunnableTest {
	public TestResult getTestResult();

	public void setTestResult(TestResult testResult);

	/**
	 * This method can be called when starting the test, to save the execution start time.
	 */
	public void startExecution();

	/**
	 * This method must be called after the end of the test, to compute the execution time.
	 */
	public void stopExecution();

	public long getExecutionTime();

	public LocalDateTime getStartDateTime();
}
