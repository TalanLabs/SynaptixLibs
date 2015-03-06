package com.synaptix.redpepper.client.controller.entry;

import com.synaptix.redpepper.client.view.entry.IBackendTestView;

public interface IBackendTestController {

	void loadTests();

	void runTest(IBackendTestView backendView, String backendTest);

}
