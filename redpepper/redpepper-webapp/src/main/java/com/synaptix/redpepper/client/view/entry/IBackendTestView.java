package com.synaptix.redpepper.client.view.entry;

public interface IBackendTestView {

	void setScript(String script);

	public String getScript();

	void setResult(String result);

	void startLoading();

	void endLoading();

}
