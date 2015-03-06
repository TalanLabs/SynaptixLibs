package com.synaptix.core.event;

import java.util.EventObject;

public class ViewOptionStateEvent extends EventObject {

	private static final long serialVersionUID = 8214743507134551093L;

	public enum State {
		OPENED, CLOSED, HIDDEN, SHOW
	};

	private State state;

	private boolean isAdjusting;

	public ViewOptionStateEvent(Object source, State state, boolean isAdjusting) {
		super(source);

		this.state = state;
		this.isAdjusting = isAdjusting;
	}

	public State getState() {
		return state;
	}

	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}

	public String toString() {
		String properties = " source=" + getSource() + " state= " + state
				+ " isAdjusting= " + isAdjusting + " ";
		return this.getClass().getName() + " [" + properties + "]";
	}
}
