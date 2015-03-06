package com.synaptix.redpepper.client.controller.entry;

import com.synaptix.redpepper.client.ITestCase;

public interface ITestRunner {

	public void onPlay(String testName, ITestCase tCase);

	public void onPlayFixture(String value);
}
