package com.synaptix.redpepper.automation.runner;

import com.synaptix.redpepper.automation.report.ConsoleReporter;

public class ConsoleRedPepperRunner extends RedPepperRunner {

	public static void main(String[] args) {
		// org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
		RedPepperRunner runner = new RedPepperRunner(new ConsoleReporter());
		try {
			runner.launch(Class.forName(args[0]));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
