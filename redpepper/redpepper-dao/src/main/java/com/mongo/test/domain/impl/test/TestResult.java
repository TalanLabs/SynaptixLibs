package com.mongo.test.domain.impl.test;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;

@Entity(value = "test")
@Embedded
public class TestResult {
	private final boolean isSuccess;
	private String message;
	// expected to add
	private ResultKind resultKind;

	/**
	 * Used for Show tests, to return an object or a list of objects. Then, the reporter can display its content properly.
	 */
	private Object resultObject;

	/**
	 * This constructor can be used in case of success.
	 */
	public TestResult() {
		this("OK", ResultKind.SUCCESS);
	}

	/**
	 * This constructor will create a TestResult of kind FAILURE (i.e. a test has failed, but it is not a technical error).
	 * 
	 * @param failureMessage
	 */
	public TestResult(String failureMessage) {
		this(failureMessage, ResultKind.FAILURE);
	}

	public TestResult(String message, ResultKind resultKind) {
		this.message = message;
		this.isSuccess = resultKind.equals(ResultKind.SUCCESS) || resultKind.equals(ResultKind.INFO);
		this.setResultKind(resultKind);
	}

	public boolean isSuccess() {
		return ResultKind.SUCCESS.equals(resultKind) || ResultKind.INFO.equals(resultKind);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResultKind getResultKind() {
		return resultKind;
	}

	public void setResultKind(ResultKind resultKind) {
		this.resultKind = resultKind;
	}

	public enum ResultKind {
		/**
		 * Test failure (red)
		 */
		FAILURE,
		/**
		 * Technical error (yellow)
		 */
		ERROR,
		/**
		 * Test success (green)
		 */
		SUCCESS,
		/**
		 * Technical success, or info (blue)
		 */
		INFO
	}
}