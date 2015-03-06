package com.synaptix.core.event;

import java.util.EventListener;

public interface ViewDockableStateListener extends EventListener {

	public void viewDockableStateChanged(ViewDockableStateEvent e);

}
