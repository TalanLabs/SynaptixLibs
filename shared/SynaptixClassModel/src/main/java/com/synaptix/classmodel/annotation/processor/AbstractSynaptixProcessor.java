package com.synaptix.classmodel.annotation.processor;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;

public abstract class AbstractSynaptixProcessor extends AbstractProcessor {

	private ProcessingEnvironment environment;

	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);
		this.environment = environment;
	}

	public ProcessingEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Prints a message.
	 * 
	 * @param message
	 *            the message
	 */
	public void printMessage(String message) {
		getEnvironment().getMessager().printMessage(Kind.NOTE, message);
	}

	/**
	 * Prints an error.
	 * 
	 * @param message
	 *            the error message
	 */
	public void printError(String message) {
		getEnvironment().getMessager().printMessage(Kind.ERROR, message);
	}

	/**
	 * Prints an error.
	 * 
	 * @param t
	 *            the error message
	 */
	public void printError(Throwable t) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			printError(sw.toString());
		} catch (Exception e) {
			printError(e.getMessage());
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (Exception e) {
					printError(e.getMessage());
				}
			}
			if (pw != null) {
				try {
					pw.close();
				} catch (Exception e) {
					printError(e.getMessage());
				}
			}
		}
	}

	/**
	 * Utility method called after processing has started.
	 */
	public void onProcessingStarted() {
		printMessage(getClass().getName() + " started.");
	}

	/**
	 * Utility method called after the processing is finished.
	 */
	public void onProcessingCompleted() {
		printMessage(getClass().getName() + " finished.");
	}
}
