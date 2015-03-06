package com.synaptix.swing.event;

import java.util.EventListener;

public interface TriageModelListener extends EventListener {

	public void triageChanged(TriageModelEvent e);
	
}
