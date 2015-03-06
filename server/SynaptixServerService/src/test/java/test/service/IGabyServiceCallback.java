package test.service;

import com.synaptix.service.IServiceCallback;

public interface IGabyServiceCallback extends IServiceCallback {

	public abstract void publish(String message);

}
