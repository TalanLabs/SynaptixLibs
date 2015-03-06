package com.synaptix.widget.core.controller;

import com.synaptix.client.view.IView;

public abstract class AbstractController implements IController {

	@Override
	public String getName() {
		return getId();
	}

	@Override
	public String getId() {
		return this.getClass().getName();
	}

	@Override
	public void start() {
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public IView getView() {
		return null;
	}
}
